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

package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.drugis.common.JUnitUtil.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.junit.Before;
import org.junit.Test;

public class TreatmentCategorySetTest {
	private TreatmentCategorySet d_empty;
	private TreatmentCategorySet d_single;
	private TreatmentCategorySet d_multi;
	private List<Category> d_multiDrug;
	private Category d_singleDrug;
	
	@Before
	public void setUp() {
		d_empty = new TreatmentCategorySet();
		d_singleDrug = Category.createTrivial(ExampleData.buildDrugEscitalopram());
		d_single = new TreatmentCategorySet(d_singleDrug);
		d_multiDrug = Arrays.asList(
				Category.createTrivial(ExampleData.buildDrugCandesartan()),
				Category.createTrivial(ExampleData.buildDrugViagra()),
				Category.createTrivial(ExampleData.buildDrugViagra()));
		d_multi = new TreatmentCategorySet(d_multiDrug);
	}

	@Test
	public void testNoArgConstruction() {
		assertEquals(Collections.emptySet(), d_empty.getContents());
	}
	
	@Test
	public void testDrugConstruction() {
		assertEquals(Collections.singleton(d_singleDrug), d_single.getContents());
	}
	
	@Test
	public void testCollectionConstruction() {
		assertEquals(new HashSet<Category>(d_multiDrug), d_multi.getContents());
	}
	
	@Test
	public void testGetName() {
		assertEquals("", d_empty.getLabel());
		assertEquals(d_singleDrug.getLabel(), d_single.getLabel());
		assertEquals("Candesartan + Viagra", d_multi.getLabel());
		
		// test alphabetic
		List<Drug> more = Arrays.asList(new Drug[] {
			ExampleData.buildDrugSertraline(),			
			ExampleData.buildDrugParoxetine(),
			ExampleData.buildDrugFluoxetine(),
			ExampleData.buildDrugViagra()
		});
		assertEquals("Fluoxetine + Paroxetine + Sertraline + Viagra", TreatmentCategorySet.createTrivial(more).getLabel());
	}
	
	@Test
	public void testEquals() {
		assertEquals(new TreatmentCategorySet(), d_empty);
		assertEquals(new TreatmentCategorySet().hashCode(), d_empty.hashCode());
		assertEquals(new TreatmentCategorySet(d_singleDrug), d_single);
		assertEquals(new TreatmentCategorySet(d_singleDrug).hashCode(), d_single.hashCode());
		assertEquals(new TreatmentCategorySet(d_multiDrug), d_multi);
		assertEquals(new TreatmentCategorySet(d_multiDrug).hashCode(), d_multi.hashCode());
		
		assertNotEquals(d_empty, d_single);
		assertNotEquals(d_empty, d_multi);
		assertNotEquals(d_single, d_multi);

		assertNotEquals(d_empty, null);
		assertNotEquals(d_empty, new Object());
	}
	
	@Test
	public void testDeepEquals() {
		// consistency with equals
		assertFalse(d_empty.deepEquals(d_single));
		assertFalse(d_empty.deepEquals(null));
		assertFalse(d_empty.deepEquals(d_singleDrug));
		
		// identical contents
		assertTrue(d_empty.deepEquals(new TreatmentCategorySet()));
		assertTrue(d_single.deepEquals(new TreatmentCategorySet(d_singleDrug)));
		assertTrue(d_multi.deepEquals(new TreatmentCategorySet(d_multiDrug)));

		// nearly identical contents
		Drug drug = new Drug(d_singleDrug.getName(), "ATCFORYOUMYFRIEND");
		assertFalse(d_single.deepEquals(TreatmentCategorySet.createTrivial(drug)));
	}
	
	@Test
	public void testCompareTo() {
		assertEquals(0, d_empty.compareTo(d_empty));
		assertEquals(0, d_empty.compareTo(new TreatmentCategorySet()));
		assertEquals(0, d_single.compareTo(new TreatmentCategorySet(d_singleDrug)));
		assertEquals(0, d_multi.compareTo(new TreatmentCategorySet(d_multiDrug)));
		
		assertTrue(d_empty.compareTo(d_single) < 0); // {} < {Escitalopram}
		assertTrue(d_single.compareTo(TreatmentCategorySet.createTrivial(ExampleData.buildDrugCitalopram())) > 0); // {Escitalopram} > {Citalopram} 
		assertEquals(d_empty.compareTo(d_single), -d_single.compareTo(d_empty));
		TreatmentCategorySet two1 = new TreatmentCategorySet(Arrays.asList(new Category[] {
				d_singleDrug, Category.createTrivial(ExampleData.buildDrugCitalopram())
		}));
		assertTrue(d_single.compareTo(two1) > 0); // {Escitalopram} > {Citalopram, Escitalopram}
		TreatmentCategorySet two2 = new TreatmentCategorySet(Arrays.asList(new Category[] {
				d_singleDrug, Category.createTrivial(ExampleData.buildDrugFluoxetine())
		}));
		assertTrue(d_single.compareTo(two2) < 0); // {Escitalopram} < {Escitalopram, Fluoxetine}
	}
}
