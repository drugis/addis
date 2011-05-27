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

package org.drugis.addis.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionTest {
	@Test
	public void testToString() {
		String str = "1.10.5-SNAPSHOT";
		assertEquals(str , new Version(str).toString());
	}
	
	@Test
	public void testCompareToOneComponent() {
		assertTrue(new Version("1").compareTo(new Version("1")) == 0);
		assertTrue(new Version("1").compareTo(new Version("2")) < 0);
		assertTrue(new Version("3").compareTo(new Version("2")) > 0);
		assertTrue(new Version("10").compareTo(new Version("2")) > 0);
		assertTrue(new Version("010").compareTo(new Version("10")) == 0);
	}
	
	@Test
	public void testCompareToTwoComponents() {
		assertTrue(new Version("1.0").compareTo(new Version("1.0")) == 0);
		assertTrue(new Version("1.0").compareTo(new Version("1.1")) < 0);
		assertTrue(new Version("1").compareTo(new Version("1.1")) < 0);
		assertTrue(new Version("1.1").compareTo(new Version("1")) > 0);
		assertTrue(new Version("0.8").compareTo(new Version("0.10")) < 0);
	}
	
	@Test
	public void testCompareToMoreComponents() {
		assertTrue(new Version("1.0.1").compareTo(new Version("1.0.1")) == 0);
		assertTrue(new Version("1.0.0").compareTo(new Version("1.0.1")) < 0);
		assertTrue(new Version("1.0").compareTo(new Version("1.0.1")) < 0);
		assertTrue(new Version("1.0.1").compareTo(new Version("1.0")) > 0);
		assertTrue(new Version("1.0.5").compareTo(new Version("1.0.1")) > 0);
	}

	@Test
	public void testCompareWithStringSuffix() {
		assertTrue(new Version("1.0a").compareTo(new Version("1.0")) == 0);
		assertTrue(new Version("0.8").compareTo(new Version("0.8-SNAPSHOT")) == 0);
	}
	
	@Test
	public void testEquals() {
		assertTrue(new Version("1.0").equals(new Version("1.0")));
		assertFalse(new Version("1.0").equals(new Version("1.1")));
		assertFalse(new Version("0.8").equals(new Version("0.10")));
		assertTrue(new Version("1.0.1").equals(new Version("1.0.1")));
		assertFalse(new Version("1.0.0").equals(new Version("1.0.1")));
	}
}
