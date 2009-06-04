package nl.rug.escher.addis.entities;

import java.util.List;
import java.util.Set;

public interface Study extends Comparable<Study> {
	public final static String PROPERTY_ID = "id";
	public final static String PROPERTY_ENDPOINTS = "endpoints";
	public final static String PROPERTY_PATIENTGROUPS = "patientGroups";

	public String getId();

	public List<Endpoint> getEndpoints();

	public List<? extends PatientGroup> getPatientGroups();

	public Set<Drug> getDrugs();
}