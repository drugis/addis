package org.drugis.addis.presentation;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class VariablePresentationModel extends PresentationModel<Variable> implements StudyListPresentationModel, LabeledPresentationModel {

	private ListHolder<Study> d_studies;
	private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();

	public VariablePresentationModel(Variable bean, ListHolder<Study> studies) {
		super(bean);
		d_studies = studies;
	}
	
	public ListHolder<Study> getIncludedStudies() {
		return d_studies;
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_characteristicVisibleMap.get(c);
	}

	public AbstractValueModel getLabelModel() {
		return new ValueHolder(getBean().getName());
	}
	
	public static String getCategoryName(Variable om) throws IllegalArgumentException{
		if(om instanceof Endpoint)
			return "Endpoint";
		if(om instanceof AdverseEvent)
			return "Adverse drug event";
		if(om instanceof Variable)
			return "Population characteristic";
		else
			throw new IllegalArgumentException("Category not recognized");
	}
	
	public String getCategoryName() throws IllegalArgumentException{
		return getCategoryName(getBean());
	}
}
