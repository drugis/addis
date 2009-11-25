package org.drugis.addis.entities;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnknownDoseTest {
	@Test
	public void testEqualsOtherUnknown() {
		assertEquals(new UnknownDose(), new UnknownDose());
	}
	
	@Test
	public void testNotEqualsKnown() {
		assertNotSame(new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY), new UnknownDose());
	}
	
	@Test
	public void testGetQuantity() {
		assertEquals(null, new UnknownDose().getQuantity());
	}
	
	@Test
	public void testGetUnit() {
		assertEquals(null, new UnknownDose().getUnit());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSetUnit() {
		new UnknownDose().setUnit(SIUnit.MILLIGRAMS_A_DAY);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSetQuantity() {
		new UnknownDose().setQuantity(10.0);
	}
	
	@Test
	public void testToString() {
		assertEquals("Unknown Dose", new UnknownDose().toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(1, new UnknownDose().hashCode());
	}
}
