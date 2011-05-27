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

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;
import org.junit.Before;
import org.junit.Test;

public class BasicStandardisedMeanDifferenceTest {
	//Example data from The Handbook of Research Synthesis and Meta-Analysis page 226-227
	private static final double s_subjMean = 103;
	private static final double s_baselMean = 100;
	private static final double s_subjStdDev = 5.5;
	private static final double s_baslStdDev = 4.5;
	private static final int s_subjSize = 50;
	private static final int s_baslSize = 50;
	int d_sampleSize = s_subjSize + s_baslSize;
	
	private BasicStandardisedMeanDifference d_smd;
	private BasicContinuousMeasurement d_subject;
	private BasicContinuousMeasurement d_baseline;
	
	@Before
	public void setUp() {
		Arm subjs = new Arm("subj", s_subjSize);
		Arm basels = new Arm("basl", s_baslSize);
		d_subject = new BasicContinuousMeasurement(s_subjMean, s_subjStdDev, subjs.getSize());
		d_baseline = new BasicContinuousMeasurement(s_baselMean, s_baslStdDev, basels.getSize());
		d_smd = new BasicStandardisedMeanDifference(d_baseline, d_subject);
	}
	
	@Test
	public void testGetMean() {
		double expected = getSMD();
		assertEquals(expected, d_smd.getConfidenceInterval().getPointEstimate(),0.0001);
	}

	@Test
	public void testGetError() {
		double firstFactor = (double) d_sampleSize / ((double) s_subjSize * (double) s_baslSize);
		double secondFactor = square(getSMD()) / (2 * ((double) d_sampleSize - 3.94));
		double expected = Math.sqrt(firstFactor + secondFactor);
		assertEquals(expected, d_smd.getError(), 0.01);
	}
	
	@Test
	public void testGetCI() {
		double t = StudentTTable.getT(d_sampleSize - 2);
		double upper = d_smd.getConfidenceInterval().getPointEstimate() + d_smd.getError() * t;
		double lower = d_smd.getConfidenceInterval().getPointEstimate() - d_smd.getError() * t;
		Interval<Double> interval = d_smd.getConfidenceInterval();
		assertEquals(upper, interval.getUpperBound(),0.0001);
		assertEquals(lower, interval.getLowerBound(),0.0001);
	}
	
	@Test
	public void testGetCohend() {
		double expected = (s_subjMean - s_baselMean)/getPooledStdDev();
		assertEquals(expected, d_smd.getCohenD(), 0.0001);
	}
	
	@Test
	public void testGetCohenVariance() {
		double expected = (double) d_sampleSize/((double) s_subjSize * (double) s_baslSize) 
							+ square(d_smd.getCohenD()) / (2 * (double) d_sampleSize);
		assertEquals(expected, d_smd.getCohenVariance(), 0.0001);
	}
	
	@Test
	public void testGetCorrectionJ() {
		double expected = 1 - (3 / (4 * ((double) d_sampleSize - 2) - 1));
		assertEquals(expected, d_smd.getCorrectionJ(), 0.0001);
	}
	
	@Test
	public void testOutcomesEqualToBook() {
		assertEquals(0.5970D, d_smd.getCohenD(), 0.0001);
		assertEquals(0.0418D, d_smd.getCohenVariance(), 0.0001);
		assertEquals(0.9923D, d_smd.getCorrectionJ(), 0.0001);
		assertEquals(0.5924D, d_smd.getConfidenceInterval().getPointEstimate(), 0.0001);
		assertEquals(Math.sqrt(0.04114D), d_smd.getError(), 0.0001);
	}
	
	private double square(double x) {
		return x*x;
	}
	
	private double getSMD() {
		double pooledStdDev = getPooledStdDev();
		
		double firstFactor = (s_subjMean - s_baselMean) / pooledStdDev;
		
		double secondFactor = 1 - (3 / (4 * (double) d_sampleSize - 9));
		double expected = firstFactor * secondFactor;
		return expected;
	}

	private double getPooledStdDev() {
		double numerator = ( (double) s_subjSize - 1) * square(s_subjStdDev) + ((double) s_baslSize - 1) * square(s_baslStdDev);
		double pooledStdDev = Math.sqrt(numerator / (double) (s_subjSize + s_baslSize - 2));
		return pooledStdDev;
	}
}
