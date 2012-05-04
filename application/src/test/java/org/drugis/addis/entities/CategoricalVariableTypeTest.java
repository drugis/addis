/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		assertEntityEquals(new FrequencyMeasurement(d_cats, new HashMap<String, Integer>()), d_var.buildMeasurement(30));
		assertEntityEquals(new FrequencyMeasurement(d_cats, new HashMap<String, Integer>()), d_var.buildMeasurement());
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
