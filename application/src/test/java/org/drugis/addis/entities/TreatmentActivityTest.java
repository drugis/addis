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

import java.util.HashSet;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class TreatmentActivityTest {

	private TreatmentActivity d_orig;
	private TreatmentActivity d_clone;
	private TreatmentActivity d_empty;

	@Before
	public void setUp() {
		d_empty = new TreatmentActivity();
		d_orig = new TreatmentActivity();
		d_orig.addTreatment(new Drug("Fluoxetine", "N06AB12"), new FixedDose(12.0, DoseUnit.createMilliGramsPerDay()));
		d_orig.addTreatment(new Drug("Paroxetine", "N062"), new FlexibleDose(new Interval<Double>(3.0, 7.0), DoseUnit.createMilliGramsPerDay()));
		d_clone = d_orig.clone();
	}
	
	@Test
	public void testDescription() {
		assertEquals("Fluoxetine 12.0 mg/day + Paroxetine 3.0-7.0 mg/day", d_orig.getLabel());
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
	public void testCloneReturnsDistinctDoses() {
		assertFalse(d_orig.getTreatments().get(0).getDose() == d_clone.getTreatments().get(0).getDose());
	}
	
	@Test
	public void testEquals() {
		assertEquals(new TreatmentActivity(), d_empty);
		TreatmentActivity ct = new TreatmentActivity();
		ct.addTreatment(new Drug("Fluoxetine", "N06AB12"), new FixedDose(12.0, DoseUnit.createMilliGramsPerDay()));
		JUnitUtil.assertNotEquals(ct, d_orig);
		ct.addTreatment(new Drug("Paroxetine", "N062"), new FlexibleDose(new Interval<Double>(3.0, 7.0), DoseUnit.createMilliGramsPerDay()));
		assertEquals(ct, d_orig);
		JUnitUtil.assertNotEquals(d_empty, d_orig);
	}
	
	@Test
	public void testDependencies() {
		Drug drug = ExampleData.buildDrugCandesartan();
		final TreatmentActivity ta = new TreatmentActivity(new DrugTreatment(drug, new UnknownDose()));
		Set<Entity> expected = new HashSet<Entity>();
		expected.add(drug);
		assertEquals(expected, ta.getDependencies());
		
		ta.addTreatment(drug, new FixedDose(3.0, ExampleData.KILOGRAMS_PER_HOUR));
		expected.add(ExampleData.KILOGRAMS_PER_HOUR.getUnit());
		assertEquals(expected, ta.getDependencies());		
	}
}
