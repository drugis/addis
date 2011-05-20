package org.drugis.addis.util;

import static org.junit.Assert.assertEquals;

import java.text.NumberFormat;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class MissingValueFormatTest {
	
	private MissingValueFormat d_mvnf;
	private NumberFormat d_integerInstance;
	
	@Before
	public void setUp() {
		d_integerInstance = NumberFormat.getIntegerInstance();
		d_mvnf = new MissingValueFormat(d_integerInstance);
	}

	@Test
	public void testPreservedFunctionality() {
		assertEquals(d_integerInstance.format(-5.5), d_mvnf.format(-5.5));
		assertEquals(d_integerInstance.format(10L), d_mvnf.format(10L));
	}

	@Test
	public void testNA() throws ParseException {
		assertEquals("N/A", d_mvnf.format(null));
		assertEquals(null, d_mvnf.parseObject("N/A"));
	}
	
}
