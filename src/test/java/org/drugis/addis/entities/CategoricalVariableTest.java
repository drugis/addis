package org.drugis.addis.entities;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class CategoricalVariableTest {
	CategoricalVariable d_gender;
	
	@Before
	public void setUp() {
		d_gender = new CategoricalVariable("Gender", new String[]{"Male", "Female"});
	}
	
	@Test
	public void testCategories() {
		String[] cats = {"Male", "Female"};
		assertEquals(cats[0], d_gender.getCategories()[0]);
		assertEquals(cats[1], d_gender.getCategories()[1]);
		assertEquals(cats.length, d_gender.getCategories().length);
	}
	
	@Test
	public void testGetName() {
		assertEquals("Gender", d_gender.getName());
	}
	
	@Test
	public void testBuildMeasurement() {
		Measurement m = d_gender.buildMeasurement();
		assertTrue(m instanceof FrequencyMeasurement);
		assertEquals(d_gender, ((FrequencyMeasurement)m).getCategoricalVariable());
		assertEquals(new Integer(0), m.getSampleSize());
	}
	
	@Test
	public void testGetDependencies() {
		assertEquals(Collections.emptySet(), d_gender.getDependencies());
	}
	
	@Test
	public void testToString() {
		assertEquals(d_gender.getName(), d_gender.toString());
	}
	
	@Test
	public void testEquals() {
		CategoricalVariable gender2 = new CategoricalVariable("Gender", new String[]{"Male", "Female"});
		assertTrue(gender2.equals(d_gender));
		
		gender2 = new CategoricalVariable("Gender", new String[]{"Male"});
		assertFalse(gender2.equals(d_gender));

		gender2 = new CategoricalVariable("Gender2", new String[]{"Male", "Female"});
		assertFalse(gender2.equals(d_gender));

		gender2 = new CategoricalVariable(null, new String[]{"Male", "Female"});
		assertFalse(gender2.equals(d_gender));

		assertFalse(gender2.equals(new Integer(2)));
	}
}
