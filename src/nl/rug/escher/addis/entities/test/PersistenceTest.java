package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.gui.MainData;

import org.junit.Before;
import org.junit.Test;

public class PersistenceTest {
	@Before
	public void setUp() {
		File f = new File("test.db");
		if (f.exists() && !f.delete()) {
			throw new RuntimeException();
		}
	}
	
	@Test
	public void testPersistEndpoint() {
		PersistenceManagerFactory pmf = getFactory();
		PersistenceManager pm = pmf.getPersistenceManager();
	
	    Endpoint endpoint = MainData.buildEndpointHamd(new DomainImpl());
	    try {
			Transaction tx = pm.currentTransaction();
			try {
			    tx.begin();
			    pm.makePersistent(endpoint);
			    tx.commit();
			} finally {
			    if (tx.isActive()) {
			        tx.rollback();
			    }
			}
			
			tx = pm.currentTransaction();
			try {
				tx.begin();
				
				Extent<Endpoint> extent = pm.getExtent(Endpoint.class);
				Iterator<Endpoint> it = extent.iterator();
				assertTrue(it.hasNext());
				assertEquals(endpoint, it.next());
				assertFalse(it.hasNext());
				tx.commit();
			} finally {
			    if (tx.isActive()) {
			        tx.rollback();
			    }
			}
	    } finally {
		    pm.close();
	    }
	}
	
	@Test(expected=JDOUserException.class)
	public void testDuplicateEndpoint() {
		PersistenceManagerFactory pmf = getFactory();
		PersistenceManager pm = pmf.getPersistenceManager();
	
		Endpoint e1 = new Endpoint("e1");
		Endpoint e2 = new Endpoint("e1");
	    try {
			Transaction tx = pm.currentTransaction();
			try {
			    tx.begin();
			    pm.makePersistent(e1);
			    pm.makePersistent(e2);
			    tx.commit();
			} finally {
			    if (tx.isActive()) {
			        tx.rollback();
			    }
			}
	    } finally {
		    pm.close();
	    }
	}

	private PersistenceManagerFactory getFactory() {
		return JDOHelper.getPersistenceManagerFactory("datanucleus.test.properties");
	}
}
