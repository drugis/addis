package org.drugis.addis.util.JSMAAintegration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.Interval;

public class LogitNormalMeasurementTest {
	private LogitNormalMeasurement d_m;
	
	@Before
	public void setUp() {
		d_m = new LogitNormalMeasurement(0.1, 0.2);
	}
	
	@Test
	public void testGetRange() {
		Interval rng = d_m.getRange();
		assertEquals(0.4275161, rng.getStart(), 0.00001);		
		assertEquals(0.6205758, rng.getEnd(), 0.00001);
	}

	@Test
	public void testEquals() {
		GaussianMeasurement m2 = new GaussianMeasurement(0.1, 0.2);
		assertFalse(d_m.equals(m2));
		assertTrue(d_m.equals(new LogitNormalMeasurement(0.1, 0.2)));
	}
	
	@Test
	public void testDeepCopy() {
		LogitNormalMeasurement m2 = d_m.deepCopy();
		assertNotSame(d_m, m2);
		assertEquals(d_m.getMean(), m2.getMean(), 0.000000001);
		assertEquals(d_m.getStDev(), m2.getStDev(), 0.000000001);
	}	
	
	@Test
	public void testSample() {
		LogitNormalMeasurement m = new LogitNormalMeasurement(0.0, 0.0);
		assertEquals(0.5, m.sample(), 0.0000001);
	}
}
