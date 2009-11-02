package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class OddsRatioTableModel extends AbstractTableModel implements RatioTableModel {
	private Study d_study;
	private Endpoint d_endpoint;
	private PresentationModelFactory d_pmf;
	
	public OddsRatioTableModel(Study s, Endpoint e, PresentationModelFactory pmf) {
		d_study = s;
		d_endpoint = e;
		d_pmf = pmf;
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
			return d_pmf.getModel(d_study.getPatientGroups().get(row));
		}
		
		Measurement denominator = d_study.getMeasurement(d_endpoint, d_study.getPatientGroups().get(row));
		Measurement numerator = d_study.getMeasurement(d_endpoint, d_study.getPatientGroups().get(col));
		return d_pmf.getModel(getOddsRatio(denominator, numerator));
	}
	
	/**
	 * @see org.drugis.addis.presentation.RatioTableModel#getDescriptionAt(int, int)
	 */
	public String getDescriptionAt(int row, int col) {
		if (row == col) {
			return null;
		}
		return "\"" + getPatientGroupLabel(col) +
			"\" relative to \"" + getPatientGroupLabel(row) + "\"";
	}

	private String getPatientGroupLabel(int index) {
		return d_pmf.getLabeledModel(d_study.getPatientGroups().get(index)).getLabelModel().getString();
	}
	
	public String getTitle() {
		return "Odds-Ratio Table";
	}
	
	public String getDescription() {
		return getTitle() + " for \"" + d_study.getId() 
				+ "\" on Endpoint \"" + d_endpoint.getName() + "\"";
	}
}
