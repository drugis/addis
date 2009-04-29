package nl.rug.escher.entities;

import java.util.ArrayList;
import java.util.List;

public class DomainImpl implements Domain {
	private List<Endpoint> d_endpoints;
	private List<Study> d_studies;
	private List<Drug> d_drugs;
	private List<DomainListener> d_listeners;
	
	public DomainImpl() {
		d_endpoints = new ArrayList<Endpoint>();
		d_studies = new ArrayList<Study>();
		d_drugs = new ArrayList<Drug>();
		d_listeners = new ArrayList<DomainListener>();
	}

	public void addEndpoint(Endpoint e) {
		if (e == null) {
			throw new NullPointerException("Endpoint may not be null");
		}
		
		d_endpoints.add(e);
		
		fireEndpointsChanged();
	}

	private void fireEndpointsChanged() {
		for (DomainListener l : d_listeners) {
			l.endpointsChanged();
		}
	}

	public List<Endpoint> getEndpoints() {
		return d_endpoints;
	}

	public void addListener(DomainListener listener) {
		if (!d_listeners.contains(listener)) {
			d_listeners.add(listener);
		}
	}

	public void removeListener(DomainListener listener) {
		d_listeners.remove(listener);
	}

	public void addStudy(Study s) throws NullPointerException {
		printStudy(s);
		if (s == null) {
			throw new NullPointerException("Study may not be null");
		}
		d_studies.add(s);
		
		fireStudiesChanged();
	}

	private void printStudy(Study s) {
		System.out.println("Study " + s.getId());
		System.out.println("Has Endpoints: ");
		for (Endpoint e : s.getEndpoints()) {
			System.out.println("\t" + e.getName() + " (" + e.getDescription() + ")");
		}
		System.out.println("Has PatientGroups: ");
		for (PatientGroup g : s.getPatientGroups()) {
			printPatientGroup(g);
		}
	}

	private void printPatientGroup(PatientGroup g) {
		System.out.println("\t" + g.getDrug().getName() + " " + g.getDose().getQuantity()
				+ " " + g.getDose().getUnit());
		for (Measurement m : g.getMeasurements()) {
			System.out.println("\t\t" + m.getEndpoint() + " " + m.getMean() + " +/- " + m.getStdDev());
		}
	}

	private void fireStudiesChanged() {
		for (DomainListener l : d_listeners) {
			l.studiesChanged();
		}
	}

	public List<Study> getStudies() {
		return d_studies;
	}

	public void addDrug(Drug d) throws NullPointerException {
		if (d == null) {
			throw new NullPointerException("Drug may not be null");
		}
		d_drugs.add(d);
		
		fireDrugsChanged();
	}

	private void fireDrugsChanged() {
		for (DomainListener l : d_listeners) {
			l.drugsChanged();
		}
	}

	public List<Drug> getDrugs() {
		return d_drugs;
	}

}
