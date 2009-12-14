package org.drugis.addis.entities;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DerivedStudyCharacteristicTest {

	@Test
	public void DerivedStudyCharacteristicCorrectType() {
		assertTrue(DerivedStudyCharacteristic.DRUGS instanceof DerivedStudyCharacteristic);
	}
	
}
