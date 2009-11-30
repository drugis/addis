package org.drugis.addis.presentation;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.AbstractValueModel;

public interface StudyListPresentationModel {

	public ListHolder<Study> getIncludedStudies();
	
	public AbstractValueModel getCharacteristicVisibleModel(
			StudyCharacteristic c);
}