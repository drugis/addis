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

package nl.rug.escher.addis.entities.test;

import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.Endpoint.Type;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Test;

public class BasicMeasurementTest {
	@SuppressWarnings("serial")
	public BasicMeasurement instance() {
		return new BasicMeasurement() {
			public String getLabel() {
				return null;
			}

			public boolean isOfType(Type type) {
				return false;
			}
		};
	}
	
	@Test
	public void testSetPatientGroup() {
		JUnitUtil.testSetter(instance(), BasicMeasurement.PROPERTY_PATIENTGROUP, null, 
				new BasicPatientGroup(null, null, null, 0));
	}
	
	@Test
	public void testSetEndpoint() {
		JUnitUtil.testSetter(instance(), BasicMeasurement.PROPERTY_ENDPOINT, null, new Endpoint());
	}
}
