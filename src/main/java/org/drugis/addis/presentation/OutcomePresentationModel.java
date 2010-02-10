package org.drugis.addis.presentation;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class OutcomePresentationModel extends PresentationModel<OutcomeMeasure> implements StudyListPresentationModel, LabeledPresentationModel {

	private ListHolder<Study> d_studies;
	private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();

	public OutcomePresentationModel(OutcomeMeasure bean, ListHolder<Study> studies) {
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
	
	public static String getCategoryName(OutcomeMeasure om) throws IllegalArgumentException{
		if(om instanceof Endpoint)
			return "Endpoint";
		if(om instanceof AdverseEvent)
			return "Adverse drug event";
		else
			throw new IllegalArgumentException("Category not recognized");
	}
	
	public String getCategoryName() throws IllegalArgumentException{
		return getCategoryName(getBean());
	}
}
