package org.drugis.addis.entities;

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class RateVariableTypeTest {

	private RateVariableType d_var;

	@Before
	public void setUp() {
		d_var = new RateVariableType();
	}
	
	@Test
	public void testBuildMeasurement() {
		assertEntityEquals(new BasicRateMeasurement(null, 30)	, d_var.buildMeasurement(30));
		assertEntityEquals(new BasicRateMeasurement(null, null)	, d_var.buildMeasurement());
		assertNotNull(d_var.buildMeasurement());
	}
	
	@Test
	public void testGetType() {
		assertEquals("Rate", d_var.getType());
	}
}
