package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
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
	
	@Test
	public void testEquals() {
		assertEquals(PopulationCharacteristic.MALE, new PopulationCharacteristic("Male subjects", Integer.class));
		assertNotSame(PopulationCharacteristic.MALE, new PopulationCharacteristic("Female subjects", Integer.class));
		assertNotSame(PopulationCharacteristic.FEMALE, new PopulationCharacteristic("Average age", Double.class));
		CharacteristicsMap map = new CharacteristicsMap();
		map.put(PopulationCharacteristic.MALE, 20);
		assertTrue(map.containsKey(PopulationCharacteristic.MALE));
		assertEquals(20, map.get(PopulationCharacteristic.MALE));
	}
	
	@Test
	public void testHashCode() {
		assertEquals(PopulationCharacteristic.MALE.hashCode(), new PopulationCharacteristic("Male subjects", Integer.class).hashCode());
		assertNotSame(PopulationCharacteristic.MALE.hashCode(), new PopulationCharacteristic("Female subjects", Integer.class).hashCode());
		assertNotSame(PopulationCharacteristic.FEMALE.hashCode(), new PopulationCharacteristic("Average age", Double.class).hashCode());
	}
	
}
