package nl.rug.escher.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.junit.Before;
import org.junit.Test;

public class DataNucleusBugTest {
	@PersistenceCapable(identityType=IdentityType.APPLICATION,detachable="true")
	public static class Element {
		@Persistent(primaryKey="true")
		public String id = "e";
		
		public boolean equals(Object o) {
			if (o instanceof Element) {
				Element e = (Element)o;
				return id == null ? e.id == null : id.equals(((Element)o).id);
			}
			return false;
		}
		
		public String toString() {
			return id;
		}
	}
	
	@PersistenceCapable(identityType=IdentityType.DATASTORE,detachable="true")
	public static class Container {
		@Persistent
		public List<Element> elements = new ArrayList<Element>();
	}
	
	PersistenceManagerFactory d_factory;
	
	@Before
	public void setUp() {
		File f = new File("test.db");
		if (f.exists() && !f.delete()) {
			throw new RuntimeException();
		}
		
		Properties p = new Properties();
		p.setProperty(
				"javax.jdo.PersistenceManagerFactoryClass", 
				"org.datanucleus.jdo.JDOPersistenceManagerFactory");
		p.setProperty(
				"javax.jdo.option.ConnectionURL",
				"db4o:file:test.db");
		System.setProperty("log4j.configuration", "file:bin/log4j.properties");
		
		d_factory = JDOHelper.getPersistenceManagerFactory(p);
	}
	
	public PersistenceManager getManager() {
		return d_factory.getPersistenceManager();
	}
	
	public void persistObject(Object o) throws Exception {
		PersistenceManager pm = getManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(o);
			tx.commit();
		} catch (Exception ex) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw ex;
		} finally {
			pm.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getObjects(Class<T> type) throws Exception {
		PersistenceManager pm = getManager();
		
		Collection<T> objects = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query q = pm.newQuery(type);
			Collection<T> res = (Collection<T>) q.execute();
			
			objects = pm.detachCopyAll(res);
			
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			pm.close();
		}
		return objects;
	}
	
	public Collection<Element> getElements() throws Exception {
		return getObjects(Element.class);
	}
	
	public Collection<Container> getContainers() throws Exception {
		return getObjects(Container.class);
	}
	
	@Test(expected=javax.jdo.JDOUserException.class)
	public void primaryKeyCheckTest() throws Exception {
		persistObject(new Element());
		persistObject(new Element());
	}

	@Test
	public void dataNucleusBugTest() throws Exception {
		persistObject(new Element());
		
		assertEquals(1, getElements().size());
		assertEquals(new Element(), getElements().iterator().next());
		
		Container c = new Container();
		c.elements.add(getElements().iterator().next());
		persistObject(c);
		
		System.out.println(getElements());
		assertEquals(1, getElements().size());
		assertEquals(new Element(), getElements().iterator().next());
	}
	
	@Test
	public void dataNucleusReAttachTest() throws Exception {
		persistObject(new Element());
		persistObject(new Container());
		
		Container c = getContainers().iterator().next();
		c.elements.add(getElements().iterator().next());
		
		PersistenceManager pm = getManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(c);
			tx.commit();
		} catch (Exception ex) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw ex;
		} finally {
			pm.close();
		}
		
		assertEquals(1, getElements().size());
		assertEquals(new Element(), getElements().iterator().next());
		assertEquals(1, getContainers().size());
		assertEquals(1, getContainers().iterator().next().elements.size());
		assertEquals(new Element(), getContainers().iterator().next().elements.get(0));
	}
	
	@Test(expected=javax.jdo.JDOUserException.class)
	public void dataNucleusReAttachTest2() throws Exception {
		persistObject(new Element());
		persistObject(new Container());
		
		Container c = getContainers().iterator().next();
		c.elements.add(new Element());
		
		PersistenceManager pm = getManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(c);
			tx.commit();
		} catch (Exception ex) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw ex;
		} finally {
			pm.close();
		}
	}
}
