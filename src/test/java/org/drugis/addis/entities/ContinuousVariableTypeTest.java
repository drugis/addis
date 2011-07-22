package org.drugis.addis.entities;

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;
public class ContinuousVariableTypeTest {

	private ContinuousVariableType d_var;

	@Before
	public void setUp() {
		d_var = new ContinuousVariableType();
	}
	
	@Test
	public void testBuildMeasurement() {
		assertEntityEquals(new BasicContinuousMeasurement(null, null, 30), d_var.buildMeasurement(30));
		assertEntityEquals(new BasicContinuousMeasurement(null, null, null), d_var.buildMeasurement());
		assertNotNull(d_var.buildMeasurement());
	}
	
	@Test
	public void testGetUnitOfMeasurement() {
		JUnitUtil.testSetter(d_var, ContinuousVariableType.PROPERTY_UNIT_OF_MEASUREMENT, d_var.getUnitOfMeasurement(), "quarts per inch");
		JUnitUtil.testSetter(d_var, ContinuousVariableType.PROPERTY_UNIT_OF_MEASUREMENT, d_var.getUnitOfMeasurement(), "pints per gallon");
	}
	
	@Test
	public void testGetType() {
		assertEquals("Continuous", d_var.getType());
	}
}
