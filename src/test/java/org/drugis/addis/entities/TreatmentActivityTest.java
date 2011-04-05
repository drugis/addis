/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

public class TreatmentActivityTest {
	
	private TreatmentActivity d_pg;
	private TreatmentActivity d_orig;
	private TreatmentActivity d_clone;

	@Before
	public void setUp() {
		d_pg = new TreatmentActivity(null, null);
		d_orig = new TreatmentActivity(new Drug("Fluoxetine", "N06AB12"), new FixedDose(12.0, SIUnit.MILLIGRAMS_A_DAY));
		d_clone = d_orig.clone();
	}
	
	@Test
	public void testDescription() {
		assertEquals("Treatment (Fluoxetine 12.0 mg/day)", d_orig.getDescription());
		assertEquals("Treatment (undefined)", d_pg.getDescription());
		d_pg.setDose(d_orig.getDose());
		assertEquals("Treatment (undefined)", d_pg.getDescription());
		d_orig.setDose(null);
		assertEquals("Treatment (Fluoxetine)", d_orig.getDescription());
	}

	@Test
	public void testSetDrug() {
		JUnitUtil.testSetter(d_pg, TreatmentActivity.PROPERTY_DRUG, null, new Drug("D", "atc"));
	}
	
	@Test
	public void testSetDose() {
		JUnitUtil.testSetter(d_pg, TreatmentActivity.PROPERTY_DOSE, null, new FixedDose(1.0, SIUnit.MILLIGRAMS_A_DAY));
	}
	
	@Test
	public void testCloneReturnsEqualEntity() {
		assertEquals(d_orig, d_clone);
	}
	
	@Test
	public void testCloneReturnsDistinctObject() {
		assertFalse(d_orig == d_clone);
	}
	
	@Test
	public void testCloneReturnsDistinctDose() {
		assertFalse(d_orig.getDose() == d_clone.getDose());
	}

	@Test
	public void testEquals() {
		assertEquals(new TreatmentActivity(null, null), d_pg);
		assertEquals(new TreatmentActivity(new Drug("Fluoxetine", "N06AB12"), new FixedDose(12.0, SIUnit.MILLIGRAMS_A_DAY)), d_orig);
		JUnitUtil.assertNotEquals(new TreatmentActivity(null, null), d_orig);
		JUnitUtil.assertNotEquals(new TreatmentActivity(new Drug("Fluoxetine", "N06AB12"), null), d_orig);
		JUnitUtil.assertNotEquals(new TreatmentActivity(null, new FixedDose(12.0, SIUnit.MILLIGRAMS_A_DAY)), d_orig);
		
		assertEquals(new TreatmentActivity(null, null).hashCode(), d_pg.hashCode());
		assertEquals(new TreatmentActivity(new Drug("Fluoxetine", "N06AB12"), new FixedDose(12.0, SIUnit.MILLIGRAMS_A_DAY)).hashCode(), d_orig.hashCode());
	}

}
