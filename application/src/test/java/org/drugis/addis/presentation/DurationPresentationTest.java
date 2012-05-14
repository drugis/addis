/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.presentation;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.presentation.DurationPresentation.DateUnits;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DurationPresentationTest {
	private Duration d_duration1;
	private Epoch d_epoch1;
	private DurationPresentation<Epoch> d_pm1;
	private Epoch d_epoch2;
	private DurationPresentation<Epoch> d_pm2;
	private Duration d_duration3;
	private Epoch d_epoch3;
	private DurationPresentation<Epoch> d_pm3;
	
	@Before
	public void setUp() throws DatatypeConfigurationException {
		d_duration1 = DatatypeFactory.newInstance().newDuration("P42D");

		d_epoch1 = new Epoch("Epoch1", d_duration1);
		d_pm1 = new DurationPresentation<Epoch>(d_epoch1);
		
		d_epoch2 = new Epoch("Epoch2", null);
		d_pm2 = new DurationPresentation<Epoch>(d_epoch2);
		
		d_duration3 = DatatypeFactory.newInstance().newDuration("P3D");
		d_epoch3 = new Epoch("Epoch3", d_duration3);
		d_pm3 = new DurationPresentation<Epoch>(d_epoch3);
	}

	@Test
	public void testDefined() {
		// initial values based on the epoch
		assertEquals(true, d_pm1.getDefined());
		assertEquals(false, d_pm2.getDefined());
		
		// changing duration in the epoch should change "defined"
		d_epoch1.setDuration(null);
		assertEquals(false, d_pm1.getDefined());
		
		// changing  "defined" should change duration in the epoch
		d_pm1.setDefined(true);
		assertNotNull(d_epoch1.getDuration());
		
		// changing  "defined" should change duration in the epoch		
		d_pm1.setDefined(false);
		assertNull(d_epoch1.getDuration());
		
		// setting "defined" (true -> true) should not clear the value
		d_epoch1.setDuration(d_duration1);
		d_pm1.setDefined(true);
		assertEquals(d_duration1, d_epoch1.getDuration());
	}
	
	@Test
	public void testGetUnits() throws DatatypeConfigurationException {
		// Default units are weeks
		assertEquals(DateUnits.Weeks, d_pm2.getUnits());
		assertEquals(DateUnits.Weeks, d_pm1.getUnits());

		// If Days not multiple of 7, use Days
		assertEquals(DateUnits.Days, d_pm3.getUnits());
		
		// Test other units
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("PT3H"));
		assertEquals(DateUnits.Hours, d_pm1.getUnits());
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("PT5M"));
		assertEquals(DateUnits.Minutes, d_pm1.getUnits());
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("PT3S"));
		assertEquals(DateUnits.Seconds, d_pm1.getUnits());
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("P4M"));
		assertEquals(DateUnits.Months, d_pm1.getUnits());
	}
	
	@Test
	public void testGetQuantity() {
		assertEquals(6, (int)d_pm1.getQuantity());
		assertEquals(0, (int)d_pm2.getQuantity());
		assertEquals(3, (int)d_pm3.getQuantity());
	}
	
	@Test
	public void testAsDurationString() {
		assertEquals("PT5S", DateUnits.Seconds.asDurationString(5));
		assertEquals("PT3M", DateUnits.Minutes.asDurationString(3));
		assertEquals("PT8H", DateUnits.Hours.asDurationString(8));
		assertEquals("P12D", DateUnits.Days.asDurationString(12));
		assertEquals("P35D", DateUnits.Weeks.asDurationString(5));
		assertEquals("P0M", DateUnits.Months.asDurationString(0));
	}
	
	@Test
	public void testSetQuantity() throws DatatypeConfigurationException {
		d_pm3.setQuantity(8);
		assertEquals(8, d_pm3.getQuantity());
		assertEquals(DatatypeFactory.newInstance().newDuration("P8D"), d_epoch3.getDuration());
	}
	
	@Test
	public void testSetUnits() throws DatatypeConfigurationException {
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("PT3H"));
		assertEquals(DateUnits.Hours, d_pm1.getUnits());
		
		// Units should be changed and quantity unchanged
		d_pm1.setUnits(DateUnits.Minutes);
		assertEquals(DateUnits.Minutes, d_pm1.getUnits());
		assertEquals(3, d_pm1.getQuantity());
		assertEquals(DatatypeFactory.newInstance().newDuration("PT3M"), d_epoch1.getDuration());
		
		// If we set Weeks this should not change to Days
		d_pm1.setUnits(DateUnits.Weeks);
		assertEquals(DateUnits.Weeks, d_pm1.getUnits());
		assertEquals(3, d_pm1.getQuantity());
		assertEquals(DatatypeFactory.newInstance().newDuration("P21D"), d_epoch1.getDuration());
		
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("P28D"));
		assertEquals(DateUnits.Weeks, d_pm1.getUnits());
		assertEquals(4, d_pm1.getQuantity());
		
		// If we set Days this should not change to Weeks
		d_pm1.setQuantity(14);
		d_pm1.setUnits(DateUnits.Days);
		assertEquals(DateUnits.Days, d_pm1.getUnits());
		assertEquals(14, d_pm1.getQuantity());
		assertEquals(DatatypeFactory.newInstance().newDuration("P14D"), d_epoch1.getDuration());
		
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("P28D"));
		assertEquals(DateUnits.Days, d_pm1.getUnits());
		assertEquals(28, d_pm1.getQuantity());
	}
	
	@Test
	public void testSetOnNotDefined() {
		d_pm2.setUnits(DateUnits.Days);
		assertEquals(false, d_pm2.getDefined());
		
		d_pm2.setQuantity(8);
		assertEquals(false, d_pm2.getDefined());
	}
	
	@Test
	public void testSetterEvents() {
		PropertyChangeListener mockListener = createNiceMock(PropertyChangeListener.class);
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm1, DurationPresentation.PROPERTY_DURATION_UNITS, DateUnits.Weeks, DateUnits.Hours)));
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm1, DurationPresentation.PROPERTY_DURATION_QUANTITY, 6, 8)));
		replay(mockListener);
		d_pm1.addPropertyChangeListener(mockListener);
		d_pm1.setUnits(DateUnits.Hours);
		d_pm1.setQuantity(8);
		verify(mockListener);
		
		// Use nice mock because setting defined will emit more events
		mockListener = createNiceMock(PropertyChangeListener.class);
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm2, DurationPresentation.PROPERTY_DEFINED, false, true)));
		replay(mockListener);
		d_pm2.addPropertyChangeListener(mockListener);
		d_pm2.setDefined(true);
		verify(mockListener);
		
		mockListener = createNiceMock(PropertyChangeListener.class);
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm2, DurationPresentation.PROPERTY_DEFINED, true, false)));
		replay(mockListener);
		d_pm2.addPropertyChangeListener(mockListener);
		d_pm2.setDefined(false);
		verify(mockListener);
	}
	
	@Test
	public void testListenerEventsBothFields() throws DatatypeConfigurationException {
		PropertyChangeListener mockListener = createNiceMock(PropertyChangeListener.class);
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm1, DurationPresentation.PROPERTY_DURATION_QUANTITY, 6, 8)));
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm1, DurationPresentation.PROPERTY_DURATION_UNITS, DateUnits.Weeks, DateUnits.Hours)));
		replay(mockListener);
		d_pm1.addPropertyChangeListener(mockListener);
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("PT8H"));
		verify(mockListener);
	}
	
	@Test
	public void testListenerEventsNoFields() throws DatatypeConfigurationException {
		PropertyChangeListener mockListener = createNiceMock(PropertyChangeListener.class);
		replay(mockListener);
		d_pm1.addPropertyChangeListener(mockListener);
		d_epoch1.setDuration(DatatypeFactory.newInstance().newDuration("P42D"));
		verify(mockListener);
	}
	
	@Test
	public void testListenerEventsDefined() throws DatatypeConfigurationException {
		PropertyChangeListener mockListener = createNiceMock(PropertyChangeListener.class);
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm2, DurationPresentation.PROPERTY_DEFINED, false, true)));
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm2, DurationPresentation.PROPERTY_DURATION_QUANTITY, 0, 5)));
		mockListener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(d_pm2, DurationPresentation.PROPERTY_DURATION_UNITS, DateUnits.Weeks, DateUnits.Hours)));
		replay(mockListener);
		d_pm2.addPropertyChangeListener(mockListener);
		d_epoch2.setDuration(DatatypeFactory.newInstance().newDuration("PT5H"));
		verify(mockListener);
	}
	
	@Test
	public void testToString() {
		assertEquals("6 Weeks", d_pm1.getLabel());
		assertEquals("Undefined", d_pm2.getLabel());
		assertEquals("3 Days", d_pm3.getLabel());
	}
	
}
