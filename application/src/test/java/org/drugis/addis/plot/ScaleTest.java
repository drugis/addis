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

package org.drugis.addis.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.forestplot.BinnedScale;
import org.drugis.addis.forestplot.IdentityScale;
import org.drugis.addis.forestplot.LinearScale;
import org.drugis.addis.forestplot.LogScale;
import org.drugis.addis.forestplot.BinnedScale.Bin;
import org.drugis.common.Interval;
import org.junit.Test;

public class ScaleTest {

	@Test
	public void testIdentityThrows() {
		IdentityScale id = new IdentityScale();
		assertEquals(1.1D, id.getNormalized(1.1), 0.0001);
	}
	
	@Test
	public void testLinearScale() {
		LinearScale ls = new LinearScale(new Interval<Double>(10.0,20.0));
		assertEquals(0.5, ls.getNormalized(15.0), 0.0001);
	}
	
	@Test
	public void testLogScale() {
		LogScale los = new LogScale(new Interval<Double>(0.1,10.0));
		assertEquals(0.5, los.getNormalized(Math.exp( (Math.log(10.0) - Math.log(0.1)) / 2 + Math.log(0.1) )), 0.0001);
	}
	
	@Test
	public void testLogScale10base() {
		LogScale los = new LogScale(new Interval<Double>(0.1,1000.0));
		assertEquals(0.75, los.getNormalizedLog10(100), 0.0001);
	}
	
	@Test
	public void testBinnedScale() {
		BinnedScale bs = new BinnedScale(new IdentityScale(), 1, 201);
		Bin b = bs.getBin(0.75);
		assertTrue(!b.outOfBoundsMax);
		assertTrue(!b.outOfBoundsMin);
		assertEquals((int) 151, (int) b.bin);
	}
	
	@Test
	public void testBinnedScaleOutofBounds() {
		BinnedScale bs = new BinnedScale(new IdentityScale(), 1, 201);
		assertTrue(bs.getBin(1.1).outOfBoundsMax);
		assertTrue(bs.getBin(-0.1).outOfBoundsMin);
		assertEquals(1,(int) bs.getBin(-0.1).bin);
		assertEquals(201,(int) bs.getBin(1.1).bin);
	}
}
