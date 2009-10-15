package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.drugis.addis.entities.StudyCharacteristic.ValueType;
import org.junit.Test;

public class StudyCharacteristicTest {

	@Test
	public void testGetDescription() {
		assertEquals("Number of study arms", StudyCharacteristic.ARMS.getDescription());
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
	
	@Test
	public void testValueTypeValidate() {
		assertTrue(ValueType.TEXT.validate("text"));
		assertFalse(ValueType.TEXT.validate(new Date()));
		
		assertTrue(ValueType.POSITIVE_INTEGER.validate(new Integer(2)));
		assertFalse(ValueType.POSITIVE_INTEGER.validate(new Integer(0)));
		assertFalse(ValueType.POSITIVE_INTEGER.validate(new Date()));
		
		assertTrue(ValueType.DATE.validate(new Date()));
		assertFalse(ValueType.DATE.validate(new Integer(0)));
		
		assertTrue(ValueType.INDICATION.validate(new Indication(0L, "")));		
		assertFalse(ValueType.INDICATION.validate(new Date()));
		
		assertTrue(ValueType.ALLOCATION.validate(StudyCharacteristic.Allocation.NONRANDOMIZED));
		assertFalse(ValueType.ALLOCATION.validate(new Date()));

		assertTrue(ValueType.BLINDING.validate(StudyCharacteristic.Blinding.DOUBLE_BLIND));
		assertFalse(ValueType.BLINDING.validate(new Date()));

		assertTrue(ValueType.STATUS.validate(StudyCharacteristic.Status.FINISHED));
		assertFalse(ValueType.STATUS.validate(new Date()));
	}

}
