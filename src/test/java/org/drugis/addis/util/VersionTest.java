package org.drugis.addis.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionTest {
	@Test
	public void testToString() {
		String str = "1.10.5-SNAPSHOT";
		assertEquals(str , new Version(str).toString());
	}
	
	@Test
	public void testCompareToOneComponent() {
		assertTrue(new Version("1").compareTo(new Version("1")) == 0);
		assertTrue(new Version("1").compareTo(new Version("2")) < 0);
		assertTrue(new Version("3").compareTo(new Version("2")) > 0);
		assertTrue(new Version("10").compareTo(new Version("2")) > 0);
		assertTrue(new Version("010").compareTo(new Version("10")) == 0);
	}
	
	@Test
	public void testCompareToTwoComponents() {
		assertTrue(new Version("1.0").compareTo(new Version("1.0")) == 0);
		assertTrue(new Version("1.0").compareTo(new Version("1.1")) < 0);
		assertTrue(new Version("1").compareTo(new Version("1.1")) < 0);
		assertTrue(new Version("1.1").compareTo(new Version("1")) > 0);
		assertTrue(new Version("0.8").compareTo(new Version("0.10")) < 0);
	}
	
	@Test
	public void testCompareToMoreComponents() {
		assertTrue(new Version("1.0.1").compareTo(new Version("1.0.1")) == 0);
		assertTrue(new Version("1.0.0").compareTo(new Version("1.0.1")) < 0);
		assertTrue(new Version("1.0").compareTo(new Version("1.0.1")) < 0);
		assertTrue(new Version("1.0.1").compareTo(new Version("1.0")) > 0);
		assertTrue(new Version("1.0.5").compareTo(new Version("1.0.1")) > 0);
	}

	@Test
	public void testCompareWithStringSuffix() {
		assertTrue(new Version("1.0a").compareTo(new Version("1.0")) == 0);
		assertTrue(new Version("0.8").compareTo(new Version("0.8-SNAPSHOT")) == 0);
	}
	
	@Test
	public void testEquals() {
		assertTrue(new Version("1.0").equals(new Version("1.0")));
		assertFalse(new Version("1.0").equals(new Version("1.1")));
		assertFalse(new Version("0.8").equals(new Version("0.10")));
		assertTrue(new Version("1.0.1").equals(new Version("1.0.1")));
		assertFalse(new Version("1.0.0").equals(new Version("1.0.1")));
	}
}
