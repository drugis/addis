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

import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.SIUnit;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class DoseTest {
	@Test
	public void testSetUnit() {
		JUnitUtil.testSetter(new FixedDose(0.0, null), AbstractDose.PROPERTY_UNIT, null, SIUnit.MILLIGRAMS_A_DAY);
	}
	
	@Test
	public void testSetFlexibleDose() {
		JUnitUtil.testSetter(new FlexibleDose(new Interval<Double>(0.0,1.0), SIUnit.MILLIGRAMS_A_DAY), FlexibleDose.PROPERTY_FLEXIBLEDOSE, new Interval<Double>(0.0,1.0), new Interval<Double>(1.0,2.0));
	}
	
	@Test
	public void testSetMinDose() {
		JUnitUtil.testSetter(new FlexibleDose(new Interval<Double>(0.0,1.0), SIUnit.MILLIGRAMS_A_DAY), FlexibleDose.PROPERTY_MIN_DOSE, 0d, 10d);
	}
	
	@Test
	public void testSetMaxDose() {
		JUnitUtil.testSetter(new FlexibleDose(new Interval<Double>(0.0,1.0), SIUnit.MILLIGRAMS_A_DAY), FlexibleDose.PROPERTY_MAX_DOSE, 1d, 12d);
	}
	
	
	@Test
	public void testSetQuantity() {
		JUnitUtil.testSetter(new FixedDose(0.0, SIUnit.MILLIGRAMS_A_DAY), FixedDose.PROPERTY_QUANTITY, 0.0, 40.0);
	}
	
	@Test
	public void testToString() {
		FixedDose d = new FixedDose(0.0, null);
		assertEquals("INCOMPLETE", d.toString());
		d.setQuantity(25.5);
		d.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		assertEquals("25.5 " + SIUnit.MILLIGRAMS_A_DAY.toString(), d.toString());
	}
	
	@Test
	public void testToStringFlexibleDose() {
		FlexibleDose d = new FlexibleDose(new Interval<Double>(0.0,0.0), null);
		assertEquals("INCOMPLETE", d.toString());
		d.setFlexibleDose(new Interval<Double>(25D, 40D));
		d.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		assertEquals("25.0-40.0 " + SIUnit.MILLIGRAMS_A_DAY.toString(), d.toString());
	}
	
	@Test
	public void testEquals() {
		double q1 = 13.0;
		double q2 = 8.8;
		
		assertEquals(new FixedDose(q1, SIUnit.MILLIGRAMS_A_DAY),
				new FixedDose(q1, SIUnit.MILLIGRAMS_A_DAY));
		
		JUnitUtil.assertNotEquals(new FixedDose(q1, SIUnit.MILLIGRAMS_A_DAY),
				new FixedDose(q2, SIUnit.MILLIGRAMS_A_DAY));
		
		assertEquals(new FixedDose(q1, SIUnit.MILLIGRAMS_A_DAY).hashCode(),
				new FixedDose(q1, SIUnit.MILLIGRAMS_A_DAY).hashCode());
	}
	
	@Test
	public void testEqualsFlexible() {
		Interval<Double> q1 = new Interval<Double>(13.0, 15.0);
		Interval<Double> q2 = new Interval<Double>(8.8, 9.9);
		
		assertEquals(new FlexibleDose(q1, SIUnit.MILLIGRAMS_A_DAY),
				new FlexibleDose(q1, SIUnit.MILLIGRAMS_A_DAY));
		
		JUnitUtil.assertNotEquals(new FlexibleDose(q1, SIUnit.MILLIGRAMS_A_DAY),
				new FlexibleDose(q2, SIUnit.MILLIGRAMS_A_DAY));
		
		assertEquals(new FlexibleDose(q1, SIUnit.MILLIGRAMS_A_DAY).hashCode(),
				new FlexibleDose(q1, SIUnit.MILLIGRAMS_A_DAY).hashCode());
	}
}
