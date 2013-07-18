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

package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class BasicContinuousMeasurementTest {
	private BasicContinuousMeasurement d_basicContinuousMeasurement;
	
	@Before
	public void setUp() {
		d_basicContinuousMeasurement = new BasicContinuousMeasurement(0.0, 0.1, 1);
	}

	@Test
	public void testSetMean() {
		JUnitUtil.testSetter(d_basicContinuousMeasurement,
				BasicContinuousMeasurement.PROPERTY_MEAN, 0.0, 25.91);
	}

	@Test
	public void testSetStdDev() {
		JUnitUtil.testSetter(d_basicContinuousMeasurement, 
				BasicContinuousMeasurement.PROPERTY_STDDEV, 0.1, 0.46);
	}
	
	@Test
	public void testToString() {
		BasicContinuousMeasurement m = d_basicContinuousMeasurement;
		assertEquals("0.0 \u00B1 0.1 (1)", m.toString());
		BasicContinuousMeasurement m2 = new BasicContinuousMeasurement(null, null, 5);
		assertEquals("N/A \u00B1 N/A (5)", m2.toString());
	}
	
	@Test
	public void testIsComplete() {
		assertTrue(d_basicContinuousMeasurement.isComplete());
		BasicContinuousMeasurement m1 = new BasicContinuousMeasurement(0.0, 0.0, null);
		assertFalse(m1.isComplete());
		BasicContinuousMeasurement m2 = new BasicContinuousMeasurement(0.0, null, 100);
		assertFalse(m2.isComplete());
		BasicContinuousMeasurement m3 = new BasicContinuousMeasurement(null, 0.0, 100);
		assertFalse(m3.isComplete());
		
		BasicContinuousMeasurement m4 = new BasicContinuousMeasurement(2.3, 0.15, 0);
		assertFalse(m4.isComplete());
		BasicContinuousMeasurement m5 = new BasicContinuousMeasurement(2.3, -0.2, 30);
		assertFalse(m5.isComplete());
		BasicContinuousMeasurement m6 = new BasicContinuousMeasurement(2.3, 0.0, 30);
		assertFalse(m6.isComplete());
		BasicContinuousMeasurement m7 = new BasicContinuousMeasurement(2.3, 1.0, -30);
		assertFalse(m7.isComplete());
		BasicContinuousMeasurement m8 = new BasicContinuousMeasurement(-2.3, 1.0, 30);
		assertTrue(m8.isComplete());
	}
	
	@Test
	public void testClone() {
		Measurement m = new BasicContinuousMeasurement(13.5, 20.0, 50);
		Measurement clone = m.clone();
		assertEquals(m, clone);
		assertNotSame(m, clone);
	}

}
