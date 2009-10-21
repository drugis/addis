package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

public class StudyListPresentationModelImpl implements
		StudyListPresentationModel {
	
	private List<Study> d_studies;

	public StudyListPresentationModelImpl(List<Study> studies) {
		d_studies = studies;
	}

	public AbstractValueModel getCharacteristicVisibleModel(
			StudyCharacteristic c) {
		return new ValueHolder(true);
	}

	public List<Study> getIncludedStudies() {
		return d_studies;
	}

}
