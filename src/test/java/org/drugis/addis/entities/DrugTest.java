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
import static org.junit.Assert.assertTrue;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class DrugTest {
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(new Drug("name", "atc"), Drug.PROPERTY_NAME, "name", "Paroxetine");
	}
	
	@Test
	public void testSetAtcCode() {
		JUnitUtil.testSetter(new Drug("name", "G0101"), Drug.PROPERTY_ATCCODE,
				"G0101", "C0101");
	}	
	
	@Test
	public void test2ArgConstructor() {
		Drug d = new Drug("name", "atc");
		assertEquals("name", d.getName());
		assertEquals("atc", d.getAtcCode());
	}
		
	@Test
	public void testToString() {
		Drug d = new Drug("Paroxetine", "atc");
		assertEquals("Paroxetine", d.toString());
	}
	
	@Test
	public void testEquals() {
		Drug d1 = new Drug("Paroxetine", "atc");
		Drug d2 = new Drug("Paroxetine", "atc");
		Drug d3 = new Drug("Fluoxetine", "atc");
		
		assertTrue(d1.equals(d2));
		assertFalse(d1.equals(d3));
		// TODO: Ask Gert about atc code (does it also have to be equal?)
	}
	
	@Test
	public void testHashCode() {
		Drug d1 = new Drug("Paroxetine", "atc");
		Drug d2 = new Drug("Paroxetine", "atc");
		assertEquals(d1.hashCode(), d2.hashCode());
	}

}
