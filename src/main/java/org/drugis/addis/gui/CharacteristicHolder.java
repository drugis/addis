package org.drugis.addis.gui;

import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.MutableStudy;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class CharacteristicHolder extends AbstractValueModel {
	MutableStudy d_study;
	StudyCharacteristic d_char;
	
	public CharacteristicHolder(MutableStudy bean, StudyCharacteristic characteristic) {
		d_study = bean;
		d_char = characteristic;
	}

	public Object getValue() {
		return d_study.getCharacteristics().get(d_char);
	}

	public void setValue(Object o) {
		Model newValue = (Model)o;
		Object oldValue = d_study.getCharacteristics().get(d_char);
		Map<StudyCharacteristic, Model> chars = new HashMap<StudyCharacteristic, Model>(
				d_study.getCharacteristics());
		chars.put(d_char, newValue);
		d_study.setCharacteristics(chars);
		firePropertyChange("value", oldValue, newValue);
	}
}
