package org.drugis.addis.entities;

public interface MutableStudy extends Study {
	
	public void addEndpoint(Endpoint e);
	public void deleteEndpoint(Endpoint e);
	public void setId(String id);
	public void setMeasurement(Endpoint e, PatientGroup g, Measurement m);
}
