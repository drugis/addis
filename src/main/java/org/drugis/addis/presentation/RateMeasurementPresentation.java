package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import org.drugis.addis.entities.RateMeasurement;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RateMeasurementPresentation extends LabeledPresentationModel<RateMeasurement> {
	public static class LabelModel extends AbstractLabelModel<RateMeasurement> {
		public LabelModel(RateMeasurement bean) {
			super(bean);
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
		
		@Override
		public String getValue() {
			return generateLabel(getRate(), getSize());
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_RATE)) {
				firePropertyChange (generateLabel((Integer) evt.getOldValue(), getSize()), generateLabel((Integer) evt.getNewValue(), getSize()));
			}
			else if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_SAMPLESIZE)) {
				firePropertyChange (generateLabel(getRate(), (Integer) evt.getOldValue()), generateLabel(getRate(), (Integer) evt.getNewValue()));
			}
		}
	}

	public RateMeasurementPresentation(RateMeasurement bean) {
		super(bean);
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}
}
