package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnmodifiableHolderTest {
	@Test
	public void testGetValue() {
		assertEquals("XXX", new UnmodifiableHolder<String>("XXX").getValue());
	}
	
	@Test(expected=Exception.class)
	public void testSetValue() {
		new UnmodifiableHolder<String>("").setValue("XXX");
	}
}
