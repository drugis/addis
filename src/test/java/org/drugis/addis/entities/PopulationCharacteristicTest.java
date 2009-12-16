package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PopulationCharacteristicTest {

	@Test
	public void testGetDescription() {
		assertEquals("Male subjects", PopulationCharacteristic.MALE.getDescription());
		for (Characteristic c : PopulationCharacteristic.values()) {
			assertTrue(c.getDescription().length() > 0);
		}
	}
	
	@Test
	public void testGetValueType() {
		assertEquals(Integer.class, PopulationCharacteristic.MALE.getValueType());
		for (Characteristic c : PopulationCharacteristic.values()) {
			assertNotNull(c.getValueType());
		}
	}
}
