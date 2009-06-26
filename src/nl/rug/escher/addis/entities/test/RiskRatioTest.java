/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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
package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MutablePatientGroup;
import nl.rug.escher.addis.entities.RiskRatio;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.common.Interval;
import nl.rug.escher.common.StudentTTable;

import org.junit.Before;
import org.junit.Test;

public class RiskRatioTest {
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;
	private static final double s_meanNum = (double)s_effectNum / (double)s_sizeNum; 
	private static final double s_meanDen = (double)s_effectDen / (double)s_sizeDen;
	private static final double s_stdDevNum = s_meanNum / Math.sqrt(s_sizeNum);
	private static final double s_stdDevDen = s_meanDen / Math.sqrt(s_sizeDen);
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	RiskRatio d_ratio;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E");
		
		MutablePatientGroup g1 = new BasicPatientGroup(new BasicStudy("X"), new Drug("D"), new Dose(8.8, SIUnit.MILLIGRAMS_A_DAY), s_sizeNum);
		d_numerator = new BasicRateMeasurement(e, s_effectNum, s_sizeNum);
		g1.addMeasurement(d_numerator);
		
		MutablePatientGroup g2 = new BasicPatientGroup(new BasicStudy("X"), new Drug("F"), new Dose(8.8, SIUnit.MILLIGRAMS_A_DAY), s_sizeDen);
		d_denominator = new BasicRateMeasurement(e, s_effectDen, s_sizeDen);
		g2.addMeasurement(d_denominator);
		
		d_ratio = new RiskRatio(d_denominator, d_numerator);
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(new Endpoint("E"), d_ratio.getEndpoint());
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(s_sizeNum + s_sizeDen, (int)d_ratio.getSampleSize());
	}
	
	@Test
	public void testGetMean() {
		assertEquals(s_meanNum / s_meanDen, (double)d_ratio.getMean(), 0.00001);
	}
	
	@Test
	public void testGetCI() {
		double t = StudentTTable.getT(d_ratio.getSampleSize() - 2);
		double g = square(t * s_stdDevDen / s_meanDen);
		double q = d_ratio.getRatio();
		double sd = q / (1 - g) * Math.sqrt((1 - g) * square(s_stdDevNum) / square(s_meanNum) + 
				square(s_stdDevDen) / square(s_meanDen));
		double lower = q / (1 - g) - t * sd;
		double upper = q / (1 - g) + t * sd;
		
		Interval<Double> ci = d_ratio.getConfidenceInterval();
		assertEquals(lower, ci.getLowerBound(), 0.0001);
		assertEquals(upper, ci.getUpperBound(), 0.0001);
	}
	
	private double square(double d) {
		return d * d;
	}
	
	@Test
	public void testStdDev() {
		double sd = d_ratio.getConfidenceInterval().getLength() / (2 * 1.96);
		assertEquals(sd, d_ratio.getStdDev(), 0.00001);
	}
}