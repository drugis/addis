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

import static org.junit.Assert.*;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Test;

public class DrugTest {
	@Test
	public void testSetUnit() {
		JUnitUtil.testSetter(new Drug(), Drug.PROPERTY_NAME, "", "Paroxetine");
	}
	
	@Test
	public void testToString() {
		Drug d = new Drug();
		d.setName("Paroxetine");
		assertEquals("Paroxetine", d.toString());
	}
	
	@Test
	public void testEquals() {
		Drug d1 = new Drug("Paroxetine");
		Drug d2 = new Drug("Paroxetine");
		Drug d3 = new Drug("Fluoxetine");
		
		assertTrue(d1.equals(d2));
		assertFalse(d1.equals(d3));
	}
	
	@Test
	public void testHashCode() {
		Drug d1 = new Drug("Paroxetine");
		Drug d2 = new Drug("Paroxetine");
		assertEquals(d1.hashCode(), d2.hashCode());
	}
}
