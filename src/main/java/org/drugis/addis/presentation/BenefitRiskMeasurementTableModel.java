package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.ContinuousMeasurementEstimate;
import org.drugis.addis.entities.relativeeffect.LogContinuousMeasurementEstimate;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;

@SuppressWarnings("serial")
public class BenefitRiskMeasurementTableModel extends AbstractTableModel {
	
	protected BenefitRiskAnalysis d_br;
//	private PresentationModelFactory d_pmf;

	public BenefitRiskMeasurementTableModel(BenefitRiskAnalysis br, PresentationModelFactory pmf) {
		d_br = br;
//		d_pmf = pmf;
	}

	public int getColumnCount() {
		return d_br.getOutcomeMeasures().size()+1;
	}

	public int getRowCount() {
		return d_br.getDrugs().size();
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public String getColumnName(int index) {
		if (index == 0) {
			return "Alternative";
		}
		return d_br.getOutcomeMeasures().get(index-1).toString();	
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return d_br.getDrugs().get(rowIndex).getName();
		}

		RelativeEffect<? extends Measurement> relativeEffect = d_br.getRelativeEffect(d_br.getDrugs().get(rowIndex), d_br.getOutcomeMeasures().get(columnIndex-1));
		if (relativeEffect == null)
			return "N/A";
		
		Double mean = relativeEffect.getRelativeEffect();
		Double error = relativeEffect.getError();
		
		if (relativeEffect.getAxisType() == AxisType.LOGARITHMIC) {
			return new LogContinuousMeasurementEstimate(Math.log(mean), error);
		} else {
			return new ContinuousMeasurementEstimate(mean, error);
		}
	}

}
