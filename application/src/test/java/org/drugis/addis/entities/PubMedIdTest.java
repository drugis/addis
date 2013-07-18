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

}
