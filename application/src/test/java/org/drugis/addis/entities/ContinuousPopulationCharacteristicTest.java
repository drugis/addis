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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class ContinuousPopulationCharacteristicTest {
	private PopulationCharacteristic d_age;
	
	@Before
	public void setUp() {
		d_age = new PopulationCharacteristic("Age", new ContinuousVariableType());
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
