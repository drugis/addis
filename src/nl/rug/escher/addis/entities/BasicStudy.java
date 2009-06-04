package nl.rug.escher.addis.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BasicStudy extends AbstractStudy implements Study {
	private static final long serialVersionUID = -1373201520248610423L;
	private List<Endpoint> d_endpoints;
	private List<PatientGroup> d_patientGroups;
	
	public BasicStudy(String id) {
		d_endpoints = new ArrayList<Endpoint>();
		d_patientGroups = new ArrayList<PatientGroup>();
		d_id = id;
	}

	public List<Endpoint> getEndpoints() {
		return d_endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		List<Endpoint> oldVal = d_endpoints;
		d_endpoints = endpoints;
		firePropertyChange(PROPERTY_ENDPOINTS, oldVal, d_endpoints);
	}

	public List<PatientGroup> getPatientGroups() {
		return d_patientGroups;
	}

	public void setPatientGroups(List<PatientGroup> patientGroups) {
		List<PatientGroup> oldVal = d_patientGroups;
		d_patientGroups = patientGroups;
		firePropertyChange(PROPERTY_PATIENTGROUPS, oldVal, d_patientGroups);
	}
	
	public void addPatientGroup(PatientGroup group) {
		List<PatientGroup> newVal = new ArrayList<PatientGroup>(d_patientGroups);
		newVal.add(group);
		setPatientGroups(newVal);
	}
	
	public void addEndpoint(Endpoint endpoint) {
		List<Endpoint> newVal = new ArrayList<Endpoint>(d_endpoints);
		newVal.add(endpoint);
		setEndpoints(newVal);
	}

	public Set<Drug> getDrugs() {
		Set<Drug> drugs = new HashSet<Drug>();
		for (PatientGroup g : getPatientGroups()) {
			drugs.add(g.getDrug());
		}
		return drugs;
	}
}