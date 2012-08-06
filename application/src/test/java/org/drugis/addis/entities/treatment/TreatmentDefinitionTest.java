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

import static org.drugis.common.JUnitUtil.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.TreatmentActivity;
import org.junit.Before;
import org.junit.Test;

public class TreatmentDefinitionTest {
	private TreatmentDefinition d_empty;
	private TreatmentDefinition d_single;
	private TreatmentDefinition d_multi;
	private List<Category> d_multiCategory;
	private Category d_singleCategory;
	
	@Before
	public void setUp() {
		d_empty = new TreatmentDefinition();
		d_singleCategory = Category.createTrivial(ExampleData.buildDrugEscitalopram());
		d_single = new TreatmentDefinition(d_singleCategory);
		d_multiCategory = Arrays.asList(
				Category.createTrivial(ExampleData.buildDrugCandesartan()),
				Category.createTrivial(ExampleData.buildDrugViagra()),
				Category.createTrivial(ExampleData.buildDrugViagra()));
		d_multi = new TreatmentDefinition(d_multiCategory);
	}

	@Test
	public void testNoArgConstruction() {
		assertEquals(Collections.emptySet(), d_empty.getContents());
	}
	
	@Test
	public void testDrugConstruction() {
		assertEquals(Collections.singleton(d_singleCategory), d_single.getContents());
	}
	
	@Test
	public void testCollectionConstruction() {
		assertEquals(new HashSet<Category>(d_multiCategory), d_multi.getContents());
	}
	
	@Test
	public void testGetName() {
		assertEquals("", d_empty.getLabel());
		assertEquals(d_singleCategory.getLabel(), d_single.getLabel());
		assertEquals("Candesartan + Viagra", d_multi.getLabel());
		
		// test alphabetic
		List<Drug> more = Arrays.asList(new Drug[] {
			ExampleData.buildDrugSertraline(),			
			ExampleData.buildDrugParoxetine(),
			ExampleData.buildDrugFluoxetine(),
			ExampleData.buildDrugViagra()
		});
		assertEquals("Fluoxetine + Paroxetine + Sertraline + Viagra", TreatmentDefinition.createTrivial(more).getLabel());
	}
	
	@Test
	public void testEquals() {
		assertEquals(new TreatmentDefinition(), d_empty);
		assertEquals(new TreatmentDefinition().hashCode(), d_empty.hashCode());
		assertEquals(new TreatmentDefinition(d_singleCategory), d_single);
		assertEquals(new TreatmentDefinition(d_singleCategory).hashCode(), d_single.hashCode());
		assertEquals(new TreatmentDefinition(d_multiCategory), d_multi);
		assertEquals(new TreatmentDefinition(d_multiCategory).hashCode(), d_multi.hashCode());
		
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
		assertFalse(d_empty.deepEquals(d_singleCategory));
		
		// identical contents
		assertTrue(d_empty.deepEquals(new TreatmentDefinition()));
		assertTrue(d_single.deepEquals(new TreatmentDefinition(d_singleCategory)));
		assertTrue(d_multi.deepEquals(new TreatmentDefinition(d_multiCategory)));

		// nearly identical contents
		Drug drug = new Drug(d_singleCategory.getCategorization().getDrug().getName(), "ATCFORYOUMYFRIEND");
		TreatmentDefinition cat = TreatmentDefinition.createTrivial(drug);
		assertEquals(d_single, cat);
		assertFalse(d_single.deepEquals(cat));
	}
	
	@Test
	public void testCompareTo() {
		assertEquals(0, d_empty.compareTo(d_empty));
		assertEquals(0, d_empty.compareTo(new TreatmentDefinition()));
		assertEquals(0, d_single.compareTo(new TreatmentDefinition(d_singleCategory)));
		assertEquals(0, d_multi.compareTo(new TreatmentDefinition(d_multiCategory)));
		
		assertTrue(d_empty.compareTo(d_single) < 0); // {} < {Escitalopram}
		assertTrue(d_single.compareTo(TreatmentDefinition.createTrivial(ExampleData.buildDrugCitalopram())) > 0); // {Escitalopram} > {Citalopram} 
		assertEquals(d_empty.compareTo(d_single), -d_single.compareTo(d_empty));
		TreatmentDefinition two1 = new TreatmentDefinition(Arrays.asList(new Category[] {
				d_singleCategory, Category.createTrivial(ExampleData.buildDrugCitalopram())
		}));
		assertTrue(d_single.compareTo(two1) > 0); // {Escitalopram} > {Citalopram, Escitalopram}
		TreatmentDefinition two2 = new TreatmentDefinition(Arrays.asList(new Category[] {
				d_singleCategory, Category.createTrivial(ExampleData.buildDrugFluoxetine())
		}));
		assertTrue(d_single.compareTo(two2) < 0); // {Escitalopram} < {Escitalopram, Fluoxetine}
	}
	
	@Test
	public void testMatch() { 
		TreatmentActivity act1 = new TreatmentActivity();
		act1.addTreatment(ExampleData.buildDrugEscitalopram(), new FixedDose(12.0, DoseUnit.MILLIGRAMS_A_DAY));
		
		TreatmentActivity act2 = new TreatmentActivity();
		act2.addTreatment(ExampleData.buildDrugViagra(), new FixedDose(12.0, DoseUnit.MILLIGRAMS_A_DAY));
		assertTrue(d_single.match(act1));
		assertFalse(d_single.match(act2));
		
		TreatmentActivity act3 = new TreatmentActivity();		
		act3.addTreatment(ExampleData.buildDrugViagra(), new FlexibleDose(3.0, 7.0, DoseUnit.MILLIGRAMS_A_DAY));
		FixedDose fixed1 = new FixedDose(12.0, DoseUnit.MILLIGRAMS_A_DAY);
		act3.addTreatment(ExampleData.buildDrugCandesartan(), fixed1);
		
		TreatmentActivity act4 = new TreatmentActivity();
		act4.addTreatment(ExampleData.buildDrugCitalopram(), new FlexibleDose(1.0, 12.0, DoseUnit.MILLIGRAMS_A_DAY));
		act4.addTreatment(ExampleData.buildDrugEscitalopram(), new FlexibleDose(3.0, 7.0, DoseUnit.MILLIGRAMS_A_DAY));
		
		assertFalse(d_multi.match(act1));
		assertFalse(d_multi.match(act2));	
		assertTrue(d_multi.match(act3));
		assertFalse(d_multi.match(act4));	
		assertFalse(d_single.match(act4));	

		TreatmentCategorization fixedCats = ExampleData.buildCategorizationFixedDose(ExampleData.buildDrugCitalopram());
		d_single.getContents().add(fixedCats.getCategories().get(0));
		assertFalse(d_single.match(act4));	
		
		d_multi.getContents().clear();
		TreatmentCategorization candUpto20mg = ExampleData.buildCategorizationUpto20mg(ExampleData.buildDrugCandesartan());
		TreatmentCategorization fluoxto20mg = ExampleData.buildCategorizationUpto20mg(ExampleData.buildDrugViagra());

		d_multi.getContents().add(candUpto20mg.getCategories().get(0));
		d_multi.getContents().add(fluoxto20mg.getCategories().get(0));
		
		assertTrue(d_multi.match(act3));
		fixed1.setQuantity(26.0);
		assertFalse(d_multi.match(act3));
	}
}
