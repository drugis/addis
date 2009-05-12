package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RateMeasurement extends Measurement {
	private Integer d_rate;
	private PatientGroupListener d_listener;
	
	public static final String PROPERTY_RATE = "rate";
	
	public RateMeasurement() {
		d_listener = new PatientGroupListener();
	}
	
	public RateMeasurement(Endpoint e) {
		super(e);
		d_listener = new PatientGroupListener();
		d_rate = 0; // FIXME
	}

	@Override
	public String getLabel() {
		return toString();
	}

	@Override
	public Double getMean() {
		if (d_rate == null || getSize() == null) {
			return null;
		}
		return (double) d_rate / (double) getSize();
	}

	private Integer getSize() {
		if (getPatientGroup() == null) {
			return null;
		}
		return getPatientGroup().getSize();
	}

	@Override
	public Double getStdDev() {
		return getMean() / Math.sqrt(getSize());
	}
	
	@Override
	public String toString() {
		return generateLabel(getSize());
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
		firePropertyChange(PROPERTY_RATE, oldVal, d_rate);
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}

	public Integer getRate() {
		return d_rate;
	}
	
	private class PatientGroupListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals(PatientGroup.PROPERTY_SIZE)) {
				Integer oldSize = (Integer)event.getOldValue();
				Integer newSize = (Integer)event.getNewValue();
				firePropertyChange(Measurement.PROPERTY_LABEL, 
						generateLabel(oldSize), generateLabel(newSize));
			}
		}
	}
	
	@Override
	public void setPatientGroup(PatientGroup g) {
		if (getPatientGroup() != null) {
			getPatientGroup().removePropertyChangeListener(PatientGroup.PROPERTY_SIZE, d_listener);
		}
		Integer oldSize = getSize();
		g.addPropertyChangeListener(PatientGroup.PROPERTY_SIZE, d_listener);
		super.setPatientGroup(g);
		Integer newSize = getSize();
		if ((oldSize == null && newSize != null) || (oldSize != null && !oldSize.equals(newSize))) {
			firePropertyChange(Measurement.PROPERTY_LABEL, 
						generateLabel(oldSize), generateLabel(newSize));
		}
	}
}