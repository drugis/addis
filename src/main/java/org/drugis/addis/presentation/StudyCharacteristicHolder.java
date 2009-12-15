package org.drugis.addis.presentation;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class StudyCharacteristicHolder extends AbstractValueModel {
	protected Study d_study;
	protected StudyCharacteristic d_char;
	
	public StudyCharacteristicHolder(Study study, StudyCharacteristic characteristic) {
		d_study = study;
		d_char = characteristic; 
	}

	public void setValue(Object newValue) {
		throw new RuntimeException("This CharacteristicHolder is immutable");
	}

	public Object getValue() {
		return d_study.getCharacteristics().get(d_char);
	}
	
	public StudyCharacteristic getCharacteristic() {
		return d_char;
	}

}
