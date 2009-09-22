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

import static org.junit.Assert.*;

import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.LogRiskRatio;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;


public class LogRiskRatioTest {

	private LogRiskRatio d_ratio;

	@Before
	public void setUp() {
		Endpoint e = new Endpoint("e", Type.RATE);
		BasicRateMeasurement r1 = new BasicRateMeasurement(e, 341, 595);
		BasicRateMeasurement r2 = new BasicRateMeasurement(e, 377, 595);
		d_ratio = new LogRiskRatio(r1, r2);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(Math.log(1.105), d_ratio.getMean(), 0.001);
	}
	
	@Test
	public void testGetStdDev() {
		assertEquals(0.04715, d_ratio.getStdDev(), 0.00001);
	}
	
	@Test
	public void testGetConfidenceInterval() {
		Interval<Double> ival = d_ratio.getConfidenceInterval();
		assertEquals(1.01, ival.getLowerBound(), 0.01);
		assertEquals(1.20, ival.getUpperBound(), 0.01);
	}
}
