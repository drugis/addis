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

package org.drugis.addis.mcmcmodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathUtilTest {

	@Test(expected = IllegalArgumentException.class)
	public void testLogitThrowsTooLow() {
		MathUtil.logit(0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLogitThrowsTooHigh() {
		MathUtil.logit(1);
	}
	
	@Test
	public void testLogit() {
		assertEquals(0.0, MathUtil.logit(0.5),0.0000001);
		assertEquals(Math.log(0.5), MathUtil.logit(1.0/3.0),0.0000001);
		assertEquals(Math.log(1.0/9.0), MathUtil.logit(0.1),0.0000001);
		assertEquals(Math.log(9.0), MathUtil.logit(0.9),0.0000001);
	}
	
	@Test
	public void testIlogit() {
		double[] xarr = {-10, 5, 0, 20, 23.7, 0.3};
		for (double x : xarr) {
			assertEquals(x, MathUtil.logit(MathUtil.ilogit(x)), 0.000001);
		}
	}
}
