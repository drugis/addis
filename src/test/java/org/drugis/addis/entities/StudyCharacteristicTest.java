package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StudyCharacteristicTest {

	@Test
	public void testGetDescription() {
		assertEquals("Study Arms", StudyCharacteristic.ARMS.getDescription());
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			assertTrue(c.getDescription().length() > 0);
		}
	}
	
	@Test
	public void testGetValueType() {
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			assertNotNull(c.getValueType());
		}
	}
}
