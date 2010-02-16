package org.drugis.addis.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AdverseEventTest {

	private AdverseEvent d_ade;

	@Before
	public void setUp() {
		d_ade = new AdverseEvent("name", Variable.Type.RATE);
	}
	
	@Test
	public void testEquals() {
		assertFalse(d_ade.equals(new Endpoint("name", Variable.Type.RATE)));
		assertTrue(d_ade.equals(new AdverseEvent("name", Variable.Type.RATE)));
	}
}
