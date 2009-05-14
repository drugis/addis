package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import com.jgoodies.binding.beans.Model;

public class PooledRateMeasurement extends Model implements RateMeasurement {
	private List<RateMeasurement> d_measurements;
	private Integer d_rate;
	private Integer d_size;
	
	private class ChildListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_RATE)) {
				triggerRateChange((Integer)evt.getNewValue() - (Integer)evt.getOldValue());
			} else if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_SAMPLESIZE)) {
				triggerSampleSizeChange((Integer)evt.getNewValue() - (Integer)evt.getOldValue());
			} else if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_ENDPOINT)) {
				throw new RuntimeException("Endpoint changed for child measurement");
			}
		}
	};
	
	/**
	 * Construct a pooled measurement from the given list of measurements.
	 * @param measurements List of measurements. 
	 * @throws NullPointerException measurements may not be null.
	 * @throws IllegalArgumentException All measurements should measure the same Endpoint. Empty list not allowed.
	 */
	public PooledRateMeasurement(List<RateMeasurement> measurements) 
	throws IllegalArgumentException, NullPointerException {
		validate(measurements);
		d_measurements = measurements;
		d_rate = calcRate();
		d_size = calcSampleSize();
		registerListener();
	}
	
	private void triggerSampleSizeChange(int delta) {
		Integer oldValue = getSampleSize();
		d_size += delta;
		firePropertyChange(RateMeasurement.PROPERTY_SAMPLESIZE, oldValue, getSampleSize());
		firePropertyChange(RateMeasurement.PROPERTY_LABEL, generateLabel(getRate(), oldValue),
				getLabel());
	}

	private void registerListener() {
		ChildListener listener = new ChildListener();
		for (RateMeasurement m : d_measurements) {
			m.addPropertyChangeListener(listener);
		}
	}

	/**
	 * Post-condition: d_rate equals sum of child rates
	 * @param delta
	 */
	private void triggerRateChange(int delta) {
		Integer oldValue = getRate();
		d_rate += delta;
		firePropertyChange(RateMeasurement.PROPERTY_RATE, oldValue, getRate());
		firePropertyChange(RateMeasurement.PROPERTY_LABEL, generateLabel(oldValue, getSampleSize()),
				getLabel());
	}

	private void validate(List<RateMeasurement> measurements) {
		if (measurements == null) {
			throw new NullPointerException();
		}
		if (measurements.size() == 0) {
			throw new IllegalArgumentException("Pooling 0 measurements not allowed");
		}
		Endpoint expected = measurements.get(0).getEndpoint();
		for (RateMeasurement m : measurements) {
			if (!m.getEndpoint().equals(expected)) {
				throw new IllegalArgumentException("Pooling measurements with different endpoints not allowed");
			}
		}
	}

	public Endpoint getEndpoint() {
		return d_measurements.get(0).getEndpoint();
	}

	public Integer getRate() {
		return d_rate;
	}

	private Integer calcRate() {
		int rate = 0;
		for (RateMeasurement m : d_measurements) {
			rate += m.getRate();
		}
		return rate;
	}
	
	public Integer getSampleSize() {
		return d_size;
	}

	private Integer calcSampleSize() {
		int size = 0;
		for (RateMeasurement m : d_measurements) {
			size += m.getSampleSize();
		}
		return size;
	}

	public String getLabel() {
		return generateLabel(getRate(), getSampleSize());
	}

	private String generateLabel(Integer rate, Integer sampleSize) {
		return rate.toString() + "/" + sampleSize.toString();
	}
}
