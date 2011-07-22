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

import java.util.Arrays;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class VariableEqualityTest {

	private OutcomeMeasure d_ade;
	private Endpoint d_ep;
	private PopulationCharacteristic d_gender;
	private PopulationCharacteristic d_rate;
	private PopulationCharacteristic d_cont;


	@Before
	public void setUp() {
		d_ade = new AdverseEvent("name", AdverseEvent.convertVarType(Variable.Type.RATE));
		d_ep = new Endpoint("apocalypse", Endpoint.convertVarType(Variable.Type.CONTINUOUS));
		d_gender = new PopulationCharacteristic("Gender", new CategoricalVariableType(Arrays.asList((new String[]{"Male", "Female"}))));
		d_rate = new PopulationCharacteristic("Test", new RateVariableType());
		d_cont = new PopulationCharacteristic("TestCont", new ContinuousVariableType());
	}
	
	@Test
	public void testAdverseEventEquals() {
		assertFalse(d_ade.equals(new Endpoint("name", Endpoint.convertVarType(Variable.Type.RATE))));
		assertFalse(d_ade.equals(new AdverseEvent("Wrong name", AdverseEvent.convertVarType(Variable.Type.RATE))));
		assertTrue(d_ade.equals(new AdverseEvent("name", AdverseEvent.convertVarType(Variable.Type.RATE))));
	}
	
	@Test
	public void testAdverseEventDeepEquals() {
		// Test class
		assertFalse(d_ade.deepEquals(new Endpoint(d_ade.getName(), d_ade.getVariableType())));
		
		// Test common fields
		AdverseEvent var2 = new AdverseEvent(d_ade.getName(), d_ade.getVariableType());
		testCommonFields(d_ade, var2);
		
		var2.setDirection(OutcomeMeasure.Direction.HIGHER_IS_BETTER);
		assertFalse(d_ade.deepEquals(var2));
	}
	
	@Test
	public void testEndpointDeepEquals() {
		// Test class
		assertFalse(d_ep.deepEquals(new AdverseEvent(d_ep.getName(), d_ep.getVariableType())));
		
		// Test common fields
		Endpoint var2 = new Endpoint(d_ep.getName(), d_ep.getVariableType());
		testCommonFields(d_ep, var2);

		var2.setDirection(OutcomeMeasure.Direction.LOWER_IS_BETTER);
		assertFalse(d_ep.deepEquals(var2));
	}
	
	@Test
	public void testCategoricalPopCharDeepEquals() {
		// Test class
		assertFalse(d_gender.deepEquals(new AdverseEvent(d_gender.getName(), d_gender.getVariableType())));
		
		// Test common fields
		PopulationCharacteristic var2 = new PopulationCharacteristic(d_gender.getName(), new CategoricalVariableType(((CategoricalVariableType) d_gender.getVariableType()).getCategories()));
		var2.setVariableType(new CategoricalVariableType(Arrays.asList("Mars", "Venus")));
		assertFalse(d_gender.deepEquals(var2));

		var2 = new PopulationCharacteristic(d_gender.getName(), new CategoricalVariableType(((CategoricalVariableType) d_gender.getVariableType()).getCategories()));
		testCommonFields(d_gender, var2);
	}
	
	@Test
	public void testRatePopCharDeepEquals() {
		// Test class
		assertFalse(d_rate.deepEquals(new AdverseEvent(d_rate.getName(), d_rate.getVariableType())));
		
		// Test common fields
		PopulationCharacteristic var2 = new PopulationCharacteristic(d_rate.getName(), new RateVariableType());
		testCommonFields(d_rate, var2);
	}

	@Test
	public void testContinuousPopCharDeepEquals() {
		// Test class
		assertFalse(d_cont.deepEquals(new AdverseEvent(d_cont.getName(), d_cont.getVariableType())));
		
		// Test common fields
		PopulationCharacteristic var2 = new PopulationCharacteristic(d_cont.getName(), new ContinuousVariableType());
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
		var1.setVariableType(new RateVariableType());
		var2.setVariableType(new ContinuousVariableType());
		assertFalse(var1.deepEquals(var2));
		var1.setVariableType(new ContinuousVariableType());
		assertTrue(var1.deepEquals(var2));

		// Equal on description
		var1.setDescription("defcrib");
		var2.setDescription("nuffink");
		assertFalse(var1.deepEquals(var2));
		var2.setDescription(var1.getDescription());
		assertTrue(var1.deepEquals(var2));
		
		// Equal on unit of measurement
		var1.setVariableType(new ContinuousVariableType("inch"));
		var2.setVariableType(new ContinuousVariableType("cm"));
		assertFalse(var1.deepEquals(var2));
		var2.setVariableType(new ContinuousVariableType("inch"));
		assertTrue(var1.deepEquals(var2));
	}

	@Test
	public void testEndpointEquals() {
		String name1 = "Endpoint A";
		String name2 = "Endpoint B";
		
		assertEquals(new Endpoint(name1, Endpoint.convertVarType(Variable.Type.RATE)), new Endpoint(name1, Endpoint.convertVarType(Variable.Type.RATE)));
		JUnitUtil.assertNotEquals(new Endpoint(name1, Endpoint.convertVarType(Variable.Type.RATE)), new Endpoint(name2, Endpoint.convertVarType(Variable.Type.RATE)));
		assertEquals(new Endpoint(name1, Endpoint.convertVarType(Variable.Type.RATE)).hashCode(), new Endpoint(name1, Endpoint.convertVarType(Variable.Type.RATE)).hashCode());
	}
	
	@Test
	public void testCategoricalPopCharEquals() {
		PopulationCharacteristic gender2 = new PopulationCharacteristic("Gender", new CategoricalVariableType(Arrays.asList((new String[]{"Male", "Female"}))));
		assertEquals(d_gender, gender2);
		assertEquals(d_gender.hashCode(), gender2.hashCode());
		
		gender2 = new PopulationCharacteristic("Gender2", new CategoricalVariableType(Arrays.asList((new String[]{"Male", "Female"}))));
		assertFalse(gender2.equals(d_gender));

		gender2 = new PopulationCharacteristic(null, new CategoricalVariableType(Arrays.asList((new String[]{"Male", "Female"}))));
		assertFalse(gender2.equals(d_gender));

		assertFalse(gender2.equals(new Integer(2)));
		
		// Equality only on Name within PopulationCharacteristic hierarchy
		assertEquals(d_gender, new PopulationCharacteristic(d_gender.getName(), new RateVariableType()));
	}
}
