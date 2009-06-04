package nl.rug.escher.addis.entities.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.DomainListener;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.JUnitUtil;

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
		assertEquals(Collections.singleton(e), d_domain.getEndpoints());
	}
	
	@Test
	public void testAddStudy() {
		Study s = new Study();
		assertEquals(0, d_domain.getStudies().size());
		d_domain.addStudy(s);
		assertEquals(1, d_domain.getStudies().size());
		assertEquals(Collections.singleton(s), d_domain.getStudies());
	}
	
	@Test
	public void testAddDrug() {
		Drug d = new Drug();
		assertEquals(0, d_domain.getDrugs().size());
		d_domain.addDrug(d);
		assertEquals(1, d_domain.getDrugs().size());
		assertEquals(Collections.singleton(d), d_domain.getDrugs());
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
	
	@Test
	public void testGetStudiesByEndpoint() {
		Endpoint e1 = new Endpoint();
		e1.setName("e1");
		Endpoint e2 = new Endpoint();
		e2.setName("e2");
		Endpoint e3 = new Endpoint();
		e3.setName("e3");
		
		List<Endpoint> l1 = new ArrayList<Endpoint>();
		l1.add(e1);
		Study s1 = new Study();
		s1.setId("s1");
		s1.setEndpoints(l1);
		
		List<Endpoint> l2 = new ArrayList<Endpoint>();
		l2.add(e2);
		l2.add(e1);
		Study s2 = new Study();
		s2.setId("s2");
		s2.setEndpoints(l2);
		
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		assertEquals(2, d_domain.getStudies(e1).size());
		assertEquals(1, d_domain.getStudies(e2).size());
		assertEquals(0, d_domain.getStudies(e3).size());
		
		assertTrue(d_domain.getStudies(e1).contains(s1));
		assertTrue(d_domain.getStudies(e1).contains(s2));
		assertTrue(d_domain.getStudies(e2).contains(s2));
	}
	
	@Test
	public void testEquals() {
		Domain d1 = new DomainImpl();
		Domain d2 = new DomainImpl();
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		Endpoint e1 = new Endpoint("e1");
		Endpoint e2 = new Endpoint("e2");
		d1.addEndpoint(e1);
		d1.addEndpoint(e2);
		d2.addEndpoint(e1);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.addEndpoint(e2);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		Drug d = new Drug("d1");
		d1.addDrug(d);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.addDrug(d);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		Study s = new Study("s1");
		d1.addStudy(s);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.addStudy(s);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
	}
}