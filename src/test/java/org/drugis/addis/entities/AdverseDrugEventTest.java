package org.drugis.addis.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.entities.OutcomeMeasure.Type;
import org.junit.Before;
import org.junit.Test;

public class AdverseDrugEventTest {

	private AdverseDrugEvent ade;

	@Before
	public void setUp() {
		ade = new AdverseDrugEvent("name", Type.RATE);
	}
	
	@Test
	public void testEquals() {
		assertFalse(ade.equals(new Endpoint("name", Type.RATE)));
		assertTrue(ade.equals(new AdverseDrugEvent("name", Type.RATE)));
	}
}
