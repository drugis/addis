/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class CategoricalPopulationCharacteristicTest {
	CategoricalPopulationCharacteristic d_gender;
	
	@Before
	public void setUp() {
		d_gender = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
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
		assertArrayEquals(d_gender.getCategories(), ((FrequencyMeasurement)m).getCategories());
		assertEquals(null, m.getSampleSize());
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
	public void testClone() {
		CategoricalPopulationCharacteristic clone_var = d_gender.clone();
		assertTrue(d_gender.deepEquals(clone_var));
		assertNotSame(d_gender, clone_var);
	}
}
