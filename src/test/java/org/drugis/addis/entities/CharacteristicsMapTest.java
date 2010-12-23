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

import static org.junit.Assert.*;

import java.util.Collections;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.util.XMLHelper;
import org.junit.Test;

public class CharacteristicsMapTest {

	@Test
	public void testGetDependencies() {
		 CharacteristicsMap map = new CharacteristicsMap();
		 map.put(BasicStudyCharacteristic.INCLUSION, "TEST");
		 assertEquals(Collections.EMPTY_SET, map.getDependencies());
	}
	
	@Test
	public void testXML() throws XMLStreamException {
		CharacteristicsMap expectedMap = ExampleData.buildStudyChouinard().getCharacteristics();
		String xml = XMLHelper.toXml(expectedMap, CharacteristicsMap.class);
		CharacteristicsMap parsedMap = (CharacteristicsMap)XMLHelper.fromXml(xml);
		AssertEntityEquals.assertEntityEquals(expectedMap, parsedMap);
	}
}
