/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.VariableType;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MeasurementSource.Listener;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.treatment.TreatmentCategorySet;
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
		public final VariableType vt;
		
		public BRATForest(BinnedScale scale, ConfidenceInterval ci, VariableType vt) {
			this.ci = ci;
			this.scale = scale;
			this.vt = vt;
			this.axis = null;
		}
		
		public BRATForest(BinnedScale scale, Scale axis, VariableType vt) {
			this.vt = vt;
			this.ci = null;
			this.scale = scale;
			this.axis = axis;
		}
	}

	public static class BRATDifference {

		private final OutcomeMeasure d_om;
		private final Distribution d_difference;

		public BRATDifference(OutcomeMeasure om, Distribution difference) {
			d_om = om;
			d_difference = difference;
		}

		public OutcomeMeasure getOutcomeMeasure() {
			return d_om;
		}

		public Distribution getDifference() {
			return d_difference;
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
	private final Alternative d_subject;
	private LinearScale d_linScale;
	private LogScale d_logScale;
	private LinearScale d_linScaleFull;
	private LogScale d_logScaleFull;

	public BRATTableModel(AnalysisType bean, Alternative subject) {
		d_analysis = bean;
		d_subject = subject;
		d_analysis.getMeasurementSource().addMeasurementsChangedListener(new Listener() {
			public void notifyMeasurementsChanged() {
				calculateScales();
				fireTableDataChanged();
			}
		});		
		calculateScales();
	}

	private void calculateScales() {
		List<ConfidenceInterval> logCIs = new ArrayList<ConfidenceInterval>();
		List<ConfidenceInterval> linCIs = new ArrayList<ConfidenceInterval>();
		for (OutcomeMeasure om : d_analysis.getCriteria()) {
			Distribution diff = getDifference(om);
			if (om.getVariableType() instanceof RateVariableType && diff != null) {
				logCIs.add(getCI(diff));
			} else if (om.getVariableType() instanceof ContinuousVariableType && diff != null) {
				linCIs.add(getCI(diff));
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
					return new BRATForest(getBinnedScale(getFullLogScale()), getNiceLogScale(), new ContinuousVariableType());
				} else if (offset == OFFSET_LINAXIS) {
					return new BRATForest(getBinnedScale(getFullLinearScale()), getNiceLinearScale(), new RateVariableType());
				}
			} else if (columnIndex == COLUMN_DIFFERENCE) {
				final int offset = rowIndex - d_analysis.getCriteria().size();
				if (offset == OFFSET_LOGAXIS && d_logScale != null) {
					return "<html>Log&nbsp;scale</html>";
				} else if (offset == OFFSET_LINAXIS && d_linScale != null) {
					return "<html>Linear&nbsp;scale</html>";
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
			return getMeasurement(rowIndex, getBaseline());
		} else if (columnIndex == COLUMN_SUBJECT) {
			return getMeasurement(rowIndex, getSubject());
		} else if (columnIndex == COLUMN_DIFFERENCE) {
			return getDifference(rowIndex);
		} else if (columnIndex == COLUMN_FOREST) {
			return getDifference(rowIndex) == null ? null :
					new BRATForest(getBinnedScale(getScale(rowIndex)), getCI(getDifference(rowIndex).getDifference()), getVariableType(rowIndex));
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

	private BRATDifference getDifference(int rowIndex) {
		OutcomeMeasure om = d_analysis.getCriteria().get(rowIndex);
		Distribution difference = getDifference(om);
		return difference == null ? null : new BRATDifference(om, difference);
	}
	
	private Distribution getDifference(OutcomeMeasure om) {
		if (d_analysis instanceof StudyBenefitRiskAnalysis) {
			StudyBenefitRiskAnalysis sba = (StudyBenefitRiskAnalysis) d_analysis;
			return sba.getRelativeEffectDistribution(om, (Arm) getSubject());
		} else if (d_analysis instanceof MetaBenefitRiskAnalysis) {
			MetaBenefitRiskAnalysis mba = (MetaBenefitRiskAnalysis) d_analysis;
			return mba.getRelativeEffectDistribution(om, (TreatmentCategorySet) getSubject());
		}
		return null;
	}

	private Object getMeasurement(int rowIndex, Alternative a) {
		if (d_analysis instanceof StudyBenefitRiskAnalysis && rowIndex < d_analysis.getCriteria().size()) {
			StudyBenefitRiskAnalysis sba = (StudyBenefitRiskAnalysis) d_analysis;
			return sba.getMeasurement(sba.getCriteria().get(rowIndex), (Arm) a);
		} else if (d_analysis instanceof MetaBenefitRiskAnalysis && rowIndex < d_analysis.getCriteria().size()) {
			MetaBenefitRiskAnalysis mba = (MetaBenefitRiskAnalysis) d_analysis;
			return mba.getMeasurement(mba.getCriteria().get(rowIndex), (TreatmentCategorySet) a);
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
				return getAlternativeDescription(getBaseline());
			case COLUMN_SUBJECT:
				return getAlternativeDescription(getSubject());
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
				return Distribution.class;
			case COLUMN_DIFFERENCE:
				return BRATDifference.class;
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

	public Alternative getBaseline() {
		return d_analysis.getBaseline();
	}

	public Alternative getSubject() {
		return d_subject;
	}
}