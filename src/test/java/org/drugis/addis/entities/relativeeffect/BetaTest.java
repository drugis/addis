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

package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.*;

import org.junit.Test;

public class BetaTest {
	private static final double EPSILON = 0.000001;
	
	@Test
	public void testGetters() {
		assertEquals(7, new Beta(7, 13).getAlpha(), EPSILON);
		assertEquals(13, new Beta(7, 13).getBeta(), EPSILON);
	}

	@Test
	public void testAxisTypeShouldBeLinear() {
		assertEquals(AxisType.LINEAR, new Beta(1, 1).getAxisType());
	}
	
	@Test
	public void testGetQuantile() {
		assertEquals(0.025, new Beta(1, 1).getQuantile(0.025), EPSILON);
		assertEquals(0.5, new Beta(1, 1).getQuantile(0.5), EPSILON);
		assertEquals(0.95, new Beta(1, 1).getQuantile(0.95), EPSILON);
		
		assertEquals(0.07820626, new Beta(5, 18).getQuantile(0.025), EPSILON);
		assertEquals(0.12150867, new Beta(5, 18).getQuantile(0.12), EPSILON);
		assertEquals(0.20910650, new Beta(5, 18).getQuantile(0.5), EPSILON);
		assertEquals(0.35124865, new Beta(5, 18).getQuantile(0.93), EPSILON);
	}
	
	@Test
	public void testEquals() {
		assertFalse(new Beta(1, 1).equals(null));
		assertEquals(new Beta(1, 1), new Beta(1, 1));
		assertFalse(new Beta(1, 1).equals("Beta"));
		assertFalse(new Beta(1, 1).equals(new Beta(2, 1)));
		assertFalse(new Beta(1, 1).equals(new Beta(1, 2)));
	}
}
