package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.ContinuousMeasurement;
import com.jgoodies.binding.value.AbstractValueModel;

// FIXME: there should be separate implementations of this class for each concrete Measurement,
// and these should implement the PROPERTY_LABEL, in stead of the Measurement itself.

@SuppressWarnings("serial")
public class ContinuousMeasurementPresentation extends LabeledPresentationModel<ContinuousMeasurement> {
	public static class LabelModel extends AbstractLabelModel<ContinuousMeasurement> {
		protected ContinuousMeasurement d_bean;

		public LabelModel(ContinuousMeasurement bean) {
			d_bean = bean;
			bean.addPropertyChangeListener(this);
		}
		
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

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}

		protected void firePropertyChange(String oldVal, String newVal) {
			firePropertyChange("value", oldVal, newVal);
		}

		protected ContinuousMeasurement getBean() {
			return d_bean;
		}
	}

	public ContinuousMeasurementPresentation(ContinuousMeasurement bean) {
		super(bean);
		//d_pmm = pmm;
		getLabelModel().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(PROPERTY_LABEL, evt.getOldValue(), evt.getNewValue());
			}
		});
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
