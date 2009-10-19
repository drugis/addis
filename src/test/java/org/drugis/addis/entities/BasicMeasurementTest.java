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


import java.beans.PropertyChangeListener;


import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class BasicMeasurementTest {
	private Endpoint d_endpoint = new Endpoint("E", Type.RATE);
	private BasicPatientGroup d_BasicPatientGroup = new BasicPatientGroup(null, null, null, 0);;

	@SuppressWarnings("serial")
	public BasicMeasurement instance() {
		return new BasicMeasurement(d_endpoint, d_BasicPatientGroup) {
			
			public String getLabel() {
				// TODO Auto-generated method stub
				return null;
			}
			public boolean isOfType(Type type) {
				return false;
			}
		};
	}
	
	@Test
	public void testSetEndpoint() {
		JUnitUtil.testSetter(instance(), BasicMeasurement.PROPERTY_ENDPOINT, d_endpoint, new Endpoint("x", Type.RATE));
	}
	
	@Test
	public void testSizeEvent() {
		int newVal = 100;
		BasicMeasurement instance = instance();
		PropertyChangeListener l = JUnitUtil.mockStrictListener(instance,
				BasicMeasurement.PROPERTY_SAMPLESIZE, 0, newVal);
		instance.addPropertyChangeListener(l);
		d_BasicPatientGroup.setSize(100);
		verify(l);
	}
}
