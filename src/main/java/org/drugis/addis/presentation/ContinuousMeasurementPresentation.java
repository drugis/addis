package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Measurement;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

// FIXME: there should be separate implementations of this class for each concrete Measurement,
// and these should implement the PROPERTY_LABEL, in stead of the Measurement itself.

@SuppressWarnings("serial")
public class ContinuousMeasurementPresentation extends PresentationModel<ContinuousMeasurement> implements LabeledPresentationModel {
	public class LabelModel extends  AbstractValueModel implements PropertyChangeListener {
		public LabelModel() {
			getBean().addPropertyChangeListener(this);
		}
		
		public String getValue() {
			return generateLabel(getMean(), getStdDev(), getSampleSize());
		}

		private Double getStdDev() {
			return getBean().getStdDev();
		}

		private Double getMean() {
			return getBean().getMean();
		}
		
		public String generateLabel(Double mean, Double stdDev, Integer sampleSize) {
			if (mean == null || stdDev == null || sampleSize == null) {
				return "INCOMPLETE"; 
			}
			
			DecimalFormat df = new DecimalFormat("####0.0##");
			return df.format(mean) + " \u00B1 " + df.format(stdDev) + " (" + sampleSize + ")";
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(ContinuousMeasurement.PROPERTY_MEAN)) {
				firePropertyChange("value", generateLabel((Double) evt.getOldValue(), getStdDev(), getSampleSize()), generateLabel((Double) evt.getNewValue(), getStdDev(), getSampleSize()));
			} else if (evt.getPropertyName().equals(ContinuousMeasurement.PROPERTY_STDDEV)) {
				firePropertyChange("value", generateLabel(getMean(), (Double) evt.getOldValue(), getSampleSize()), generateLabel(getMean(), (Double) evt.getNewValue(), getSampleSize()));
			} else if (evt.getPropertyName().equals(Measurement.PROPERTY_SAMPLESIZE)) {
				firePropertyChange("value", generateLabel(getMean(), getStdDev(), (Integer) evt.getOldValue()), generateLabel(getMean(), getStdDev(), (Integer) evt.getNewValue()));
			}
		}

		private Integer getSampleSize() {
			return getBean().getSampleSize();
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}
	
	public ContinuousMeasurementPresentation(ContinuousMeasurement bean) {
		super(bean);
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	public String toString() {
		return (String) getLabelModel().getValue(); 
	}
}
