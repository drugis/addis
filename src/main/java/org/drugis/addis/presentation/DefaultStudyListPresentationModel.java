/**
 * 
 */
package org.drugis.addis.presentation;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.AbstractValueModel;

public class DefaultStudyListPresentationModel implements StudyListPresentationModel {
	private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();
	private ListHolder<Study> d_list;
	
	public DefaultStudyListPresentationModel(ListHolder<Study> list) {
		d_list = list;
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(
			StudyCharacteristic c) {
		return d_characteristicVisibleMap.get(c);
	}

	public ListHolder<Study> getIncludedStudies() {
		return d_list;
	}
}