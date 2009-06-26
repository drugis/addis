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
import static org.junit.Assert.*;

import java.beans.PropertyChangeListener;
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

public class BasicContinuousMeasurementTest {
	private Endpoint d_endpoint;
	
	@Before
	public void setUp() {
		d_endpoint = new Endpoint("X");
	}
	
	@Test
	public void testSetMean() {
		JUnitUtil.testSetter(new BasicContinuousMeasurement(d_endpoint, 1),
				BasicContinuousMeasurement.PROPERTY_MEAN, 0.0, 25.91);
	}
	
	@Test
	public void testSetSampleSize() {
		JUnitUtil.testSetter(new BasicContinuousMeasurement(d_endpoint, 1),
				BasicContinuousMeasurement.PROPERTY_SAMPLESIZE, 1, 100);
	}
	
	@Test
	public void testSetStdDev() {
		JUnitUtil.testSetter(new BasicContinuousMeasurement(d_endpoint, 1), BasicContinuousMeasurement.PROPERTY_STDDEV, 0.0, 0.46);
	}
	
	@Test
	public void testToString() {
		BasicContinuousMeasurement m = new BasicContinuousMeasurement(d_endpoint, 1);
		assertEquals("0.0 \u00B1 0.0", m.toString());
	}
	
	@Test
	public void testFireLabelChanged() {
		BasicContinuousMeasurement measurement = new BasicContinuousMeasurement(d_endpoint, 1);
		measurement.setMean(25.5);
		PropertyChangeListener l = JUnitUtil.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "25.5 \u00B1 0.0", "25.5 \u00B1 1.1");
		measurement.addPropertyChangeListener(l);
		measurement.setStdDev(1.1);
		verify(l);
		
		measurement.removePropertyChangeListener(l);
		l = JUnitUtil.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "25.5 \u00B1 1.1", "27.5 \u00B1 1.1");
		measurement.addPropertyChangeListener(l);
		measurement.setMean(27.5);
		verify(l);
	}
}
