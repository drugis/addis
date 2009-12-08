package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.RateMeasurement;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RateMeasurementPresentation extends PresentationModel<RateMeasurement> implements LabeledPresentationModel {
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {

		public LabelModel() {
			getBean().addPropertyChangeListener(this);
			getBean().getPatientGroup().addPropertyChangeListener(this);
		}

		private Integer getSize() {
			return getBean().getPatientGroup().getSize();
		}
		
		private Integer getRate() {
			return getBean().getRate();
		}
		
		private String generateLabel(Integer rate, Integer size) {
			if (rate == null || size == null) {
				return "INCOMPLETE";
			}
			return rate.toString() + "/" + size.toString();
		}
		
		public String getValue() {
			return generateLabel(getRate(), getSize());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_RATE)) {
				firePropertyChange("value", generateLabel((Integer) evt.getOldValue(), getSize()), generateLabel((Integer) evt.getNewValue(), getSize()));
			}
			else if (evt.getPropertyName().equals(PatientGroup.PROPERTY_SIZE)) {
				firePropertyChange("value", generateLabel(getRate(), (Integer) evt.getOldValue()), generateLabel(getRate(), (Integer) evt.getNewValue()));
			}
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}

	public RateMeasurementPresentation(RateMeasurement bean) {
		super(bean);
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
}
