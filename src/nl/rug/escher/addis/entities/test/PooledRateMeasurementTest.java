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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.MutablePatientGroup;
import nl.rug.escher.addis.entities.PooledRateMeasurement;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.common.JUnitUtil;

import org.contract4j5.errors.ContractError;
import org.junit.Before;
import org.junit.Test;

public class PooledRateMeasurementTest {
	@Test(expected=ContractError.class)
	public void testConstructWithNull() {
		new PooledRateMeasurement(null);
	}
	
	@Test(expected=ContractError.class)
	public void testConstructWithEmpty() {
		new PooledRateMeasurement(new ArrayList<RateMeasurement>());
	}
	
	@Test(expected=ContractError.class)
	public void testConstructWithDifferentEndpoints() {
		d_m1 = new BasicRateMeasurement(new Endpoint("e1"));
		d_m1.setRate(12);
		new PooledRateMeasurement(Arrays.asList(
				new RateMeasurement[] {d_m1, d_m2}));
	}
	
	Endpoint d_e;
	BasicRateMeasurement d_m1;
	BasicRateMeasurement d_m2;
	PooledRateMeasurement d_m;
	BasicPatientGroup d_g1;
	BasicPatientGroup d_g2;
	
	@Before
	public void setUp() {
		d_e = new Endpoint("e0");
		d_g1 = new BasicPatientGroup(null, null, null, 100);
		d_m1 = new BasicRateMeasurement(d_e, 12, d_g1.getSize());
		d_g2 = new BasicPatientGroup(null, null, null, 50);
		d_m2 = new BasicRateMeasurement(d_e, 18, d_g2.getSize());
		d_m = new PooledRateMeasurement(Arrays.asList(new RateMeasurement[] {d_m1, d_m2}));
	}
	
	@Test
	public void testGetRate() {
		assertEquals(new Integer(d_m1.getRate() + d_m2.getRate()), d_m.getRate());
		d_m1.setRate(50);
		assertEquals(new Integer(d_m1.getRate() + d_m2.getRate()), d_m.getRate());
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(d_e, d_m.getEndpoint());
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(new Integer(d_m1.getSampleSize() + d_m2.getSampleSize()), d_m.getSampleSize());
		d_m2.setSampleSize(1000);
		assertEquals(new Integer(d_m1.getSampleSize() + d_m2.getSampleSize()), d_m.getSampleSize());
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("30/150", d_m.getLabel());
	}
	
	@Test
	public void testFireRateChanged() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_m, RateMeasurement.PROPERTY_RATE, d_m.getRate(), d_m.getRate() + 10);
		d_m.addPropertyChangeListener(l);
		d_m1.setRate(d_m1.getRate() + 10);
		verify(l);
	}
	
	@Test
	public void testFireSampleSizeChanged() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_m, RateMeasurement.PROPERTY_SAMPLESIZE, d_m.getSampleSize(), d_m.getSampleSize() + 10);
		d_m.addPropertyChangeListener(l);
		d_m1.setSampleSize(d_m1.getSampleSize() + 10);
		verify(l);
	}
	
	@Test
	public void testFireLabelChangedOnRate() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_m, RateMeasurement.PROPERTY_LABEL, "30/150", "40/150");
		d_m.addPropertyChangeListener(l);
		d_m1.setRate(d_m1.getRate() + 10);
		verify(l);
	}
	
	@Test
	public void testFireLabelChangedOnSampleSize() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_m, RateMeasurement.PROPERTY_LABEL, "30/150", "30/250");
		d_m.addPropertyChangeListener(l);
		d_m1.setSampleSize(d_m1.getSampleSize() + 100);
		verify(l);
	}
	
	@Test(expected=RuntimeException.class)
	public void testFailEndpointChanged() {
		d_m2.setEndpoint(new Endpoint());
	}

	@Test
	public void testEquals() {
		Endpoint e = new Endpoint("e");
		MutablePatientGroup g1 = new BasicPatientGroup(
				new BasicStudy("s1"), new Drug("d1"),
				new Dose(8.0, SIUnit.MILLIGRAMS_A_DAY),
				50, new ArrayList<BasicMeasurement>());
		MutablePatientGroup g2 = new BasicPatientGroup(
				new BasicStudy("s2"), new Drug("d1"),
				new Dose(8.0, SIUnit.MILLIGRAMS_A_DAY),
				50, new ArrayList<BasicMeasurement>());
		BasicRateMeasurement m1 = new BasicRateMeasurement(e);
		g1.addMeasurement(m1);
		BasicRateMeasurement m2 = new BasicRateMeasurement(e);
		g2.addMeasurement(m2);
		List<RateMeasurement> l1 = new ArrayList<RateMeasurement>();
		l1.add(m1);
		l1.add(m2);
		List<RateMeasurement> l2 = new ArrayList<RateMeasurement>();
		l2.add(m2);
		l2.add(m1);
		List<RateMeasurement> l3 = new ArrayList<RateMeasurement>();
		l3.add(m2);
		
		assertEquals(new PooledRateMeasurement(l1), new PooledRateMeasurement(l1));
		assertEquals(
				new PooledRateMeasurement(l1).hashCode(),
				new PooledRateMeasurement(l1).hashCode());
		assertEquals(new PooledRateMeasurement(l2), new PooledRateMeasurement(l1));
		assertEquals(
				new PooledRateMeasurement(l2).hashCode(),
				new PooledRateMeasurement(l1).hashCode());
		JUnitUtil.assertNotEquals(
				new PooledRateMeasurement(l1),
				new PooledRateMeasurement(l3));
	}
}
