package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

public class EndpointPresentationModel extends PresentationModel<Endpoint> implements StudyListPresentationModel{

	private ListHolder<Study> d_studies;

	public EndpointPresentationModel(Endpoint bean, SortedSet<Study> studies) {
		super(bean);
		final ArrayList<Study> list = new ArrayList<Study>(studies);
		d_studies = new AbstractListHolder<Study>() {
			@Override
			public List<Study> getValue() {
				return list;
			}
		};
	}
	
	private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();
	
	public ListHolder<Study> getIncludedStudies() {
		return d_studies;
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(StudyCharacteristic c) {
		return d_characteristicVisibleMap.get(c);
	}	
}
