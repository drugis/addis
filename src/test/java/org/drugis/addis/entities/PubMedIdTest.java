package org.drugis.addis.entities;

import static org.junit.Assert.*;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class PubMedIdTest {
	@Test(expected=IllegalArgumentException.class)
	public void testNonDigitThrowsException() {
		new PubMedId("123a45");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonDigitThrowsException2() {
		new PubMedId("12345x");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonDigitThrowsException3() {
		new PubMedId("x12345");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEmptyThrowsException() {
		new PubMedId("");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNullThrowsException() {
		new PubMedId(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLeadingZeroThrowsException3() {
		new PubMedId("012345");
	}
	
	@Test
	public void testEquals() {
		assertEquals(new PubMedId("12345"), new PubMedId("12345"));
		JUnitUtil.assertNotEquals(new PubMedId("12345"), new PubMedId("12346"));
	}
}
