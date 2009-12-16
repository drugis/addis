package org.drugis.addis.presentation;

import java.util.HashMap;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.StudyCharacteristics;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class CharacteristicVisibleMap extends HashMap<Characteristic, AbstractValueModel> {
	
	public CharacteristicVisibleMap() {
		for (Characteristic c : StudyCharacteristics.values()) {
			put(c, new ValueHolder(true));
		}		
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return get(c);
	}	

}
