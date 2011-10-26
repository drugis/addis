package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.VariableType;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.forestplot.BinnedScale;
import org.drugis.addis.forestplot.ForestPlot;
import org.drugis.addis.forestplot.LinearScale;
import org.drugis.addis.forestplot.LogScale;
import org.drugis.addis.forestplot.Scale;
import org.drugis.common.Interval;


public class BRATTableModel<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> extends AbstractTableModel {
	public static class BRATForest {
		public final ConfidenceInterval ci;
		public final BinnedScale scale;
		public final Scale axis;
		
		public BRATForest(BinnedScale scale, ConfidenceInterval ci) {
			this.ci = ci;
			this.scale = scale;
			this.axis = null;
		}
		
		public BRATForest(BinnedScale scale, Scale axis) {
			this.ci = null;
			this.scale = scale;
			this.axis = axis;
		}
	}
	
	public static final int COLUMN_BR = 0;
	public static final int COLUMN_CRITERIA = 1;
	public static final int COLUMN_OUTCOME_TYPE = 2;
	public static final int COLUMN_BASELINE = 3;
	public static final int COLUMN_SUBJECT = 4;
	public static final int COLUMN_DIFFERENCE = 5;
	public static final int COLUMN_FOREST = 6;
	public static final int OFFSET_LOGAXIS = 0;
	public static final int OFFSET_LINAXIS = 1;
	
	private static final long serialVersionUID = 4201230853343429062L;
	private final AnalysisType d_analysis;
	private final Alternative d_baseline;
	private final Alternative d_subject;
	private LinearScale d_linScale;
	private LogScale d_logScale;
	private LinearScale d_linScaleFull;
	private LogScale d_logScaleFull;

	public BRATTableModel(AnalysisType bean, Alternative baseline, Alternative subject) {
		d_analysis = bean;
		d_baseline = baseline;
		d_subject = subject;
		calculateScales();
	}

	private void calculateScales() {
		List<ConfidenceInterval> logCIs = new ArrayList<ConfidenceInterval>();
		List<ConfidenceInterval> linCIs = new ArrayList<ConfidenceInterval>();
		for (OutcomeMeasure om : d_analysis.getCriteria()) {
			if (om.getVariableType() instanceof RateVariableType) {
				logCIs.add(getCI(getDifference(om)));
			} else if (om.getVariableType() instanceof ContinuousVariableType) {
				linCIs.add(getCI(getDifference(om)));
			}
		}
		if (!logCIs.isEmpty()) {
			d_logScale = new LogScale(ForestPlotPresentation.getRange(logCIs, AxisType.LOGARITHMIC));
		}
		if (!linCIs.isEmpty()) {
			d_linScale = new LinearScale(ForestPlotPresentation.getRange(linCIs, AxisType.LINEAR));
		}
		
		double hullLower = -1;
		double hullUpper = 1;
		if (d_logScale != null && d_linScale != null) {
			hullLower = Math.min(Math.log(d_logScale.getMin()), d_linScale.getMin());
			hullUpper = Math.max(Math.log(d_logScale.getMax()), d_linScale.getMax());
		} else if (d_logScale != null) {
			hullLower = Math.log(d_logScale.getMin());
			hullUpper = Math.log(d_logScale.getMax());
		} else if (d_linScale != null) {
			hullLower = d_linScale.getMin();
			hullUpper = d_linScale.getMax();
		}
		d_logScaleFull = new LogScale(new Interval<Double>(Math.exp(hullLower), Math.exp(hullUpper)));
		d_linScaleFull = new LinearScale(new Interval<Double>(hullLower, hullUpper));
	}

	public BRATTableModel(AnalysisType bean) {
		this(bean, bean.getAlternatives().get(0), bean.getAlternatives().get(1));
	}

	@Override
	public int getColumnCount() {
		return COLUMN_FOREST + 1;
	}

	@Override
	public int getRowCount() {
		return d_analysis.getCriteria().size() + 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// Special case for axes
		if (rowIndex >= d_analysis.getCriteria().size()) {
			if (columnIndex == COLUMN_FOREST) {
				final int offset = rowIndex - d_analysis.getCriteria().size();
				if (offset == OFFSET_LOGAXIS) {
					return new BRATForest(getBinnedScale(getFullLogScale()), getNiceLogScale());
				} else if (offset == OFFSET_LINAXIS) {
					return new BRATForest(getBinnedScale(getFullLinearScale()), getNiceLinearScale());
				}
			}
			return null;
		}
		
		// regular table cells
		if (columnIndex == COLUMN_BR) {
			if (d_analysis.getCriteria().get(rowIndex) instanceof Endpoint) {
				return "Benefit";
			} else if (d_analysis.getCriteria().get(rowIndex) instanceof AdverseEvent) {
				return "Risk";
			}
		} else if (columnIndex == COLUMN_CRITERIA) {
			return d_analysis.getCriteria().get(rowIndex);
		} else if (columnIndex == COLUMN_OUTCOME_TYPE) {
			return getVariableType(rowIndex);
		} else if (columnIndex == COLUMN_BASELINE) {
			return getMeasurement(rowIndex, d_baseline);
		} else if (columnIndex == COLUMN_SUBJECT) {
			return getMeasurement(rowIndex, d_subject);
		} else if (columnIndex == COLUMN_DIFFERENCE) {
			return getDifference(rowIndex);
		} else if (columnIndex == COLUMN_FOREST) {
			return new BRATForest(getBinnedScale(getScale(rowIndex)), getCI(getDifference(rowIndex)));
		}
		return "";
	}

