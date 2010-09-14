package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.*;

import org.junit.Test;

public class BetaTest {
	private static final double EPSILON = 0.000001;
	
	@Test
	public void testGetters() {
		assertEquals(7, new Beta(7, 13).getAlpha(), EPSILON);
		assertEquals(13, new Beta(7, 13).getBeta(), EPSILON);
	}

	@Test
	public void testAxisTypeShouldBeLinear() {
		assertEquals(AxisType.LINEAR, new Beta(1, 1).getAxisType());
	}
	
	@Test
	public void testGetQuantile() {
		assertEquals(0.025, new Beta(1, 1).getQuantile(0.025), EPSILON);
		assertEquals(0.5, new Beta(1, 1).getQuantile(0.5), EPSILON);
		assertEquals(0.95, new Beta(1, 1).getQuantile(0.95), EPSILON);
		
		assertEquals(0.07820626, new Beta(5, 18).getQuantile(0.025), EPSILON);
		assertEquals(0.12150867, new Beta(5, 18).getQuantile(0.12), EPSILON);
		assertEquals(0.20910650, new Beta(5, 18).getQuantile(0.5), EPSILON);
		assertEquals(0.35124865, new Beta(5, 18).getQuantile(0.93), EPSILON);
	}
}
