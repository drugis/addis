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

import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.SIUnit;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class DoseTest {
	@Test
	public void testSetUnit() {
		JUnitUtil.testSetter(new Dose(0.0, null), Dose.PROPERTY_UNIT, null, SIUnit.MILLIGRAMS_A_DAY);
	}
	
	@Test
	public void testSetFlexibleDose() {
		JUnitUtil.testSetter(new Dose(new Interval<Double>(0.0,1.0), null), Dose.PROPERTY_FLEXIBLEDOSE, new Interval<Double>(0.0,1.0), new Interval<Double>(1.0,2.0));
	}
	
	@Test
	public void testSetQuantity() {
		JUnitUtil.testSetter(new Dose(0.0, null), Dose.PROPERTY_QUANTITY, 0.0, 40.0);
	}
	
	@Test
	public void testToString() {
		Dose d = new Dose(0.0, null);
		assertEquals("INCOMPLETE", d.toString());
		d.setQuantity(25.5);
		d.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		assertEquals("25.5 " + SIUnit.MILLIGRAMS_A_DAY.toString(), d.toString());
	}
	
	@Test
	public void testToStringFlexibleDose() {
		Dose d = new Dose(new Interval<Double>(0.0,0.0), null);
		assertEquals("INCOMPLETE", d.toString());
		d.setFlexibleDose(new Interval<Double>(25D, 40D));
		d.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		assertEquals("25.0 - 40.0 " + SIUnit.MILLIGRAMS_A_DAY.toString(), d.toString());
	}
	
	@Test
	public void IsFlexbile() {
		Dose d1 = new Dose(new Interval<Double>(25D,40D), SIUnit.MILLIGRAMS_A_DAY);
		assertTrue(d1.isFlexible());
		Dose d2 = new Dose(30, SIUnit.MILLIGRAMS_A_DAY);
		assertFalse(d2.isFlexible());
	}
	
	
	@Test
	public void testEquals() {
		double q1 = 13.0;
		double q2 = 8.8;
		
		assertEquals(new Dose(q1, SIUnit.MILLIGRAMS_A_DAY),
				new Dose(q1, SIUnit.MILLIGRAMS_A_DAY));
		
		JUnitUtil.assertNotEquals(new Dose(q1, SIUnit.MILLIGRAMS_A_DAY),
				new Dose(q2, SIUnit.MILLIGRAMS_A_DAY));
		
		assertEquals(new Dose(q1, SIUnit.MILLIGRAMS_A_DAY).hashCode(),
				new Dose(q1, SIUnit.MILLIGRAMS_A_DAY).hashCode());
	}
	
	@Test
	public void testEqualsFlexible() {
		Interval<Double> q1 = new Interval<Double>(13.0, 15.0);
		Interval<Double> q2 = new Interval<Double>(8.8, 9.9);
		
		assertEquals(new Dose(q1, SIUnit.MILLIGRAMS_A_DAY),
				new Dose(q1, SIUnit.MILLIGRAMS_A_DAY));
		
		JUnitUtil.assertNotEquals(new Dose(q1, SIUnit.MILLIGRAMS_A_DAY),
				new Dose(q2, SIUnit.MILLIGRAMS_A_DAY));
		
		assertEquals(new Dose(q1, SIUnit.MILLIGRAMS_A_DAY).hashCode(),
				new Dose(q1, SIUnit.MILLIGRAMS_A_DAY).hashCode());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalDoseSetting() {
		Dose d1 = new Dose(new Interval<Double>(1.0,2.0), SIUnit.MILLIGRAMS_A_DAY);
		d1.setQuantity(20.0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalDoseSetting2() {
		Dose d2 = new Dose(20.0, SIUnit.MILLIGRAMS_A_DAY);
		d2.setFlexibleDose(new Interval<Double>(1.0, 2.0));
	}
}
