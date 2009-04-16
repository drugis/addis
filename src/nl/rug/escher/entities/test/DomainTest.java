package nl.rug.escher.entities.test;

import static org.junit.Assert.*;

import java.util.Collections;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.DomainImpl;
import nl.rug.escher.entities.Endpoint;

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
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddEndpointNull() {
		d_domain.addEndpoint(null);
	}

	@Test
	public void testAddEndpoint() {
		Endpoint e = new Endpoint();
		assertEquals(0, d_domain.getEndpoints().size());
		d_domain.addEndpoint(e);
		assertEquals(1, d_domain.getEndpoints().size());
		assertEquals(Collections.singletonList(e), d_domain.getEndpoints());
	}

}
