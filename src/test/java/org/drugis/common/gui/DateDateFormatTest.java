package org.drugis.common.gui;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

public class DateDateFormatTest {

	private DayDateFormat d_format;

	@Before
	public void setUp() {
		d_format = new DayDateFormat();
	}
	
	@Test
	public void testFormat() {
		GregorianCalendar date = new GregorianCalendar(2001, 10, 20);
		String expected = "20 Nov 2001";
		assertEquals(expected, d_format.format(date.getTime()));
	}
	
	@Test
	public void testNullFormat() {
		assertEquals("", d_format.format(null));
	}
	
	
}
