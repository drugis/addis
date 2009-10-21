package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import org.drugis.addis.entities.ContinuousMeasurement;
import com.jgoodies.binding.value.AbstractValueModel;

// FIXME: there should be separate implementations of this class for each concrete Measurement,
// and these should implement the PROPERTY_LABEL, in stead of the Measurement itself.

@SuppressWarnings("serial")
public class ContinuousMeasurementPresentation extends LabeledPresentationModel<ContinuousMeasurement> {
	public static class LabelModel extends AbstractLabelModel<ContinuousMeasurement> {
		public LabelModel(ContinuousMeasurement bean) {
			super(bean);
		}
		
		@Override
		public String getValue() {
			return generateLabel(getMean(), getStdDev());
		}

		private Double getStdDev() {
			return getBean().getStdDev();
		}

		private Double getMean() {
			return getBean().getMean();
		}
		
		public String generateLabel(Double mean, Double stdDev) {
			if (mean == null || stdDev == null) {
				return "INCOMPLETE"; 
			}
			return mean.toString() + " \u00B1 " + stdDev.toString();
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(ContinuousMeasurement.PROPERTY_MEAN)) {
				firePropertyChange(
						generateLabel((Double) evt.getOldValue(), getStdDev()),
						generateLabel((Double) evt.getNewValue(), getStdDev()));
			} else if (evt.getPropertyName().equals(ContinuousMeasurement.PROPERTY_STDDEV)) {
				firePropertyChange(
						generateLabel(getMean(), (Double) evt.getOldValue()),
						generateLabel(getMean(), (Double) evt.getNewValue()));
			}
		}
	}

	public ContinuousMeasurementPresentation(ContinuousMeasurement bean) {
		super(bean);
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
