/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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
		JUnitUtil.testSetter(new Endpoint("e", Variable.Type.RATE), Endpoint.PROPERTY_DESCRIPTION, "", "My Description");
	}
	
	@Test
	public void testSetUnitOfMeasurement() {
		JUnitUtil.testSetter(new Endpoint("e", Variable.Type.CONTINUOUS), Variable.PROPERTY_UNIT_OF_MEASUREMENT, "", "kg per day");
	}

	@Test
	public void testSetName() {
		JUnitUtil.testSetter(new Endpoint("e", Variable.Type.RATE), Endpoint.PROPERTY_NAME, "e", "My Name");
	}
	
	@Test
	public void testSetType() {
		JUnitUtil.testSetter(new Endpoint("e", Variable.Type.RATE), Endpoint.PROPERTY_TYPE, Variable.Type.RATE, Variable.Type.CONTINUOUS);
	}
	
	@Test
	public void testSetDirection() {
		JUnitUtil.testSetter(new Endpoint("e", Variable.Type.RATE), Endpoint.PROPERTY_DIRECTION,
				OutcomeMeasure.Direction.HIGHER_IS_BETTER, OutcomeMeasure.Direction.LOWER_IS_BETTER);
	}
	
	@Test
	public void testBuildMeasurement() {
		Arm pg = new Arm(null, null, 0);
		Endpoint e = new Endpoint("e", Variable.Type.RATE);
		e.setType(Variable.Type.RATE);
		assertTrue(e.buildMeasurement(pg) instanceof BasicRateMeasurement);
		e.setType(Variable.Type.CONTINUOUS);
		assertTrue(e.buildMeasurement(pg) instanceof BasicContinuousMeasurement);
	}
	
	@Test
	public void testToString() {
		String name = "TestName";
		Endpoint e = new Endpoint("e", Variable.Type.RATE);
		e.setName(name);
		assertEquals(name, e.toString());
	}
	

	@Test
	public void testEquals() {
		String name1 = "Endpoint A";
		String name2 = "Endpoint B";
		
		assertEquals(new Endpoint(name1, Variable.Type.RATE), new Endpoint(name1, Variable.Type.RATE));
		JUnitUtil.assertNotEquals(new Endpoint(name1, Variable.Type.RATE), new Endpoint(name2, Variable.Type.RATE));
		assertEquals(new Endpoint(name1, Variable.Type.RATE).hashCode(), new Endpoint(name1, Variable.Type.RATE).hashCode());
	}
}
