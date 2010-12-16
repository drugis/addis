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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.util.XMLHelper;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class IndicationTest {
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(new Indication(0L, ""), Indication.PROPERTY_NAME, "", "Severe depression");
	}
	
	@Test
	public void testSetCode() {
		JUnitUtil.testSetter(new Indication(0L, ""), Indication.PROPERTY_CODE, 0L, 310497006L);
	}

	@Test
	public void testEquals() {
		Indication i1 = new Indication(310497006L, "Severe depression");
		Indication i2 = new Indication(310497006L, "Zware depressie");
		Indication i3 = new Indication(0L, "Severe depression");
		
		assertEquals(i1, i2);
		assertFalse(i1.equals(i3));
		assertFalse(i2.equals(i3));
	}
	
	@Test
	public void testHashCode() {
		Indication i1 = new Indication(310497006L, "Severe depression");
		Indication i2 = new Indication(310497006L, "Severe depression");
		assertEquals(i1.hashCode(), i2.hashCode());
	}

	@Test
	public void testToString() {
		Indication i1 = new Indication(310497006L, "Severe depression");
		assertEquals(i1.getCode().toString() + " " + i1.getName(), i1.toString());
	}
	
	@Test
	public void testXML() throws XMLStreamException {
		Indication i = ExampleData.buildIndicationDepression();
		String xml = XMLHelper.toXml(i, Indication.class);
		AssertEntityEquals.assertEntityEquals(i, (Indication)XMLHelper.fromXml(xml));
	}
	
}
