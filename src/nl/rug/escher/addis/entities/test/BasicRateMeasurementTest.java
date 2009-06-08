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
import java.util.ArrayList;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

public class BasicRateMeasurementTest {
	private BasicPatientGroup d_patientGroup;
	private BasicRateMeasurement d_measurement;
	
	@Before
	public void setUp() {
		d_patientGroup = new BasicPatientGroup(null, null, null, 101);
		d_measurement = new BasicRateMeasurement();
		d_measurement.setRate(67);		
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
		d_measurement.getPatientGroup().setSize(105);
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
	public void testFireSizeChanged() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_measurement, BasicRateMeasurement.PROPERTY_SAMPLESIZE, 101, 102);
		d_measurement.addPropertyChangeListener(l);
		d_patientGroup.setSize(102);
		verify(l);
		
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
		d_patientGroup.setSize(102);
		verify(l);
	}	

	@Test
	public void testEquals() {
		Endpoint e1 = new Endpoint("e1");
		Endpoint e2 = new Endpoint("e2");
		BasicStudy s = new BasicStudy("STUDY");
		Drug drug1 = new Drug("Drug 1");
		Drug drug2 = new Drug("Drug 2");
		Dose dose = new Dose(8.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup g1 = new BasicPatientGroup(s, drug1, dose, 8,
				new ArrayList<BasicMeasurement>());
		BasicPatientGroup g2 = new BasicPatientGroup(s, drug2, dose, 8,
				new ArrayList<BasicMeasurement>());
		
		JUnitUtil.assertNotEquals(g1, g2);
		
		BasicRateMeasurement m1 = new BasicRateMeasurement(e1);
		m1.setPatientGroup(g1);
		m1.setRate(10);
		BasicRateMeasurement m2 = new BasicRateMeasurement(e1);
		m2.setPatientGroup(g1);
		m2.setRate(50);
		
		assertEquals(m1, m2);
		assertEquals(m1.hashCode(), m2.hashCode());
		
		m2.setPatientGroup(g2);
		JUnitUtil.assertNotEquals(m1, m2);
		
		m2.setPatientGroup(g1);
		m2.setEndpoint(e2);
		JUnitUtil.assertNotEquals(m1, m2);
		
		BasicContinuousMeasurement m3 = new BasicContinuousMeasurement(e1);
		m3.setMean(0.0);
		m3.setStdDev(1.0);
		m3.setPatientGroup(g1);
		assertEquals(m1, m3);
	}
}
