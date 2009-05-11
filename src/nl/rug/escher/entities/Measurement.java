package nl.rug.escher.entities;

import com.jgoodies.binding.beans.Model;

public abstract class Measurement extends Model {

	private PatientGroup d_patientGroup;

	public abstract String getLabel();

	public abstract Double getStdDev();

	public abstract Double getMean();

	private Endpoint d_endpoint;
	public static final String PROPERTY_MEAN = "mean";
	public static final String PROPERTY_STDDEV = "stdDev";
	public static final String PROPERTY_PATIENTGROUP = "patientGroup";
	public static final String PROPERTY_ENDPOINT = "endpoint";
	public static final String PROPERTY_LABEL = "label";

	public Measurement() {
		super();
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