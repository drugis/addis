package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable="true")
public class BasicRateMeasurement extends BasicMeasurement implements RateMeasurement {
	private Integer d_rate;
	private SampleSizeListener d_listener = new SampleSizeListener();
	
	public BasicRateMeasurement() {
		addPropertyChangeListener(PROPERTY_SAMPLESIZE, d_listener);
	}
	
	public BasicRateMeasurement(Endpoint e) {
		super(e);
		addPropertyChangeListener(PROPERTY_SAMPLESIZE, d_listener);
		d_rate = 0; // FIXME
	}

	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		return generateLabel(getSampleSize());
	}
	
	private String generateLabel(Integer size) {
		if (d_rate == null || size == null) {
			return "INCOMPLETE";
		}
		return d_rate.toString() + "/" + size.toString();
	}

	public void setRate(Integer rate) {
		String oldLabel = getLabel();
		Integer oldVal = d_rate;
		d_rate = rate;
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_RATE, oldVal, d_rate));
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_LABEL, oldLabel, getLabel()));
	}

	@Persistent
	public Integer getRate() {
		return d_rate;
	}
	
	private class SampleSizeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals(Measurement.PROPERTY_SAMPLESIZE)) {
				Integer oldSize = (Integer)event.getOldValue();
				Integer newSize = (Integer)event.getNewValue();
				firePropertyChange(Measurement.PROPERTY_LABEL, 
						generateLabel(oldSize), generateLabel(newSize));
			}
		}	
	}
}