package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class LogContinuousMeasurementEstimateTest {

	private LogContinuousMeasurementEstimate d_logEstimate;

	@Before
	public void setUp() {
		d_logEstimate = new LogContinuousMeasurementEstimate(1.73, 0.42);
	}
	
	private BasicContinuousMeasurement getMeasurement() {
		return d_logEstimate;
	}
	
	@Test
	public void testGetSetMean() {
		JUnitUtil.testSetter(getMeasurement(),
				LogContinuousMeasurementEstimate.PROPERTY_MEAN, 1.73, 25.91);
	}
	
	@Test
	public void testGetSetStdDev() {
		JUnitUtil.testSetter(getMeasurement(), LogContinuousMeasurementEstimate.PROPERTY_STDDEV, 0.42, 0.46);
	}
	
	@Test
	public void testGetConfidenceInterval() {
		assertEquals(2.476423,  d_logEstimate.getConfidenceInterval().getLowerBound(), 0.000001);
		assertEquals(12.847958, d_logEstimate.getConfidenceInterval().getUpperBound(), 0.000001);
	}
	
	@Test
	public void testGetExpMean() {
		assertEquals(Math.exp(1.73), d_logEstimate.getExpMean(),0.00001);
	}
	
	@Test
	public void testToString() {
		assertEquals("5.641 (2.476, 12.848)", d_logEstimate.toString());
	}
}
