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

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.relativeeffect.LogContinuousMeasurementEstimate;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class LogContinuousMeasurementEstimateTest {

	private LogContinuousMeasurementEstimate d_logEstimate;

	@Before
	public void setUp() {
		d_logEstimate = new LogContinuousMeasurementEstimate(1.73, 0.42);
	}
	
	private BasicContinuousMeasurement getMeasurement() {
		return d_logEstimate;
	}
	
	@Test
	public void testGetSetMean() {
		JUnitUtil.testSetter(getMeasurement(),
				LogContinuousMeasurementEstimate.PROPERTY_MEAN, 1.73, 25.91);
	}
	
	@Test
	public void testGetSetStdDev() {
		JUnitUtil.testSetter(getMeasurement(), LogContinuousMeasurementEstimate.PROPERTY_STDDEV, 0.42, 0.46);
	}
	
	@Test
	public void testGetConfidenceInterval() {
		assertEquals(2.476423,  d_logEstimate.getConfidenceInterval().getLowerBound(), 0.000001);
		assertEquals(12.847958, d_logEstimate.getConfidenceInterval().getUpperBound(), 0.000001);
	}
	
	@Test
	public void testToString() {
		assertEquals("5.641 (2.476, 12.848)", d_logEstimate.toString());
		assertEquals("n/a", new LogContinuousMeasurementEstimate(null, null).toString());
	}
}
