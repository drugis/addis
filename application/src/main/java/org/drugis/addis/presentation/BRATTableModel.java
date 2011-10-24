package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;


public class BRATTableModel<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> extends AbstractTableModel {
	private static final long serialVersionUID = 4201230853343429062L;
	private final AnalysisType d_analysis;

	public BRATTableModel(AnalysisType bean) {
		d_analysis = bean;
		
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public int getRowCount() {
		int rows = 0;
		for (OutcomeMeasure om : d_analysis.getCriteria()) {
			if (om instanceof AdverseEvent || om instanceof Endpoint) {
				++rows;
			}
		}
		return rows;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			if (d_analysis.getCriteria().get(rowIndex) instanceof Endpoint) {
				return "Benefit";
			} else if (d_analysis.getCriteria().get(rowIndex) instanceof AdverseEvent) {
				return "Risk";
			}
		} else if (columnIndex == 1) {
			return d_analysis.getCriteria().get(rowIndex);
		} else if (columnIndex == 2) {
			if (d_analysis instanceof StudyBenefitRiskAnalysis) {
				StudyBenefitRiskAnalysis sba = (StudyBenefitRiskAnalysis) d_analysis;
				return sba.getStudy().getMeasurement(sba.getCriteria().get(rowIndex), sba.getAlternatives().get(rowIndex));
			} else if (d_analysis instanceof MetaBenefitRiskAnalysis) {
				MetaBenefitRiskAnalysis mba = (MetaBenefitRiskAnalysis) d_analysis;
				return mba.getMeasurement(mba.getAlternatives().get(rowIndex), mba.getCriteria().get(rowIndex));
			}
		} 
		return "";
//		throw new ArrayIndexOutOfBoundsException();
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
			case 0:	
				return "";
			case 1:
				return "Outcome";
			case 2:
				return d_analysis.getAlternatives().get(0).getLabel();
			case 3:
				return d_analysis.getAlternatives().get(1).getLabel();
			case 4:
				return "Risk difference per 10.000 person years";
			case 5:
				return "Risk difference forest plot";
			default:
				return"";
		}
	}
}