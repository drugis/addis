package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.jgoodies.binding.beans.Model;

@PersistenceCapable(detachable="true")
@FetchGroup(name="default",members={@Persistent(name="d_endpoint"),@Persistent(name="d_patientGroup")})
public abstract class BasicMeasurement extends Model implements Measurement {
	@Persistent(mappedBy="d_measurements")
	private PatientGroup d_patientGroup;
	@Persistent
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

	public PatientGroup getPatientGroup() {
		return d_patientGroup;
	}

	private class PatientGroupListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals(PatientGroup.PROPERTY_SIZE)) {
				Integer oldSize = (Integer)event.getOldValue();
				Integer newSize = (Integer)event.getNewValue();
				if (BasicMeasurement.this != null) {
					firePropertyChange(PROPERTY_SAMPLESIZE, oldSize, newSize);
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
			g.addPropertyChangeListener(PatientGroup.PROPERTY_SIZE, d_listener);
		}
		PatientGroup oldVal = d_patientGroup;
		d_patientGroup = g;
		firePropertyChange(PROPERTY_PATIENTGROUP, oldVal, d_patientGroup);
		Integer newSize = getSampleSize();
		firePropertyChange(PROPERTY_SAMPLESIZE, oldSize, newSize);
	}

	public Endpoint getEndpoint() {
		return d_endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		Endpoint oldVal = d_endpoint;
		d_endpoint = endpoint;
		firePropertyChange(PROPERTY_ENDPOINT, oldVal, d_endpoint);
	}

	public Integer getSampleSize() {
		if (getPatientGroup() == null) {
			return null;
		}
		return getPatientGroup().getSize();
	}
}