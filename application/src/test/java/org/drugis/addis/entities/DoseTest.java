/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class DoseTest {
	@Test
	public void testSetUnit() {
		JUnitUtil.testSetter(new FixedDose(0.0, null), AbstractDose.PROPERTY_DOSE_UNIT, null, DoseUnit.MILLIGRAMS_A_DAY);
	}
	
	@Test
	public void testSetFlexibleDose() {
		JUnitUtil.testSetter(new FlexibleDose(new Interval<Double>(0.0,1.0), DoseUnit.MILLIGRAMS_A_DAY), FlexibleDose.PROPERTY_FLEXIBLEDOSE, new Interval<Double>(0.0,1.0), new Interval<Double>(1.0,2.0));
	}
	
	@Test
	public void testSetMinDose() {
		JUnitUtil.testSetter(new FlexibleDose(new Interval<Double>(0.0,1.0), DoseUnit.MILLIGRAMS_A_DAY), FlexibleDose.PROPERTY_MIN_DOSE, 0d, 10d);
	}
	
	@Test
	public void testSetMaxDose() {
		JUnitUtil.testSetter(new FlexibleDose(new Interval<Double>(0.0,1.0), DoseUnit.MILLIGRAMS_A_DAY), FlexibleDose.PROPERTY_MAX_DOSE, 1d, 12d);
	}
	
	
	@Test
	public void testSetQuantity() {
		JUnitUtil.testSetter(new FixedDose(0.0, DoseUnit.MILLIGRAMS_A_DAY), FixedDose.PROPERTY_QUANTITY, 0.0, 40.0);
	}
	
	@Test
	public void testToString() {
		FixedDose d = new FixedDose(0.0, null);
		assertEquals("INCOMPLETE", d.toString());
		d.setQuantity(25.5);
		d.setDoseUnit(DoseUnit.MILLIGRAMS_A_DAY);
		assertEquals("25.5 " + DoseUnit.MILLIGRAMS_A_DAY.getLabel(), d.toString());
	}
	
	@Test
	public void testToStringFlexibleDose() {
		FlexibleDose d = new FlexibleDose(new Interval<Double>(0.0,0.0), null);
		assertEquals("INCOMPLETE", d.toString());
		d.setFlexibleDose(new Interval<Double>(25D, 40D));
		d.setDoseUnit(DoseUnit.MILLIGRAMS_A_DAY);
		assertEquals("25.0-40.0 " + DoseUnit.MILLIGRAMS_A_DAY.getLabel(), d.toString());
	}
	
	@Test
	public void testEquals() {
		double q1 = 13.0;
		double q2 = 8.8;
		
		assertEquals(new FixedDose(q1, DoseUnit.MILLIGRAMS_A_DAY),
				new FixedDose(q1, DoseUnit.MILLIGRAMS_A_DAY));
		
		JUnitUtil.assertNotEquals(new FixedDose(q1, DoseUnit.MILLIGRAMS_A_DAY),
				new FixedDose(q2, DoseUnit.MILLIGRAMS_A_DAY));
		
		assertEquals(new FixedDose(q1, DoseUnit.MILLIGRAMS_A_DAY).hashCode(),
				new FixedDose(q1, DoseUnit.MILLIGRAMS_A_DAY).hashCode());
	}
	
	@Test
	public void testEqualsFlexible() {
		Interval<Double> q1 = new Interval<Double>(13.0, 15.0);
		Interval<Double> q2 = new Interval<Double>(8.8, 9.9);
		
		assertEquals(new FlexibleDose(q1, DoseUnit.MILLIGRAMS_A_DAY),
				new FlexibleDose(q1, DoseUnit.MILLIGRAMS_A_DAY));
		
		JUnitUtil.assertNotEquals(new FlexibleDose(q1, DoseUnit.MILLIGRAMS_A_DAY),
				new FlexibleDose(q2, DoseUnit.MILLIGRAMS_A_DAY));
		
		assertEquals(new FlexibleDose(q1, DoseUnit.MILLIGRAMS_A_DAY).hashCode(),
				new FlexibleDose(q1, DoseUnit.MILLIGRAMS_A_DAY).hashCode());
	}
	
	@Test
	public void testCloneFixedDose() {
		FixedDose dose = new FixedDose(12.5, DoseUnit.MILLIGRAMS_A_DAY);
		assertEquals(dose, dose.clone());
		assertNotSame(dose, dose.clone());
		assertNotSame(dose.getDoseUnit(), dose.clone().getDoseUnit());
	}
	
	@Test
	public void testCloneFlexibleDose() {
		Interval<Double> q1 = new Interval<Double>(13.0, 15.0);
		FlexibleDose dose = new FlexibleDose(q1, DoseUnit.MILLIGRAMS_A_DAY);
		assertEquals(dose, dose.clone());
		assertNotSame(dose, dose.clone());
		assertNotSame(dose.getDoseUnit(), dose.clone().getDoseUnit());
	}
	
	@Test
	public void testCloneUnknownDose() {
		UnknownDose dose = new UnknownDose();
		assertEquals(dose, dose.clone());
		assertNotSame(dose, dose.clone());
	}
	
	@Test
	public void testUnitDifferCompare() {
		FixedDose fixd1 = new FixedDose(2400, DoseUnit.MILLIGRAMS_A_DAY);
		FixedDose fixd2 = new FixedDose(0.0001, ExampleData.KILOGRAMS_PER_HOUR);
		assertTrue(fixd1.equals(fixd2));
		
		
		FlexibleDose flex1 = new FlexibleDose(new Interval<Double>(2400.0, 24000.0), DoseUnit.MILLIGRAMS_A_DAY);
		FlexibleDose flex2 = new FlexibleDose(new Interval<Double>(0.0001, 0.001), ExampleData.KILOGRAMS_PER_HOUR);
		assertTrue(flex1.equals(flex2));

	}
}
