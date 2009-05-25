package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

public class DomainPersistent implements Domain {
	PersistenceManagerFactory d_pmf;
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
		
		persistObject(e);
		
		fireEndpointsChanged();
	}

	private void persistObject(Object o) {
		PersistenceManager pm = d_pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(o);
			tx.commit();
		} catch (Exception ex) {
			if (tx.isActive()) {
				tx.rollback();
			}
			ex.printStackTrace();
		} finally {
			pm.close();
		}
	}

	private void fireEndpointsChanged() {
		for (DomainListener l : d_listeners) {
			l.endpointsChanged();
		}
	}

	public List<Endpoint> getEndpoints() {
		List<Endpoint> endpoints = fetchObjects(Endpoint.class);
		
		return endpoints;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> fetchObjects(Class<T> type) {
		PersistenceManager pm = d_pmf.getPersistenceManager();
		
		Collection<T> objects = new ArrayList<T>();
		Transaction tx = pm.currentTransaction();
		try {
			pm.getFetchPlan().addGroup("default");
			pm.getFetchPlan().addGroup("pg-full");
            pm.getFetchPlan().setMaxFetchDepth(4);
			tx.begin();
			Query q = pm.newQuery(type);
			Collection<T> res = (Collection<T>) q.execute();
			
			objects = pm.detachCopyAll(res);
			
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			pm.close();
		}
		return new ArrayList<T>(objects);
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
		
		persistObject(s);
		
		fireStudiesChanged();
	}

	private void fireStudiesChanged() {
		for (DomainListener l : d_listeners) {
			l.studiesChanged();
		}
	}

	public List<Study> getStudies() {
		List<Study> studies = fetchObjects(Study.class);
		return studies;
	}

	public void addDrug(Drug d) throws NullPointerException {
		if (d == null) {
			throw new NullPointerException("Drug may not be null");
		}
		
		persistObject(d);
		
		fireDrugsChanged();
	}

	private void fireDrugsChanged() {
		for (DomainListener l : d_listeners) {
			l.drugsChanged();
		}
	}

	public List<Drug> getDrugs() {
		List<Drug> drugs = fetchObjects(Drug.class);
		
		return drugs;
	}

	public List<Study> getStudies(Endpoint e) {
		if (e == null) {
			throw new NullPointerException("Endpoint may not be null");
		}
		List<Study> list = new ArrayList<Study>();
		for (Study s : getStudies()) {
			if (s == null) {
				throw new NullPointerException("Null study?");
			}
			if (s.getEndpoints() == null) {
				throw new NullPointerException("Illegal study (" + s + ")");
			}
			if (s.getEndpoints().contains(e)) {
				list.add(s);
			}
		}
		return list;
	}
	
	private static PersistenceManagerFactory getFactory() {
		return JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
	}

	public Drug getDrug(String name) {
		for (Drug d : getDrugs()) {
			if (d.getName().equals(name)) {
				return d;
			}
		}
		return null;
	}

	public Study getStudy(String id) {
		for (Study s : getStudies()) {
			if (s.getId().equals(id)) {
				return s;
			}
		}
		return null;
	}
}