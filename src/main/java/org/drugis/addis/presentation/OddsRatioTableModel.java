package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class OddsRatioTableModel extends AbstractTableModel {
	private Study d_study;
	private Endpoint d_endpoint;
	private PresentationModelFactory d_pmm;
	
	public OddsRatioTableModel(Study s, Endpoint e, PresentationModelFactory pmm) {
		d_study = s;
		d_endpoint = e;
		d_pmm = pmm;
	}

	private OddsRatio getOddsRatio(Measurement denominator, Measurement numerator) {
		return new OddsRatio((RateMeasurement)denominator,
		(RateMeasurement)numerator);
	}

	public int getColumnCount() {
		return d_study.getPatientGroups().size();
	}

	public int getRowCount() {
		return d_study.getPatientGroups().size();
	}

	public Object getValueAt(int row, int col) {
		if (row == col) {
			return d_pmm.getModel(d_study.getPatientGroups().get(row));
		}
		
		Measurement denominator = d_study.getMeasurement(d_endpoint, d_study.getPatientGroups().get(row));
		Measurement numerator = d_study.getMeasurement(d_endpoint, d_study.getPatientGroups().get(col));
		return d_pmm.getModel(getOddsRatio(denominator, numerator));
	}
}
