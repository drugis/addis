package nl.rug.escher.entities;

import com.jgoodies.binding.beans.Model;

public class Measurement extends Model {
	private Double d_mean;
	private Double d_stdDev;
	private PatientGroup d_patientGroup;
	private Endpoint d_endpoint;
	
	public static final String PROPERTY_MEAN = "mean";
	public static final String PROPERTY_STDDEV = "stdDev";
	public static final String PROPERTY_PATIENTGROUP = "patientGroup";
	public static final String PROPERTY_ENDPOINT = "endpoint";
	
	public Double getMean() {
		return d_mean;
	}
	
	public void setMean(Double mean) {
		Double oldVal = d_mean;
		d_mean = mean;
		firePropertyChange(PROPERTY_MEAN, oldVal, d_mean);
	}
	
	public Double getStdDev() {
		return d_stdDev;
	}
	
	public void setStdDev(Double stdDev) {
		Double oldVal = d_stdDev;
		d_stdDev = stdDev;
		firePropertyChange(PROPERTY_STDDEV, oldVal, d_stdDev);
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
