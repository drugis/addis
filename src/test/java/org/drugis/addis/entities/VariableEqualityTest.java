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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class VariableEqualityTest {

	private AdverseEvent d_ade;
	private Endpoint d_ep;
	private CategoricalPopulationCharacteristic d_gender;
	private RatePopulationCharacteristic d_rate;
	private ContinuousPopulationCharacteristic d_cont;


	@Before
	public void setUp() {
		d_ade = new AdverseEvent("name", Variable.Type.RATE);
		d_ep = new Endpoint("apocalypse", Variable.Type.CONTINUOUS);
		d_gender = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
		d_rate = new RatePopulationCharacteristic("Test");
		d_cont = new ContinuousPopulationCharacteristic("TestCont");
	}
	
	@Test
	public void testAdverseEventEquals() {
		assertFalse(d_ade.equals(new Endpoint("name", Variable.Type.RATE)));
		assertFalse(d_ade.equals(new AdverseEvent("Wrong name", Variable.Type.RATE)));
		assertTrue(d_ade.equals(new AdverseEvent("name", Variable.Type.RATE)));
	}
	
	@Test
	public void testAdverseEventDeepEquals() {
		// Test class
		assertFalse(d_ade.deepEquals(new Endpoint(d_ade.getName(), d_ade.getType())));
		
		// Test common fields
		AdverseEvent var2 = new AdverseEvent(d_ade.getName(), d_ade.getType());
		testCommonFields(d_ade, var2);
		
		var2.setDirection(OutcomeMeasure.Direction.HIGHER_IS_BETTER);
		assertFalse(d_ade.deepEquals(var2));
	}
	
	@Test
	public void testEndpointDeepEquals() {
		// Test class
		assertFalse(d_ep.deepEquals(new AdverseEvent(d_ep.getName(), d_ep.getType())));
		
		// Test common fields
		Endpoint var2 = new Endpoint(d_ep.getName(), d_ep.getType());
		testCommonFields(d_ep, var2);

		var2.setDirection(OutcomeMeasure.Direction.LOWER_IS_BETTER);
		assertFalse(d_ep.deepEquals(var2));
	}
	
	@Test
	public void testCategoricalPopCharDeepEquals() {
		// Test class
		assertFalse(d_gender.deepEquals(new AdverseEvent(d_gender.getName(), d_gender.getType())));
		
		// Test common fields
		CategoricalPopulationCharacteristic var2 = new CategoricalPopulationCharacteristic(d_gender.getName(),
				d_gender.getCategories());
		testCommonFields(d_gender, var2);

		var2.setCategories(new String[] {"Mars", "Venus"});
		assertFalse(d_gender.deepEquals(var2));
	}
	
	@Test
	public void testRatePopCharDeepEquals() {
		// Test class
		assertFalse(d_rate.deepEquals(new AdverseEvent(d_rate.getName(), d_rate.getType())));
		
		// Test common fields
		RatePopulationCharacteristic var2 = new RatePopulationCharacteristic(d_rate.getName());
		testCommonFields(d_rate, var2);
	}

	@Test
	public void testContinuousPopCharDeepEquals() {
		// Test class
		assertFalse(d_cont.deepEquals(new AdverseEvent(d_cont.getName(), d_cont.getType())));
		
		// Test common fields
		ContinuousPopulationCharacteristic var2 = new ContinuousPopulationCharacteristic(d_cont.getName());
		testCommonFields(d_cont, var2);
	}

	private void testCommonFields(Variable var1, Variable var2) {
		// Passed objects should be equal
		assertTrue(var1.deepEquals(var2));
		
		// Equal on Name
		String name = var2.getName();
		var2.setName("Wrong title");
		assertFalse(var1.deepEquals(var2));
		var2.setName(name);
		
		// Equal on Type
		var1.setType(Variable.Type.RATE);
		var2.setType(Variable.Type.CONTINUOUS);
		assertFalse(var1.deepEquals(var2));
		var2.setType(Variable.Type.RATE);
		
		// Equal on description
		var1.setDescription("defcrib");
		var2.setDescription("nuffink");
		assertFalse(var1.deepEquals(var2));
		var2.setDescription(var1.getDescription());
		assertTrue(var1.deepEquals(var2));
		
		// Equal on unit of measurement
		var1.setUnitOfMeasurement("inch");
		var2.setUnitOfMeasurement("cm");
		assertFalse(var1.deepEquals(var2));
		var2.setUnitOfMeasurement(var1.getUnitOfMeasurement());
		assertTrue(var1.deepEquals(var2));
	}

	@Test
	public void testEndpointEquals() {
		String name1 = "Endpoint A";
		String name2 = "Endpoint B";
		
		assertEquals(new Endpoint(name1, Variable.Type.RATE), new Endpoint(name1, Variable.Type.RATE));
		JUnitUtil.assertNotEquals(new Endpoint(name1, Variable.Type.RATE), new Endpoint(name2, Variable.Type.RATE));
		assertEquals(new Endpoint(name1, Variable.Type.RATE).hashCode(), new Endpoint(name1, Variable.Type.RATE).hashCode());
	}
	
	@Test
	public void testCategoricalPopCharEquals() {
		CategoricalPopulationCharacteristic gender2 = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
		assertEquals(d_gender, gender2);
		assertEquals(d_gender.hashCode(), gender2.hashCode());
		
		gender2 = new CategoricalPopulationCharacteristic("Gender2", new String[]{"Male", "Female"});
		assertFalse(gender2.equals(d_gender));

		gender2 = new CategoricalPopulationCharacteristic(null, new String[]{"Male", "Female"});
		assertFalse(gender2.equals(d_gender));

		assertFalse(gender2.equals(new Integer(2)));
		
		// Equality only on Name within PopulationCharacteristic hierarchy
		assertEquals(d_gender, new RatePopulationCharacteristic(d_gender.getName()));
	}
}
