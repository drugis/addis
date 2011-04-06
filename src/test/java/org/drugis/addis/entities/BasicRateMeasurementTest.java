/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import static org.junit.Assert.assertFalse;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLHelper;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class BasicRateMeasurementTest {
	private BasicRateMeasurement d_measurement;
	private Arm d_pg;
	
	@Before
	public void setUp() {
		d_pg = new Arm("", 101, null, null);
		d_measurement = new BasicRateMeasurement(67, d_pg.getSize());
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
	
	@Test
	public void testClone() {
		assertEquals(d_measurement, d_measurement.clone());
		assertFalse(d_measurement == d_measurement.clone());
	}
	
	@Test
	public void testXML() throws XMLStreamException {
		String xml = XMLHelper.toXml(d_measurement, BasicRateMeasurement.class);
		BasicRateMeasurement brmFromXml = XMLHelper.fromXml(xml);
		assertEquals(d_measurement, brmFromXml);
	}
}
