package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DomainImpl implements Domain, Serializable {
	private static final long serialVersionUID = 3222342605059458693L;
	private List<Endpoint> d_endpoints;
	private List<Study> d_studies;
	private List<Drug> d_drugs;
	private transient List<DomainListener> d_listeners;
	
	private void readObject(ObjectInputStream in)
	throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		d_listeners = new ArrayList<DomainListener>();
		d_studyListener = new StudyChangeListener();
		
		for (Study s : d_studies) {
			s.addPropertyChangeListener(d_studyListener);
		}
	}
	
	private class StudyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireStudiesChanged();
		}
	}
	
	private transient PropertyChangeListener d_studyListener = new StudyChangeListener();
	
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
	
	public List<DomainListener> getListeners() {
		return Collections.unmodifiableList(d_listeners);
	}

	public void addStudy(Study s) throws NullPointerException {
		if (s == null) {
			throw new NullPointerException("Study may not be null");
		}
		s.addPropertyChangeListener(d_studyListener);
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

	public List<Study> getStudies(Endpoint e) {
		List<Study> list = new ArrayList<Study>();
		for (Study s : d_studies) {
			if (s.getEndpoints().contains(e)) {
				list.add(s);
			}
		}
		return list;
	}

	public boolean equals(Object o) {
		if (o instanceof Domain) {
			Domain other = (Domain)o;
			return getEndpoints().equals(other.getEndpoints()) &&
				getDrugs().equals(other.getDrugs()) &&
				getStudies().equals(other.getStudies());
		}
		
		return false;
	}
	
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getEndpoints().hashCode();
		hash = hash * 31 + getDrugs().hashCode();
		hash = hash * 31 + getStudies().hashCode();
		return hash;
	}
}
