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

package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;


public class TransformedStudentTTest {
	
	private TransformedStudentT d_student1;
	private TransformedStudentT d_student2;

	@Before public void setUp() {
		d_student1 = new TransformedStudentT(0.0, 1.0, 1);
		d_student2 = new TransformedStudentT(-5.0, 2.0, 9);
	}
	
	@Test public void testGetAxisType() {
		assertEquals(AxisType.LINEAR, d_student1.getAxisType());
	}
	
	@Test public void testGetParameters() {
		assertEquals(0.0, d_student1.getMu(), 0.00000001);
		assertEquals(1.0, d_student1.getSigma(), 0.00000001);
		assertEquals(1, d_student1.getDegreesOfFreedom());
		assertEquals(-5.0, d_student2.getMu(), 0.00000001);
		assertEquals(2.0, d_student2.getSigma(), 0.00000001);
		assertEquals(9, d_student2.getDegreesOfFreedom());
	}
	
	@Test public void testGetQuantile() {
		double t1_90 = 6.314;
		double t1_95 = 12.706;
		double t9_90 = 1.833;
		double t9_95 = 2.262;
		assertEquals(t1_95 * 1.0 + 0.0, d_student1.getQuantile(0.975), 0.001);
		assertEquals(t9_95 * 2.0 + -5.0, d_student2.getQuantile(0.975), 0.001);
		assertEquals(-t1_95 * 1.0 + 0.0, d_student1.getQuantile(0.025), 0.001);
		assertEquals(-t9_95 * 2.0 + -5.0, d_student2.getQuantile(0.025), 0.001);
		assertEquals(t1_90 * 1.0 + 0.0, d_student1.getQuantile(0.95), 0.001);
		assertEquals(t9_90 * 2.0 + -5.0, d_student2.getQuantile(0.95), 0.001);
		assertEquals(0.0, d_student1.getQuantile(0.5), 0.00001);
		assertEquals(-5.0, d_student2.getQuantile(0.5), 0.00001);
	}
	
	@Test public void testCalculateCumulativeProbability() {
		assertEquals(0.5, d_student1.calculateCumulativeProbability(d_student1.getMu()), 0.000001);
		assertEquals(0.75, d_student1.calculateCumulativeProbability(d_student1.getSigma()), 0.000001);
		assertEquals(0.25, d_student1.calculateCumulativeProbability(-d_student1.getSigma()), 0.000001);
		assertEquals(0.5, d_student2.calculateCumulativeProbability(d_student2.getMu()), 0.000001);
		assertEquals(0.8282818, d_student2.calculateCumulativeProbability(d_student2.getMu() + d_student2.getSigma()), 0.000001);
		assertEquals(0.1717181, d_student2.calculateCumulativeProbability(d_student2.getMu() - d_student2.getSigma()), 0.000001);
	}
		
	
	@Test(expected=IllegalArgumentException.class) public void testPreconditionSigmaNonNegative() {
		new TransformedStudentT(0.0, -.01, 1);
	}

	@Test(expected=IllegalArgumentException.class) public void testPreconditionSigmaNotNaN() {
		new TransformedStudentT(0.0, Double.NaN, 1);
	}

	@Test(expected=IllegalArgumentException.class) public void testPreconditionMuNotNaN() {
		new TransformedStudentT(Double.NaN, 1.0, 1);
	}
	
	@Test(expected=IllegalArgumentException.class) public void testPreconditionDegreesOfFreedomPositive() {
		new TransformedStudentT(0.0, 1.0, 0);
	}
	
	@Test
	public void testEquals() {
		assertFalse(new TransformedStudentT(1.0, 0.2, 5).equals(null));
		assertEquals(new TransformedStudentT(1.0, 0.2, 5), new TransformedStudentT(1.0, 0.2, 5));
		assertFalse(new TransformedStudentT(1.0, 0.2, 5).equals(new TransformedStudentT(1.003, 0.2, 5)));
		assertFalse(new TransformedStudentT(1.0, 0.2, 5).equals(new TransformedStudentT(1.0, 0.3, 5)));
		assertFalse(new TransformedStudentT(1.0, 0.2, 5).equals(new TransformedStudentT(1.0, 0.2, 800)));
	}
}
