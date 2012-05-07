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

package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.assertEquals;

import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class ConfidenceIntervalTest {
	@Test public void testGetPointEstimate() {
		ConfidenceInterval ci1 = new ConfidenceInterval(1.044, 0.0, 2.0);
		assertEquals(1.044, ci1.getPointEstimate(), 0.00000001);
	}
	
	@Test public void testToString() {
		ConfidenceInterval ci1 = new ConfidenceInterval(1.044, 0.0, 2.0);
		assertEquals("1.04 (0.00, 2.00)", ci1.toString());
	}
	
	@Test public void testEquals() {
		ConfidenceInterval ci1 = new ConfidenceInterval(1.0, 0.0, 2.0);
		ConfidenceInterval ci2 = new ConfidenceInterval(1.0, 0.0, 2.0);
		ConfidenceInterval ci3 = new ConfidenceInterval(0.5, 0.0, 2.0);
		ConfidenceInterval ci4 = new ConfidenceInterval(1.0, 0.5, 2.0);
		ConfidenceInterval ci5 = new ConfidenceInterval(1.0, 0.0, 2.5);
		
		assertEquals(ci1, ci2);
		assertEquals(ci1.hashCode(), ci2.hashCode());
		JUnitUtil.assertNotEquals(ci1, ci3);
		JUnitUtil.assertNotEquals(ci1, ci4);
		JUnitUtil.assertNotEquals(ci1, ci5);
	}
	
	@Test
	public void testEqualsInterval() {
		Interval<Double> in5 = new Interval<Double>(2.0, 5.0);
		ConfidenceInterval in6 = new ConfidenceInterval(1.0, 2.0, 5.0);
		JUnitUtil.assertNotEquals(in6, in5);
		JUnitUtil.assertNotEquals(in5, in6);
	}

}
