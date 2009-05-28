package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;

import java.io.File;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainPersistent;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Study;

public class DomainPersistentTest {
	@Before
	public void setUp() {
		File f = new File("test.db");
		if (f.exists() && !f.delete()) {
			throw new RuntimeException();
		}
	}
	
	private PersistenceManagerFactory getFactory() {
		return JDOHelper.getPersistenceManagerFactory("datanucleus.test.properties");
	}
	
	@Test
	public void testPersistEndpoint() {
		Domain domain = new DomainPersistent(getFactory());
		
		Endpoint e = new Endpoint("e");
		assertEquals("e", e.getName());
		domain.addEndpoint(e);
		
		assertTrue(domain.getEndpoint("e") != null);
		assertEquals("e", domain.getEndpoint("e").getName());
	}
	
	@Test @Ignore("Blocked by DataNucleus bug that needs work-around")
	public void testPersistStudyWithEndpoint() {
		Domain domain = new DomainPersistent(getFactory());
		domain.addEndpoint(new Endpoint("e"));
		
		Study s = new Study();
		s.setId("STUDY");
		s.setEndpoints(new ArrayList<Endpoint>(domain.getEndpoints()));
		
		domain.addStudy(s);
		
		assertNotNull(domain.getStudy("STUDY"));
		assertEquals(1, domain.getStudy("STUDY").getEndpoints().size());
		assertEquals(1, domain.getEndpoints().size());
	}
}
