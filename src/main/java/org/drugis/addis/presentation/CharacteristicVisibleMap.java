package org.drugis.addis.presentation;

import java.util.HashMap;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class CharacteristicVisibleMap extends HashMap<StudyCharacteristic, AbstractValueModel> {
	
	public CharacteristicVisibleMap() {
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			put(c, new ValueHolder(true));
		}		
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return get(c);
	}	

}
