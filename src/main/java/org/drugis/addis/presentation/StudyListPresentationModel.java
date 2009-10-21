package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.AbstractValueModel;

public interface StudyListPresentationModel {

	public List<Study> getIncludedStudies();

	public AbstractValueModel getCharacteristicVisibleModel(
			StudyCharacteristic c);

}