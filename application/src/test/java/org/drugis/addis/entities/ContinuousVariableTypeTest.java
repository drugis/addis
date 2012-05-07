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

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;
public class ContinuousVariableTypeTest {

	private ContinuousVariableType d_var;

	@Before
	public void setUp() {
		d_var = new ContinuousVariableType();
	}
	
	@Test
	public void testBuildMeasurement() {
		assertEntityEquals(new BasicContinuousMeasurement(null, null, 30), d_var.buildMeasurement(30));
		assertEntityEquals(new BasicContinuousMeasurement(null, null, null), d_var.buildMeasurement());
		assertNotNull(d_var.buildMeasurement());
	}
	
	@Test
	public void testGetUnitOfMeasurement() {
		JUnitUtil.testSetter(d_var, ContinuousVariableType.PROPERTY_UNIT_OF_MEASUREMENT, d_var.getUnitOfMeasurement(), "quarts per inch");
		JUnitUtil.testSetter(d_var, ContinuousVariableType.PROPERTY_UNIT_OF_MEASUREMENT, d_var.getUnitOfMeasurement(), "pints per gallon");
	}
	
	@Test
	public void testGetType() {
		assertEquals("Continuous", d_var.getType());
	}
}
