package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class UnitTest {

	private Unit d_gram;
	private Unit d_liter;

	@Before
	public void setUp() {
		d_gram = new Unit("gram", "g");
		d_liter = new Unit("liter", "l");
	}
	
	@Test
	public void testCompare() {
		assertTrue(d_gram.compareTo(d_liter) < 0);
		assertTrue(d_gram.compareTo(d_gram) == 0);
	}
	
	@Test
	public void testEqualities() {
		assertFalse(d_gram.equals(d_liter));
		assertFalse(d_gram.deepEquals(d_liter));
		Unit badGram = new Unit("gram", "bg");
		assertTrue(d_gram.equals(badGram));
		assertEquals(d_gram.hashCode(), badGram.hashCode());
		assertFalse(d_gram.deepEquals(badGram));
	}
	
	@Test
	public void testEventFires() {
		JUnitUtil.testSetter(d_gram, Unit.PROPERTY_SYMBOL, "g", "Graeme");
	}
	
}
