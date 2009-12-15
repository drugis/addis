package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PatientGroupCharacteristicTest {

	@Test
	public void testGetDescription() {
		assertEquals("Male subjects", PatientGroupCharacteristic.MALE.getDescription());
		for (Characteristic c : PatientGroupCharacteristic.values()) {
			assertTrue(c.getDescription().length() > 0);
		}
	}
	
	@Test
	public void testGetValueType() {
		assertEquals(Integer.class, PatientGroupCharacteristic.MALE.getValueType());
		for (Characteristic c : PatientGroupCharacteristic.values()) {
			assertNotNull(c.getValueType());
		}
	}
	
	@Test
	public void testEquals() {
		assertEquals(PatientGroupCharacteristic.MALE, new PatientGroupCharacteristic("Male subjects", Integer.class));
		assertNotSame(PatientGroupCharacteristic.MALE, new PatientGroupCharacteristic("Female subjects", Integer.class));
		assertNotSame(PatientGroupCharacteristic.FEMALE, new PatientGroupCharacteristic("Average age", Double.class));
		CharacteristicsMap map = new CharacteristicsMap();
		map.put(PatientGroupCharacteristic.MALE, 20);
		assertTrue(map.containsKey(PatientGroupCharacteristic.MALE));
		assertEquals(20, map.get(PatientGroupCharacteristic.MALE));
	}
	
	@Test
	public void testHashCode() {
		assertEquals(PatientGroupCharacteristic.MALE.hashCode(), new PatientGroupCharacteristic("Male subjects", Integer.class).hashCode());
		assertNotSame(PatientGroupCharacteristic.MALE.hashCode(), new PatientGroupCharacteristic("Female subjects", Integer.class).hashCode());
		assertNotSame(PatientGroupCharacteristic.FEMALE.hashCode(), new PatientGroupCharacteristic("Average age", Double.class).hashCode());
	}
	
}
