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
import static org.junit.Assert.assertTrue;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class EndpointTest {
	@Test
	public void testSetDescription() {
		JUnitUtil.testSetter(new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE)), Endpoint.PROPERTY_DESCRIPTION, "", "My Description");
	}

	@Test
	public void testSetName() {
		JUnitUtil.testSetter(new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE)), Endpoint.PROPERTY_NAME, "e", "My Name");
	}
	
	@Test
	public void testSetDirection() {
		JUnitUtil.testSetter(new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE)), Endpoint.PROPERTY_DIRECTION,
				OutcomeMeasure.Direction.HIGHER_IS_BETTER, OutcomeMeasure.Direction.LOWER_IS_BETTER);
	}
	
	@Test
	public void testBuildMeasurement() {
		Arm pg = new Arm("", 0);
		Endpoint e = new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE));
		e.setVariableType(new RateVariableType());
		assertTrue(e.buildMeasurement(pg) instanceof BasicRateMeasurement);
		e.setVariableType(new ContinuousVariableType());
		assertTrue(e.buildMeasurement(pg) instanceof BasicContinuousMeasurement);
	}
	
	@Test
	public void testToString() {
		String name = "TestName";
		Endpoint e = new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE));
		e.setName(name);
		assertEquals(name, e.toString());
	}

	@Test
	public void testVariableTypeGetterAndSetter() {
		JUnitUtil.testSetter(new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE)), Variable.PROPERTY_VARIABLE_TYPE, 
				new RateVariableType(), new ContinuousVariableType());
	}
}
