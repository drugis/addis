package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.RateMeasurement;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RateMeasurementPresentation extends PresentationModel<RateMeasurement> implements LabeledPresentationModel {
	public static class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		protected RateMeasurement d_bean;

		public LabelModel(RateMeasurement bean) {
			d_bean = bean;
			bean.addPropertyChangeListener(this);
		}

		private Integer getSize() {
			return getBean().getSampleSize();
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
				firePropertyChange (generateLabel((Integer) evt.getOldValue(), getSize()), generateLabel((Integer) evt.getNewValue(), getSize()));
			}
			else if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_SAMPLESIZE)) {
				firePropertyChange (generateLabel(getRate(), (Integer) evt.getOldValue()), generateLabel(getRate(), (Integer) evt.getNewValue()));
			}
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}

		protected void firePropertyChange(String oldVal, String newVal) {
			firePropertyChange("value", oldVal, newVal);
		}

		protected RateMeasurement getBean() {
			return d_bean;
		}
	}

	protected PresentationModelManager d_pmm;

	public RateMeasurementPresentation(RateMeasurement bean) {
		super(bean);
		//d_pmm = pmm;
		getLabelModel().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(PROPERTY_LABEL, evt.getOldValue(), evt.getNewValue());
			}
		});
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

	public AbstractValueModel getModel(String name) { 
		if (PROPERTY_LABEL.equals(name)) {
			return getLabelModel();
		}
		return super.getModel(name);
	}

	public String getLabel() {
		return getLabelModel().getValue().toString();
	}
}
