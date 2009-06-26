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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

public class BasicRateMeasurementTest {
	private BasicPatientGroup d_patientGroup;
	private BasicRateMeasurement d_measurement;
	
	@Before
	public void setUp() {
		d_patientGroup = new BasicPatientGroup(null, null, null, 101);
		d_measurement = new BasicRateMeasurement(new Endpoint("E"), 67, d_patientGroup.getSize());
		d_patientGroup.addMeasurement(d_measurement);
	}
	
	@Test
	public void testSerialization() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(d_measurement);
		ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(bos.toByteArray()));
		d_measurement = (BasicRateMeasurement) ois.readObject();
		
		String oldLabel = d_measurement.getLabel();
		String newLabel = "67/105";
		PropertyChangeListener mock = JUnitUtil.mockListener(d_measurement, 
				BasicRateMeasurement.PROPERTY_LABEL, oldLabel, newLabel);
		d_measurement.addPropertyChangeListener(mock);
		d_measurement.setSampleSize(105);
		verify(mock);
	}
	
	@Test
	public void testSetRate() {
		JUnitUtil.testSetter(new BasicRateMeasurement(), BasicRateMeasurement.PROPERTY_RATE, null, new Integer(67));
	}
	
	@Test
	public void testToString() {
		assertEquals("67/101", d_measurement.toString());
	}
	
	@Test
	public void testFireLabelChanged() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_measurement, Measurement.PROPERTY_LABEL, "67/101", "68/101");
		d_measurement.addPropertyChangeListener(l);
		d_measurement.setRate(68);
		verify(l);
		
		d_measurement.removePropertyChangeListener(l);
		l = JUnitUtil.mockListener(
				d_measurement, Measurement.PROPERTY_LABEL, "68/101", "68/102");
		d_measurement.addPropertyChangeListener(l);
		d_measurement.setSampleSize(102);
		verify(l);
	}	

}
