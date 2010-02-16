package org.drugis.addis.entities;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class ContinuousPopulationCharacteristicTest {
	private ContinuousPopulationCharacteristic d_age;
	
	@Before
	public void setUp() {
		d_age = new ContinuousPopulationCharacteristic("Age");
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
	
	@Test
	public void testToString() {
		assertEquals(d_age.getName(), d_age.toString());
	}
}
