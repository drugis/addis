package org.drugis.addis.entities;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class ContinuousVariableTest {
	private ContinuousVariable d_age;
	
	@Before
	public void setUp() {
		d_age = new ContinuousVariable("Age");
	}
	
	@Test
	public void testGetName() {
		assertEquals("Age", d_age.getName());
	}
	
	@Test
	public void testGetDependencies() {
		assertEquals(Collections.emptySet(), d_age.getDependencies());
	}
	
	@Test
	public void testBuildMeasurement() {
		Measurement m = d_age.buildMeasurement();
		assertTrue(m instanceof ContinuousMeasurement);
	}
}
