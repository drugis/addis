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

package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GaussianTest {
	
	private static final double EPSILON = 0.000001;
	private GaussianBase d_gauss1;
	private GaussianBase d_gauss2;

	@Before public void setUp() {
		d_gauss1 = new Gaussian(0.0, 1.0);
		d_gauss2 = new Gaussian(-5.0, 2.0);
	}
	
	@Test public void testGetAxisType() {
		assertEquals(AxisType.LINEAR, d_gauss1.getAxisType());
	}
	
	@Test public void testGetParameters() {
		assertEquals(0.0, d_gauss1.getMu(), EPSILON);
		assertEquals(1.0, d_gauss1.getSigma(), EPSILON);
		assertEquals(-5.0, d_gauss2.getMu(), EPSILON);
		assertEquals(2.0, d_gauss2.getSigma(), EPSILON);
	}
	
	@Test public void testCalculateCumulativeProbability() {
		assertEquals(0.5, d_gauss1.calculateCumulativeProbability(d_gauss1.getMu()), EPSILON);
		assertEquals(0.8413447, d_gauss1.calculateCumulativeProbability(d_gauss1.getSigma()), EPSILON);
		assertEquals(0.1586552, d_gauss1.calculateCumulativeProbability(-d_gauss1.getSigma()), EPSILON);
	}
	
	@Test public void testGetQuantile() {
		double z90 = 1.644853626951;
		double z95 = 1.959963984540;
		assertEquals(z95 * 1.0 + 0.0, d_gauss1.getQuantile(0.975), EPSILON);
		assertEquals(z95 * 2.0 + -5.0, d_gauss2.getQuantile(0.975), EPSILON);
		assertEquals(-z95 * 1.0 + 0.0, d_gauss1.getQuantile(0.025), EPSILON);
		assertEquals(-z95 * 2.0 + -5.0, d_gauss2.getQuantile(0.025), EPSILON);
		assertEquals(z90 * 1.0 + 0.0, d_gauss1.getQuantile(0.95), EPSILON);
		assertEquals(z90 * 2.0 + -5.0, d_gauss2.getQuantile(0.95), EPSILON);
		assertEquals(0.0, d_gauss1.getQuantile(0.5), EPSILON);
		assertEquals(-5.0, d_gauss2.getQuantile(0.5), EPSILON);
	}
	
	@Test
	public void testPlus() {
		GaussianBase x = new Gaussian(-1.25, 0.23);
		GaussianBase y = new Gaussian(3.8, 1.2);
		double expectedMu = x.getMu() + y.getMu();
		double expectedSigma = Math.sqrt(Math.pow(x.getSigma(), 2) + Math.pow(y.getSigma(), 2));

		GaussianBase z = (GaussianBase)x.plus(y);
		assertTrue(z instanceof Gaussian);
		assertEquals(expectedMu, z.getMu(), EPSILON);
		assertEquals(expectedSigma, z.getSigma(), EPSILON);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPlusShouldFailOnIncompatibleType() {
		(new Gaussian(0.0, 0.1)).plus(new LogGaussian(1.0, 2.0));
	}
	
	@Test(expected=IllegalArgumentException.class) public void testPreconditionSigmaNonNegative() {
		new Gaussian(0.0, -.01);
	}

	@Test(expected=IllegalArgumentException.class) public void testPreconditionSigmaNotNaN() {
		new Gaussian(0.0, Double.NaN);
	}

	@Test(expected=IllegalArgumentException.class) public void testPreconditionMuNotNaN() {
		new Gaussian(Double.NaN, 1.0);
	}
}
