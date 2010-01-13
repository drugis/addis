package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class StudyCharacteristicHolder extends AbstractValueModel {
	protected Study d_study;
	protected Characteristic d_char;
	
	public StudyCharacteristicHolder(Study study, Characteristic characteristic) {
		d_study = study;
		d_char = characteristic; 
		d_study.addPropertyChangeListener(new CharChangedListener());
	}

	public void setValue(Object newValue) {
		throw new RuntimeException("This CharacteristicHolder is immutable");
	}

	public Object getValue() {
		return d_study.getCharacteristics().get(d_char);
	}
	
	public Characteristic getCharacteristic() {
		return d_char;
	}
	
	private class CharChangedListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Study.PROPERTY_CHARACTERISTIC)) {
				if (evt.getNewValue().equals(d_char))
					firePropertyChange("value", null, d_study.getCharacteristic(d_char));
			}
		}
	}
}
