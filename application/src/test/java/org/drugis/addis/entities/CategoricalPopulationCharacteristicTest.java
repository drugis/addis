/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class CategoricalPopulationCharacteristicTest {
	PopulationCharacteristic d_gender;
	
	@Before
	public void setUp() {
		d_gender = new PopulationCharacteristic("Gender", new CategoricalVariableType(Arrays.asList((new String[]{"Male", "Female"}))));
	}
	
	@Test
	public void testCategories() {
		String[] cats = {"Male", "Female"};
		assertEquals(cats[0], ((CategoricalVariableType) d_gender.getVariableType()).getCategories().get(0));
		assertEquals(cats[1], ((CategoricalVariableType) d_gender.getVariableType()).getCategories().get(1));
		assertEquals(cats.length, ((CategoricalVariableType) d_gender.getVariableType()).getCategories().size());
	}
	
	@Test
	public void testGetName() {
		assertEquals("Gender", d_gender.getName());
	}
	
	@Test
	public void testBuildMeasurement() {
		Measurement m = d_gender.buildMeasurement();
		assertTrue(m instanceof FrequencyMeasurement);
		assertArrayEquals(((CategoricalVariableType) d_gender.getVariableType()).getCategories().toArray(), ((FrequencyMeasurement)m).getCategories());
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
}
