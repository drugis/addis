package org.drugis.addis.mcmcmodel;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathUtilTest {

	@Test(expected = IllegalArgumentException.class)
	public void testLogitThrowsTooLow() {
		MathUtil.logit(0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLogitThrowsTooHigh() {
		MathUtil.logit(1);
	}
	
	@Test
	public void testLogit() {
		assertEquals(0.0, MathUtil.logit(0.5),0.0000001);
		assertEquals(Math.log(0.5), MathUtil.logit(1.0/3.0),0.0000001);
		assertEquals(Math.log(1.0/9.0), MathUtil.logit(0.1),0.0000001);
		assertEquals(Math.log(9.0), MathUtil.logit(0.9),0.0000001);
	}
	
	@Test
	public void testIlogit() {
		double[] xarr = {-10, 5, 0, 20, 23.7, 0.3};
		for (double x : xarr) {
			assertEquals(x, MathUtil.logit(MathUtil.ilogit(x)), 0.000001);
		}
	}
}
