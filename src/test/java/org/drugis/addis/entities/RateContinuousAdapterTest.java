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

import org.drugis.addis.entities.Endpoint.Type;
import org.junit.Before;
import org.junit.Test;

public class RateContinuousAdapterTest {
	BasicRateMeasurement d_rate;
	ContinuousMeasurement d_continuous;
	
	@Before
	public void setUp() {
		PatientGroup pden = new BasicPatientGroup(null,null,null,100);
		d_rate = new BasicRateMeasurement(new Endpoint("e", Type.RATE), 50, pden);
		d_continuous = new RateContinuousAdapter(d_rate);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(0.5, d_continuous.getMean(), 0.000001);
	}
	
	@Test
	public void testGetStdDev() {
		assertEquals(0.5/Math.sqrt(100), d_continuous.getStdDev(), 0.0000001);
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(100, (int)d_continuous.getSampleSize());
	}
	
}
