package nl.rug.escher.entities;

import java.util.ArrayList;
import java.util.List;

public class DomainImpl implements Domain {
	private List<Endpoint> d_endpoints;
	private List<Study> d_studies;
	private List<DomainListener> d_listeners;
	
	public DomainImpl() {
		d_endpoints = new ArrayList<Endpoint>();
		d_studies = new ArrayList<Study>();
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
		if (s == null) {
			throw new NullPointerException("Study may not be null");
		}
		d_studies.add(s);
		
		fireStudiesChanged();
	}

	private void fireStudiesChanged() {
		for (DomainListener l : d_listeners) {
			l.studiesChanged();
		}
	}

	public List<Study> getStudies() {
		return d_studies;
	}

}
