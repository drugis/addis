package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class OddsRatioTableModel extends AbstractTableModel {
	Study d_study;
	Endpoint d_endpoint;
	PresentationModelManager d_pmm;
	
	public OddsRatioTableModel(Study s, Endpoint e, PresentationModelManager pmm) {
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
		// TODO Auto-generated method stub
		if (row == col) {
			return d_pmm.getModel(d_study.getPatientGroups().get(row));
		}
		
		//Measurement denominator = s.getMeasurement(e, s.getPatientGroups().get(0));
		//Measurement numerator = s.getMeasurement(e, s.getPatientGroups().get(1));
		//getOddsRatio(denominator, numerator);
		return null;
	}
}
