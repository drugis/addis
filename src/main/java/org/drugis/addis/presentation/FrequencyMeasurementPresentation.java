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
			fireValueChange(null, getValue());
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}
	
	public class FrequencyModel extends AbstractValueModel implements PropertyChangeListener {
		private String d_cat;

		public FrequencyModel(String category) {
			d_cat = category;
			getBean().addPropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(FrequencyMeasurement.PROPERTY_FREQUENCIES)) {
				fireValueChange(null, getValue());
			}
		}

		public Object getValue() {
			return getBean().getFrequency(d_cat);
		}

		public void setValue(Object newValue) {
			if (newValue instanceof Integer)
				getBean().setFrequency(d_cat, (Integer)newValue);
			else
				throw new IllegalArgumentException("Can only set frequencies with an Integer");
		}
	}
	
	public FrequencyMeasurementPresentation(FrequencyMeasurement bean) {
		super(bean);
	}
	
	public AbstractValueModel getFrequencyModel(String category) {
		return new FrequencyModel(category);
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
}
