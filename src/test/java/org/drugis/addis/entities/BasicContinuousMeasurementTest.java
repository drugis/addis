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

package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class BasicContinuousMeasurementTest {
	private BasicContinuousMeasurement d_basicContinuousMeasurement;
	
	@Before
	public void setUp() {
		d_basicContinuousMeasurement = new BasicContinuousMeasurement(0.0, 0.0, 1);
	}

	private BasicContinuousMeasurement getMeasurement() {
		return d_basicContinuousMeasurement;
	}
	
	@Test
	public void testSetMean() {
		JUnitUtil.testSetter(getMeasurement(),
				BasicContinuousMeasurement.PROPERTY_MEAN, 0.0, 25.91);
	}

	@Test
	public void testSetStdDev() {
		JUnitUtil.testSetter(getMeasurement(), BasicContinuousMeasurement.PROPERTY_STDDEV, 0.0, 0.46);
	}
	
	@Test
	public void testToString() {
		BasicContinuousMeasurement m = getMeasurement();
		assertEquals("0.0 \u00B1 0.0 (1)", m.toString());
	}
	
	@Test
	public void testClone() {
		Measurement m = new BasicContinuousMeasurement(13.5, 20.0, 50);
		assertEquals(m, m.clone());
		assertFalse(m == m.clone());
	}
}
