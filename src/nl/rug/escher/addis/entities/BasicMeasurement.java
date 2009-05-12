package nl.rug.escher.addis.entities;

import com.jgoodies.binding.beans.Model;

public abstract class BasicMeasurement extends Model implements Measurement {
	private PatientGroup d_patientGroup;
	private Endpoint d_endpoint;

	public static final String PROPERTY_PATIENTGROUP = "patientGroup";
	protected BasicMeasurement() {
		
	}
	
	public BasicMeasurement(Endpoint e) {
		d_endpoint = e;
	}

	public PatientGroup getPatientGroup() {
		return d_patientGroup;
	}

	public void setPatientGroup(PatientGroup patientGroup) {
		PatientGroup oldVal = d_patientGroup;
		d_patientGroup = patientGroup;
		firePropertyChange(PROPERTY_PATIENTGROUP, oldVal, d_patientGroup);
	}

	public Endpoint getEndpoint() {
		return d_endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		Endpoint oldVal = d_endpoint;
		d_endpoint = endpoint;
		firePropertyChange(PROPERTY_ENDPOINT, oldVal, d_endpoint);
	}

}