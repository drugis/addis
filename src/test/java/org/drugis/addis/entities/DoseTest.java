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
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class DoseTest {
	@Test
	public void testSetUnit() {
		JUnitUtil.testSetter(new Dose(0.0, null), Dose.PROPERTY_UNIT, null, SIUnit.MILLIGRAMS_A_DAY);
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
}
