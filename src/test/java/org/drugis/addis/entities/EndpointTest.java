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

import static org.junit.Assert.*;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class EndpointTest {

	@Test
	public void testSetDescription() {
		JUnitUtil.testSetter(new Endpoint("e", Type.RATE), Endpoint.PROPERTY_DESCRIPTION, "", "My Description");
	}

	@Test
	public void testSetName() {
		JUnitUtil.testSetter(new Endpoint("e", Type.RATE), Endpoint.PROPERTY_NAME, "e", "My Name");
	}
	
	@Test
	public void testSetType() {
		JUnitUtil.testSetter(new Endpoint("e", Type.RATE), Endpoint.PROPERTY_TYPE, Type.RATE, Endpoint.Type.CONTINUOUS);
	}
	
	@Test
	public void testBuildMeasurement() {
		Endpoint e = new Endpoint("e", Type.RATE);
		e.setType(Endpoint.Type.RATE);
		assertTrue(e.buildMeasurement() instanceof BasicRateMeasurement);
		assertEquals(e, e.buildMeasurement().getEndpoint());
		e.setType(Endpoint.Type.CONTINUOUS);
		assertTrue(e.buildMeasurement() instanceof BasicContinuousMeasurement);
		assertEquals(e, e.buildMeasurement().getEndpoint());
	}
	
	@Test
	public void testToString() {
		String name = "TestName";
		Endpoint e = new Endpoint("e", Type.RATE);
		e.setName(name);
		assertEquals(name, e.toString());
	}
	

	@Test
	public void testEquals() {
		String name1 = "Endpoint A";
		String name2 = "Endpoint B";
		
		assertEquals(new Endpoint(name1, Type.RATE), new Endpoint(name1, Type.RATE));
		JUnitUtil.assertNotEquals(new Endpoint(name1, Type.RATE), new Endpoint(name2, Type.RATE));
		assertEquals(new Endpoint(name1, Type.RATE).hashCode(), new Endpoint(name1, Type.RATE).hashCode());
	}
}