	private BinnedScale getBinnedScale(Scale scale) {
		return new BinnedScale(scale, 1, ForestPlot.BARWIDTH);
	}

	private VariableType getVariableType(int rowIndex) {
		return d_analysis.getCriteria().get(rowIndex).getVariableType();
	}

	private ConfidenceInterval getCI(Distribution difference) {
		return new ConfidenceInterval(difference.getQuantile(0.5), difference.getQuantile(0.025), difference.getQuantile(0.975));
	}

	private Distribution getDifference(int rowIndex) {
		OutcomeMeasure om = d_analysis.getCriteria().get(rowIndex);
		return getDifference(om);
	}
	
	private Distribution getDifference(OutcomeMeasure om) {
		if (d_analysis instanceof StudyBenefitRiskAnalysis) {
			StudyBenefitRiskAnalysis sba = (StudyBenefitRiskAnalysis) d_analysis;
			BasicMeasurement baseMeas = sba.getStudy().getMeasurement(om, (Arm) d_baseline);
			BasicMeasurement subjMeas = sba.getStudy().getMeasurement(om, (Arm) d_subject);
			if (baseMeas instanceof BasicRateMeasurement) {
				return new BasicOddsRatio((RateMeasurement) baseMeas, (RateMeasurement) subjMeas).getDistribution();
			} else if (baseMeas instanceof BasicContinuousMeasurement) {
				return new BasicStandardisedMeanDifference((ContinuousMeasurement) baseMeas, (ContinuousMeasurement) subjMeas).getDistribution();
			}
		} else if (d_analysis instanceof MetaBenefitRiskAnalysis) {
			MetaBenefitRiskAnalysis mba = (MetaBenefitRiskAnalysis) d_analysis;
			return mba.getRelativeEffectDistribution(om, (DrugSet) d_baseline, (DrugSet) d_subject);
		}
		return null;
	}

	private Object getMeasurement(int rowIndex, Alternative a) {
		if (d_analysis instanceof StudyBenefitRiskAnalysis) {
			StudyBenefitRiskAnalysis sba = (StudyBenefitRiskAnalysis) d_analysis;
			return sba.getMeasurement(sba.getCriteria().get(rowIndex), (Arm) a);
		} else if (d_analysis instanceof MetaBenefitRiskAnalysis) {
			MetaBenefitRiskAnalysis mba = (MetaBenefitRiskAnalysis) d_analysis;
			return mba.getMeasurement(mba.getCriteria().get(rowIndex), (DrugSet) a);
		}
		throw new IllegalStateException("Unknown analysis type " + d_analysis.getClass().getSimpleName());
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
			case COLUMN_BR:	
				return "";
			case COLUMN_CRITERIA:
				return "Outcome";
			case COLUMN_OUTCOME_TYPE:
				return "Type";
			case COLUMN_BASELINE:
				return getAlternativeDescription(d_baseline);
			case COLUMN_SUBJECT:
				return getAlternativeDescription(d_subject);
			case COLUMN_DIFFERENCE:
				return "Difference (95% CI)";
			case COLUMN_FOREST:
				return "";
			default:
				return"";
		}
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
			case COLUMN_BR:	
				return String.class;
			case COLUMN_CRITERIA:
				return Variable.class;
			case COLUMN_OUTCOME_TYPE:
				return VariableType.class;
			case COLUMN_BASELINE:
			case COLUMN_SUBJECT:
			case COLUMN_DIFFERENCE:
				return Distribution.class;
			case COLUMN_FOREST:
				return BRATForest.class;
			default:
				return null;
		}
	}

	private String getAlternativeDescription(Alternative alternative) {
		if(alternative instanceof Arm) {
			StudyBenefitRiskAnalysis sba = (StudyBenefitRiskAnalysis) d_analysis;
			return sba.getStudy().getTreatment((Arm) alternative).getLabel();
		}
		return alternative.getLabel();
	}
	
	private Scale getScale(int rowIndex) {
		VariableType vt = getVariableType(rowIndex);
		if (vt instanceof ContinuousVariableType) {
			return d_linScaleFull;
		} else if (vt instanceof RateVariableType) {
			return d_logScaleFull;
		}
		throw new IllegalStateException("Unknown variable type " + vt.getClass().getSimpleName());
	}
	
	LinearScale getNiceLinearScale() {
		return d_linScale;
	}
	
	LogScale getNiceLogScale() {
		return d_logScale;
	}
	
	LinearScale getFullLinearScale() {
		return d_linScaleFull;
	}
	
	LogScale getFullLogScale() {
		return d_logScaleFull;
	}
}