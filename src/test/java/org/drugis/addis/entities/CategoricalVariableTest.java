package org.drugis.addis.entities;

import static org.junit.Assert.*;

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
}
