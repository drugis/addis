package org.drugis.addis.presentation;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public abstract class AbstractRelativeEffectTableModel extends AbstractTableModel
implements RelativeEffectTableModel {
	protected Study d_study;
	protected OutcomeMeasure d_outMeas;
	protected PresentationModelFactory d_pmf;
	private List<Arm> d_arms;
	
	protected AbstractRelativeEffectTableModel(Study study, OutcomeMeasure om, PresentationModelFactory pmf) {
		d_study = study;
		d_outMeas = om;
		d_pmf = pmf;
		d_arms = d_study.getArms();
	}
	
	protected AbstractRelativeEffectTableModel(List<Arm> arms, OutcomeMeasure om, PresentationModelFactory pmf) {
		d_arms = arms;
		d_outMeas = om;
		d_pmf = pmf;
	}
	
	public abstract String getTitle();

	protected abstract RelativeEffect<?> getRelativeEffect(Measurement denominator, Measurement numerator);
	
	protected abstract Class<? extends RelativeEffect<?>> getRelativeEffectType();


	public int getColumnCount() {
		return d_arms.size();
	}

	public int getRowCount() {
		return d_arms.size();
	}

	public Object getValueAt(int row, int col) {
		if (row == col) {
			return d_pmf.getModel(d_study.getArms().get(row));
		}
		
		Measurement denominator = d_study.getMeasurement(d_outMeas, d_arms.get(row));
		Measurement numerator = d_study.getMeasurement(d_outMeas, d_arms.get(col));
		return d_pmf.getModel(getRelativeEffect(denominator, numerator));
	}

	/**
	 * @see org.drugis.addis.presentation.RelativeEffectTableModel#getDescriptionAt(int, int)
	 */
	public String getDescriptionAt(int row, int col) {
		if (row == col) {
			return null;
		}
		return "\"" + getArmLabel(col) +
			"\" relative to \"" + getArmLabel(row) + "\"";
	}

	private String getArmLabel(int index) {
		return d_pmf.getLabeledModel(d_arms.get(index)).getLabelModel().getString();
	}

	public String getDescription() {
		return getTitle() + " for \"" + d_study.getId() 
				+ "\" on Endpoint \"" + d_outMeas.getName() + "\"";
	}

	public ForestPlotPresentation getPlotPresentation(int row, int column) {
		return new ForestPlotPresentation((Study)d_study, d_outMeas, d_arms.get(row).getDrug(),
				d_study.getArms().get(column).getDrug(), getRelativeEffectType(), d_pmf);
	}
}
