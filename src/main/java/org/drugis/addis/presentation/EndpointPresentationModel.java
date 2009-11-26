package org.drugis.addis.presentation;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class EndpointPresentationModel extends PresentationModel<Endpoint> implements StudyListPresentationModel, LabeledPresentationModel {

	private ListHolder<Study> d_studies;

	public EndpointPresentationModel(Endpoint bean, ListHolder<Study> studies) {
		super(bean);
		d_studies = studies;
	}
	
	private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();
	
	public ListHolder<Study> getIncludedStudies() {
		return d_studies;
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(StudyCharacteristic c) {
		return d_characteristicVisibleMap.get(c);
	}

	public AbstractValueModel getLabelModel() {
		return new ValueHolder(getBean().getName());
	}	
}
