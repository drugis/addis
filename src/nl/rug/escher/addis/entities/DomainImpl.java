package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DomainImpl implements Domain, Serializable {
	private static final long serialVersionUID = 3222342605059458693L;
	private Set<Endpoint> d_endpoints;
	private Set<Study> d_studies;
	private Set<Drug> d_drugs;
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
		d_endpoints = new HashSet<Endpoint>();
		d_studies = new HashSet<Study>();
		d_drugs = new HashSet<Drug>();
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

	public Set<Endpoint> getEndpoints() {
		return Collections.unmodifiableSet(d_endpoints);
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

	public Set<Study> getStudies() {
		return Collections.unmodifiableSet(d_studies);
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

	public Set<Drug> getDrugs() {
		return Collections.unmodifiableSet(d_drugs);
	}

	public Set<Study> getStudies(Endpoint e) {
		Set<Study> list = new HashSet<Study>();
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
