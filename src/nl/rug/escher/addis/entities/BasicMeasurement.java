package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.jgoodies.binding.beans.Model;

@PersistenceCapable(detachable="true")
@FetchGroup(name="default",members={@Persistent(name="endpoint")})
public abstract class BasicMeasurement extends Model implements Measurement {
	private PatientGroup d_patientGroup;
	private Endpoint d_endpoint;
	private PatientGroupListener d_listener;

	public static final String PROPERTY_PATIENTGROUP = "patientGroup";
	protected BasicMeasurement() {
		d_listener = new PatientGroupListener();
	}
	
	public BasicMeasurement(Endpoint e) {
		this();
		d_endpoint = e;
	}

	@Persistent(mappedBy="measurements")
	public PatientGroup getPatientGroup() {
		return d_patientGroup;
	}

	private class PatientGroupListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals(PatientGroup.PROPERTY_SIZE)) {
				Integer oldSize = (Integer)event.getOldValue();
				Integer newSize = (Integer)event.getNewValue();
				if (BasicMeasurement.this != null) {
					sampleSizeChange(oldSize, newSize);
				}
			}
		}
	}
	
	public void setPatientGroup(PatientGroup g) {
		if (getPatientGroup() != null) {
			getPatientGroup().removePropertyChangeListener(PatientGroup.PROPERTY_SIZE, d_listener);
		}
		Integer oldSize = getSampleSize();
		if (g != null && d_listener != null) {
			//g.addPropertyChangeListener(PatientGroup.PROPERTY_SIZE, d_listener);
		}
		PatientGroup oldVal = d_patientGroup;
		d_patientGroup = g;
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_PATIENTGROUP, oldVal, d_patientGroup));
		Integer newSize = getSampleSize();
		if ((oldSize == null && newSize != null) || (oldSize != null && !oldSize.equals(newSize))) {
			firePropertyChange(new PropertyChangeEvent(this, PROPERTY_SAMPLESIZE, oldSize, newSize));
		}
	}

	public void sampleSizeChange(Integer oldSize, Integer newSize) {
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_SAMPLESIZE, oldSize, newSize));
	}

	@Persistent
	public Endpoint getEndpoint() {
		return d_endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		Endpoint oldVal = d_endpoint;
		d_endpoint = endpoint;
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_ENDPOINT, oldVal, d_endpoint));
	}

	public Integer getSampleSize() {
		if (getPatientGroup() == null) {
			return null;
		}
		return getPatientGroup().getSize();
	}
}