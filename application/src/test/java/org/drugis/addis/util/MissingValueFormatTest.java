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

import java.text.NumberFormat;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class MissingValueFormatTest {
	
	private MissingValueFormat d_mvnf;
	private NumberFormat d_integerInstance;
	
	@Before
	public void setUp() {
		d_integerInstance = NumberFormat.getIntegerInstance();
		d_mvnf = new MissingValueFormat(d_integerInstance);
	}

	@Test
	public void testPreservedFunctionality() {
		assertEquals(d_integerInstance.format(-5.5), d_mvnf.format(-5.5));
		assertEquals(d_integerInstance.format(10L), d_mvnf.format(10L));
	}

	@Test
	public void testNA() throws ParseException {
		assertEquals("N/A", d_mvnf.format(null));
		assertEquals(null, d_mvnf.parseObject("N/A"));
	}
	
}
