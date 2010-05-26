/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.common.StudentTTable;
import org.junit.Before;
import org.junit.Test;

public class BasicMeanDifferenceTest {
	private static final double s_mean1 = 0.2342;
	private static final double s_mean2 = 4.7811;
	private static final double s_stdDev1 = 0.2;
	private static final double s_stdDev2 = 2.5;
	private static final int s_subjSize = 35;
	private static final int s_baslSize = 41;
	private BasicMeanDifference d_md;
	private BasicContinuousMeasurement d_subject;
	private BasicContinuousMeasurement d_baseline;
	
	@Before
	public void setUp() {
		Arm subjs = new Arm(null, null, s_subjSize);
		Arm basels = new Arm(null, null, s_baslSize);
		d_subject = new BasicContinuousMeasurement(s_mean1, s_stdDev1, subjs.getSize());
		d_baseline = new BasicContinuousMeasurement(s_mean2, s_stdDev2, basels.getSize());
		d_md = new BasicMeanDifference(d_baseline, d_subject);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(s_mean1 - s_mean2, d_md.getConfidenceInterval().getPointEstimate(),0.0001);
	}
	
	@Test
	public void testGetError() {
		double expected = Math.sqrt(square(s_stdDev1) / (double) s_subjSize + square(s_stdDev2) / (double) s_baslSize);
		assertEquals(expected, d_md.getError(),0.0001);
	}

	@Test
	public void testGetCI() {
		double t = StudentTTable.getT(s_subjSize + s_baslSize - 2);
		double upper = d_md.getConfidenceInterval().getPointEstimate() + t*d_md.getError();
		double lower = d_md.getConfidenceInterval().getPointEstimate() - t*d_md.getError();
		assertEquals(upper, d_md.getConfidenceInterval().getUpperBound(), 0.0001);
		assertEquals(lower, d_md.getConfidenceInterval().getLowerBound(), 0.0001);
	}
	
	@Test
	public void testGetSampleSize() {
		int expected = s_subjSize + s_baslSize;
		assertEquals(expected, (int) d_md.getSampleSize());
	}
	
	private double square(double x) {
		return x*x;
	}
}
