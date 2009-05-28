package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

public class DomainPersistent implements Domain {
	PersistenceManagerFactory d_pmf;
	private List<Endpoint> d_endpoints = new ArrayList<Endpoint>();
	private List<Study> d_studies = new ArrayList<Study>();
	private List<Drug> d_drugs = new ArrayList<Drug>();
	private List<DomainListener> d_listeners = new ArrayList<DomainListener>();
	
	private PropertyChangeListener d_studyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			fireStudiesChanged();
		}
	};
	
	public DomainPersistent() {
		this(getFactory());
	}
	
	public DomainPersistent(PersistenceManagerFactory pmf) {
		d_pmf = pmf;
	}

	public void addEndpoint(Endpoint e) {
		if (e == null) {
			throw new NullPointerException("Endpoint may not be null");
		}
		
		PersistenceManager pm = d_pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(e);
			tx.commit();
		} finally {
			pm.close();
		}
		
		fireEndpointsChanged();
	}

	private void fireEndpointsChanged() {
		for (DomainListener l : d_listeners) {
			l.endpointsChanged();
		}
	}

	public List<Endpoint> getEndpoints() {
		PersistenceManager pm = d_pmf.getPersistenceManager();
		
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Extent<Endpoint> extent = pm.getExtent(Endpoint.class);
			for (Endpoint e : extent) {
				endpoints.add(e);
			}
			tx.commit();
		} finally {
			pm.close();
		}
		
		return endpoints;
	}
	
	public Endpoint getEndpoint(String name) {
		for (Endpoint e : getEndpoints()) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
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
	
	private static PersistenceManagerFactory getFactory() {
		return JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
	}
}