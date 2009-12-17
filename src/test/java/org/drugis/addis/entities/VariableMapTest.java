package org.drugis.addis.entities;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class VariableMapTest {
	private VariableMap d_map;
	private ContinuousVariable d_age;
	
	@Before
	public void setUp() {
		d_map = new VariableMap();
		d_age = new ContinuousVariable("Age");
		d_map.put(d_age, d_age.buildMeasurement());
	}
	@Test
	public void testGetDependencies() {
		assertEquals(Collections.singleton(d_age), d_map.getDependencies());
	}
}
