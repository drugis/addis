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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;
import org.junit.Before;
import org.junit.Test;

public class BasicRiskDifferenceTest {
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;

	// Paper data [Warn2002]:
	private static final int s_cooper1977rT = 15;
	private static final int s_cooper1977nT = 38;
	private static final int s_cooper1977rC = 6;
	private static final int s_cooper1977nC = 40;
	private static final double s_cooper1977RD = 0.245;
	private static final double s_cooper1977RDvar = 0.009;
	
	BasicRateMeasurement d_cooper1977Num;
	BasicRateMeasurement d_cooper1977Den;
	BasicRiskDifference d_cooperRD;
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	BasicRiskDifference d_riskDif;
	
	@Before
	public void setUp() {
		Arm pnum = new Arm("Num", s_sizeNum);
		Arm pden = new Arm("Den", s_sizeDen);
		d_numerator = new BasicRateMeasurement(s_effectNum, pnum.getSize());		
		d_denominator = new BasicRateMeasurement(s_effectDen, pden.getSize());
		d_riskDif = new BasicRiskDifference(d_denominator, d_numerator);
		
		//cooper 1977 from Warn2002
		Arm fnum = new Arm("T", s_cooper1977nT);
		Arm fden = new Arm("C", s_cooper1977nC);
		d_cooper1977Num = new BasicRateMeasurement(s_cooper1977rT, fnum.getSize());
		d_cooper1977Den = new BasicRateMeasurement(s_cooper1977rC, fden.getSize());
		d_cooperRD = new BasicRiskDifference(d_cooper1977Den, d_cooper1977Num);
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(s_sizeNum + s_sizeDen, (int)d_riskDif.getSampleSize());
	}
	
	@Test
	public void testGetRelativeEffect() {
		assertEquals(RiskDifference(),	(double)d_riskDif.getConfidenceInterval().getPointEstimate(), 0.00001);
	}
	
	@Test
	public void testGetRelativeEffectvsCooper1977() {
		assertEquals(s_cooper1977RD, d_cooperRD.getConfidenceInterval().getPointEstimate(), 0.001);	
	}
	
	@Test
	public void testGetCI() {
		double helper1 = (double)s_effectNum * (double)(s_sizeNum - s_effectNum) / Math.pow(s_sizeNum, 3);
		double helper2 = (double)s_effectDen * (double)(s_sizeDen - s_effectDen) / Math.pow(s_sizeDen, 3);
		double expectedSE = Math.sqrt(helper1 + helper2);		
		double t = StudentTTable.getT(s_sizeNum + s_sizeDen - 2);
		
		double lower = RiskDifference() - t * expectedSE;
		double upper = RiskDifference() + t * expectedSE;
		
		Interval<Double> ci = d_riskDif.getConfidenceInterval();
		assertEquals(lower, ci.getLowerBound(), 0.00001);
		assertEquals(upper, ci.getUpperBound(), 0.00001);		
	}

	private double RiskDifference() {
		return (double)s_effectNum/(double)s_sizeNum 
				- (double)s_effectDen/(double)s_sizeDen;
	}
	
	private double square(double d) {
		return d * d;
	}

	@Test
	public void testGetErrorvsCooper() {
		assertEquals(s_cooper1977RDvar,square(d_cooperRD.getError()),0.001);
	}
	
	@Test
	public void testBothRatesZeroShouldBeUndefined() { // although we can calculate a point estimate, the CI is not well defined.
		BasicRateMeasurement base = new BasicRateMeasurement(0, 10);
		BasicRateMeasurement subj = new BasicRateMeasurement(0, 10);
		BasicRiskDifference rd = new BasicRiskDifference(base, subj);
		assertFalse(rd.isDefined());
	}
	
	@Test
	public void testOneRateZeroShouldBeDefined() {
		BasicRateMeasurement base = new BasicRateMeasurement(0, 10);
		BasicRateMeasurement subj = new BasicRateMeasurement(5, 10);
		BasicRiskDifference rd = new BasicRiskDifference(base, subj);
		assertTrue(rd.isDefined());
		BasicRiskDifference rd2 = new BasicRiskDifference(subj, base);
		assertTrue(rd2.isDefined());
	}
}
