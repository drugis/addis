package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.FrequencyMeasurement;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class FrequencyMeasurementPresentation extends PresentationModel<FrequencyMeasurement>
		implements LabeledPresentationModel {
	public class LabelModel extends  AbstractValueModel implements PropertyChangeListener {
		public LabelModel() {
			getBean().addPropertyChangeListener(this);
		}
		
		public String getValue() {
			return getBean().toString();
		}

		public void propertyChange(PropertyChangeEvent evt) {
			
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}
	
	public FrequencyMeasurementPresentation(FrequencyMeasurement bean) {
		super(bean);
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
}
