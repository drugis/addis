package nl.rug.escher.entities.test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.Collections;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.DomainImpl;
import nl.rug.escher.entities.DomainListener;
import nl.rug.escher.entities.Drug;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Study;

import org.junit.Before;
import org.junit.Test;

public class DomainTest {

	private Domain d_domain;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
	}
	
	@Test
	public void testEmptyDomain() {
		assertTrue(d_domain.getEndpoints().isEmpty());
		assertTrue(d_domain.getStudies().isEmpty());
		assertTrue(d_domain.getDrugs().isEmpty());
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddEndpointNull() {
		d_domain.addEndpoint(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddStudyNull() {
		d_domain.addStudy(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddDrugNull() {
		d_domain.addStudy(null);
	}

	@Test
	public void testAddEndpoint() {
		Endpoint e = new Endpoint();
		assertEquals(0, d_domain.getEndpoints().size());
		d_domain.addEndpoint(e);
		assertEquals(1, d_domain.getEndpoints().size());
		assertEquals(Collections.singletonList(e), d_domain.getEndpoints());
	}
	
	@Test
	public void testAddStudy() {
		Study s = new Study();
		assertEquals(0, d_domain.getStudies().size());
		d_domain.addStudy(s);
		assertEquals(1, d_domain.getStudies().size());
		assertEquals(Collections.singletonList(s), d_domain.getStudies());
	}
	
	@Test
	public void testAddDrug() {
		Drug d = new Drug();
		assertEquals(0, d_domain.getDrugs().size());
		d_domain.addDrug(d);
		assertEquals(1, d_domain.getDrugs().size());
		assertEquals(Collections.singletonList(d), d_domain.getDrugs());
	}
	
	@Test
	public void testAddEndpointListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.endpointsChanged();
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addEndpoint(new Endpoint());
		verify(mockListener);
	}
	
	@Test
	public void testAddStudyListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.studiesChanged();
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addStudy(new Study());
		verify(mockListener);
	}
	
	@Test
	public void testAddDrugListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.drugsChanged();
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addDrug(new Drug());
		verify(mockListener);
	}
}