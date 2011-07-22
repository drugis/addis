package org.drugis.addis.entities;

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class CategoricalVariableTypeTest {
	private CategoricalVariableType d_var;
	private List<String> d_cats;

	@Before
	public void setUp() {
		d_cats = Arrays.asList("Male", "Female");
		d_var = new CategoricalVariableType(d_cats);
	}
	
	@Test
	public void testBuildMeasurement() {
		assertEntityEquals(new FrequencyMeasurement(d_cats.toArray(new String[]{}), new HashMap<String, Integer>()), d_var.buildMeasurement(30));
		assertEntityEquals(new FrequencyMeasurement(d_cats.toArray(new String[]{}), new HashMap<String, Integer>()), d_var.buildMeasurement());
		assertNotNull(d_var.buildMeasurement());
	}
	
	@Test
	public void testGetType() {
		assertEquals("Categorical", d_var.getType());
	}
	
	@Test
	public void testCategories() {
		assertEquals(d_cats, d_var.getCategories());
		d_var.getCategories().add("Trans gender");
		assertEquals(2, d_cats.size());
		assertEquals(3, d_var.getCategories().size());
	}

	@Test
	public void testEquals() {
		JUnitUtil.assertNotEquals(d_var, null);
		JUnitUtil.assertNotEquals(d_var, new RateVariableType());
		
		CategoricalVariableType var2 = new CategoricalVariableType(d_cats);
		assertEquals(d_var, var2);
		assertEquals(d_var.hashCode(), var2.hashCode());

		var2.getCategories().add("Trans gender");
		JUnitUtil.assertNotEquals(d_var, var2);
	}
}
