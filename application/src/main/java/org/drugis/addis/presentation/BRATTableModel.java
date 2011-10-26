package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.VariableType;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.drugis.addis.entities.relativeeffect.Distribution;


public class BRATTableModel<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> extends AbstractTableModel {
	public static final int COLUMN_BR = 0;
	public static final int COLUMN_CRITERIA = 1;
	public static final int COLUMN_OUTCOME_TYPE = 2;
	public static final int COLUMN_BASELINE = 3;
	public static final int COLUMN_SUBJECT = 4;
	public static final int COLUMN_DIFFERENCE = 5;
	public static final int COLUMN_FOREST = 6;
	
	private static final long serialVersionUID = 4201230853343429062L;
	private final AnalysisType d_analysis;
	private final Alternative d_baseline;
	private final Alternative d_subject;

	public BRATTableModel(AnalysisType bean, Alternative baseline, Alternative subject) {
		d_analysis = bean;
		d_baseline = baseline;
		d_subject = subject;
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
		return d_analysis.getCriteria().size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == COLUMN_BR) {
			if (d_analysis.getCriteria().get(rowIndex) instanceof Endpoint) {
				return "Benefit";
			} else if (d_analysis.getCriteria().get(rowIndex) instanceof AdverseEvent) {
				return "Risk";
			}
		} else if (columnIndex == COLUMN_CRITERIA) {
			return d_analysis.getCriteria().get(rowIndex);
		} else if (columnIndex == COLUMN_OUTCOME_TYPE) {
			return d_analysis.getCriteria().get(rowIndex).getVariableType();
		} else if (columnIndex == COLUMN_BASELINE) {
			return getMeasurement(rowIndex, d_baseline);
		} else if (columnIndex == COLUMN_SUBJECT) {
			return getMeasurement(rowIndex, d_subject);
		} else if (columnIndex == COLUMN_DIFFERENCE) {
			return getDifference(rowIndex);
		}
		return "";
	}

	private Distribution getDifference(int rowIndex) {
		OutcomeMeasure om = d_analysis.getCriteria().get(rowIndex);
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
				return Object.class;
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
}