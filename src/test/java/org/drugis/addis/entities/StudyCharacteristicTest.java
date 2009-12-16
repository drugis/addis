package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StudyCharacteristicTest {

	@Test
	public void testGetDescription() {
		assertEquals("Number of study centers", BasicStudyCharacteristic.CENTERS.getDescription());
		for (Characteristic c : BasicStudyCharacteristic.values()) {
			assertTrue(c.getDescription().length() > 0);
		}
	}
	
	@Test
	public void testGetValueType() {
		for (Characteristic c : BasicStudyCharacteristic.values()) {
			assertNotNull(c.getValueType());
		}
	}
}
