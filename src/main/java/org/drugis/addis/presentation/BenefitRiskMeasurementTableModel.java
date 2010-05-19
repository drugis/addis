package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.ContinuousMeasurementEstimate;
import org.drugis.addis.entities.LogContinuousMeasurementEstimate;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Distribution.AxisType;

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
		
		Double mean = relativeEffect.getMedian();
		Double error = relativeEffect.getSigma();
		
		if (relativeEffect.getAxisType() == org.drugis.addis.entities.Distribution.AxisType.LOGARITHMIC) {
			return new LogContinuousMeasurementEstimate(Math.log(mean), error);
		} else {
			return new ContinuousMeasurementEstimate(mean, error);
		}
	}

}
