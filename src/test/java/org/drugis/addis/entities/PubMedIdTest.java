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

import static org.junit.Assert.*;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLHelper;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class PubMedIdTest {
	@Test(expected=IllegalArgumentException.class)
	public void testNonDigitThrowsException() {
		new PubMedId("123a45");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonDigitThrowsException2() {
		new PubMedId("12345x");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonDigitThrowsException3() {
		new PubMedId("x12345");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEmptyThrowsException() {
		new PubMedId("");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNullThrowsException() {
		new PubMedId(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLeadingZeroThrowsException3() {
		new PubMedId("012345");
	}
	
	@Test
	public void testEquals() {
		assertEquals(new PubMedId("12345"), new PubMedId("12345"));
		JUnitUtil.assertNotEquals(new PubMedId("12345"), new PubMedId("12346"));
	}
	
	@Test
	public void testXML() throws XMLStreamException {
		PubMedIdList expectedList = new PubMedIdList();
		expectedList.add(new PubMedId("12345"));
		expectedList.add(new PubMedId("5006"));
		String xml = XMLHelper.toXml(expectedList, PubMedIdList.class);
		PubMedIdList parsedList = (PubMedIdList)XMLHelper.fromXml(xml);
		assertEquals(expectedList, parsedList);
	}
	
}
