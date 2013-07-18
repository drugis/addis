/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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
import org.junit.Before;
import org.junit.Test;

public class UnitTest {

	private Unit d_gram;
	private Unit d_liter;

	@Before
	public void setUp() {
		d_gram = new Unit("gram", "g");
		d_liter = new Unit("liter", "l");
	}
	
	@Test
	public void testCompare() {
		assertTrue(d_gram.compareTo(d_liter) < 0);
		assertTrue(d_gram.compareTo(d_gram) == 0);
	}
	
	@Test
	public void testEqualities() {
		assertFalse(d_gram.equals(d_liter));
		assertFalse(d_gram.deepEquals(d_liter));
		Unit badGram = new Unit("gram", "bg");
		assertTrue(d_gram.equals(badGram));
		assertEquals(d_gram.hashCode(), badGram.hashCode());
		assertFalse(d_gram.deepEquals(badGram));
	}
	
	@Test
	public void testEventFires() {
		JUnitUtil.testSetter(d_gram, Unit.PROPERTY_SYMBOL, "g", "Graeme");
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("gram", d_gram.getLabel());
	}
	
}
