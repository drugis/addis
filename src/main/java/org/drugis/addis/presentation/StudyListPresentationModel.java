package org.drugis.addis.presentation;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.value.AbstractValueModel;

public interface StudyListPresentationModel {

	public ListHolder<Study> getIncludedStudies();
	
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c);
}