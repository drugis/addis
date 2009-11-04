package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public abstract class AbstractRelativeEffectTableModel extends AbstractTableModel implements RelativeEffectTableModel {
	protected Study d_study;
	protected Endpoint d_endpoint;
	protected PresentationModelFactory d_pmf;
	
	protected AbstractRelativeEffectTableModel(Study study, Endpoint endpoint, PresentationModelFactory pmf) {
		d_study = study;
		d_endpoint = endpoint;
		d_pmf = pmf;
	}
	
	public abstract String getTitle();

	protected abstract RelativeEffect<?> getRelativeEffect(Measurement denominator, Measurement numerator);


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
		return d_pmf.getModel(getRelativeEffect(denominator, numerator));
	}

	/**
	 * @see org.drugis.addis.presentation.RelativeEffectTableModel#getDescriptionAt(int, int)
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

	public String getDescription() {
		return getTitle() + " for \"" + d_study.getId() 
				+ "\" on Endpoint \"" + d_endpoint.getName() + "\"";
	}
}
