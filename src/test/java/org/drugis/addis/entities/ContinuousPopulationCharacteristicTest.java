/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.*;

import java.util.Collections;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLHelper;
import org.junit.Before;
import org.junit.Test;

public class ContinuousPopulationCharacteristicTest {
	private ContinuousPopulationCharacteristic d_age;
	
	@Before
	public void setUp() {
		d_age = new ContinuousPopulationCharacteristic("Age");
	}
	
	@Test
	public void testGetName() {
		assertEquals("Age", d_age.getName());
	}
	
	@Test
	public void testGetDependencies() {
		assertEquals(Collections.emptySet(), d_age.getDependencies());
	}
	
	@Test
	public void testBuildMeasurement() {
		Measurement m = d_age.buildMeasurement();
		assertTrue(m instanceof ContinuousMeasurement);
	}
	
	@Test
	public void testToString() {
		assertEquals(d_age.getName(), d_age.toString());
	}
	
	@Test
	public void testXML() throws XMLStreamException {
		ContinuousPopulationCharacteristic age = buildAge();
		String xml = XMLHelper.toXml(age, ContinuousPopulationCharacteristic.class);
		ContinuousPopulationCharacteristic objFromXml = XMLHelper.fromXml(xml);
		assertEntityEquals(age, objFromXml);
	}

	private ContinuousPopulationCharacteristic buildAge() {
		ContinuousPopulationCharacteristic age = new ContinuousPopulationCharacteristic("Age");
		age.setDescription("Age in years from birth");
		age.setUnitOfMeasurement("Gregorian calendar years");
		return age;
	}
	
	private static final String s_legacyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
		"<org.drugis.addis.entities.ContinuousPopulationCharacteristic id=\"0\" description=\"Age in years from birth\" name=\"Age\" unitOfMeasurement=\"Gregorian calendar years\">" +
			"<type value=\"CONTINUOUS\"/>" +
		"</org.drugis.addis.entities.ContinuousPopulationCharacteristic>";
	
	@Test
	public void testLegacyXML() throws XMLStreamException {
		ContinuousPopulationCharacteristic age = buildAge();
		ContinuousPopulationCharacteristic objFromXml = XMLHelper.fromXml(s_legacyXML);
		assertEntityEquals(age, objFromXml);
	}
}
