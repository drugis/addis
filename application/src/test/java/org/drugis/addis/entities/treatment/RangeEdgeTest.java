/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.util.BoundedInterval;
import org.junit.Test;

public class RangeEdgeTest {
	@Test
	public void testInitialization() {
		RangeEdge edge = new RangeEdge(1.0, false, 2.0, true);
		
		assertEquals(1, edge.getLowerBound(), BoundedInterval.EPSILON);
		assertEquals(2, edge.getUpperBound(), BoundedInterval.EPSILON);
		assertFalse(edge.isLowerBoundOpen());
		assertTrue(edge.isUpperBoundOpen());
	}
	
	@Test 
	public void testDecide() {
		RangeEdge edge = new RangeEdge(0.0, true, 40.0, false);
		assertTrue(edge.decide(15.0));
		assertTrue(edge.decide(30.0));
		
		assertFalse(edge.decide(50.0));
		assertFalse(edge.decide(-0.1));
	}
	
	@Test
	public void testDecideBoundary() {
		RangeEdge edge = new RangeEdge(0.0, true, 40.0, false);		
		assertFalse(edge.decide(0.0));
		assertTrue(edge.decide(40.0));
	}
}
