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

package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.LogOddsRatio;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.Endpoint.Type;
import org.junit.Before;
import org.junit.Test;

public class LogOddsRatioTest {
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;
	private static final int s_nonNum = s_sizeNum - s_effectNum;
	private static final int s_nonDen = s_sizeDen - s_effectDen; 
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	LogOddsRatio d_ratio;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.RATE);
		
		d_numerator = new BasicRateMeasurement(e, s_effectNum, s_sizeNum);
		
		d_denominator = new BasicRateMeasurement(e, s_effectDen, s_sizeDen);
		
		d_ratio = new LogOddsRatio(d_denominator, d_numerator);
	}
	
	@Test
	public void testMean() {
		OddsRatio ratio = new OddsRatio(d_denominator, d_numerator);
		assertEquals(Math.log(ratio.getRatio()), d_ratio.getMean(), 0.00001);
	}
	
	@Test
	public void testStdDev() {
		assertEquals(Math.sqrt(1.0 / s_effectNum + 1.0 / s_effectDen + 1.0 / s_nonNum + 1.0 / s_nonDen),
				d_ratio.getStdDev(), 0.00001);
	}
	
	@Test
	public void testGetLabel() {
		assertEquals(0.31 + "\u00B1" + 0.24, d_ratio.getLabel());
	}
}
