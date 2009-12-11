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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class BasicRateMeasurementTest {
	private BasicRateMeasurement d_measurement;
	private Endpoint d_endpoint;
	private BasicPatientGroup d_pg;
	
	@Before
	public void setUp() {
		d_endpoint = new Endpoint("E", Type.RATE);
		d_pg = new BasicPatientGroup(null, null, 101);
		d_measurement = new BasicRateMeasurement(d_endpoint, 67, d_pg);
	}
	
	@Test
	public void testSerialization() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(d_measurement);
		ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(bos.toByteArray()));
		d_measurement = (BasicRateMeasurement) ois.readObject();
		
		Endpoint newEndpoint = new Endpoint("X", Type.RATE);
		PropertyChangeListener mock = JUnitUtil.mockListener(d_measurement, 
				BasicRateMeasurement.PROPERTY_ENDPOINT, d_endpoint, newEndpoint);
		d_measurement.addPropertyChangeListener(mock);
		d_measurement.setEndpoint(newEndpoint);
		verify(mock);
	}
	
	@Test
	public void testSetRate() {
		JUnitUtil.testSetter(d_measurement, BasicRateMeasurement.PROPERTY_RATE, new Integer(67), new Integer(68));
	}
	
	@Test
	public void testSetSampleSize() {
		JUnitUtil.testSetter(d_measurement, BasicRateMeasurement.PROPERTY_SAMPLESIZE,
				new Integer(101), new Integer(111));
	}
	
	@Test
	public void testToString() {
		assertEquals("67/101", d_measurement.toString());
	}
}
