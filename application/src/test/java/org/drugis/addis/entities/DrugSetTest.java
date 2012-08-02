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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.drugis.common.JUnitUtil.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.junit.Before;
import org.junit.Test;

public class DrugSetTest {
	private DrugSet d_empty;
	private DrugSet d_single;
	private DrugSet d_multi;
	private List<Drug> d_multiList;
	private Drug d_singleDrug;
	
	@Before
	public void setUp() {
		d_empty = new DrugSet();
		d_singleDrug = ExampleData.buildDrugEscitalopram();
		d_single = DrugSet.createTrivial(d_singleDrug);
		d_multiList = Arrays.asList(
				ExampleData.buildDrugCandesartan(),
				ExampleData.buildDrugViagra(),
				ExampleData.buildDrugViagra());
		d_multi = DrugSet.createTrivial(d_multiList);
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
		assertEquals(new HashSet<Drug>(d_multiList), d_multi.getContents());
	}
	
	@Test
	public void testGetName() {
		assertEquals("", d_empty.getLabel());
		assertEquals(d_singleDrug.getName(), d_single.getLabel());
		assertEquals("Candesartan + Viagra", d_multi.getLabel());
		
		// test alphabetic
		List<Drug> more = Arrays.asList(new Drug[] {
			ExampleData.buildDrugSertraline(),			
			ExampleData.buildDrugParoxetine(),
			ExampleData.buildDrugFluoxetine(),
			ExampleData.buildDrugViagra()
		});
		assertEquals("Fluoxetine + Paroxetine + Sertraline + Viagra", DrugSet.createTrivial(more).getLabel());
	}
	
	@Test
	public void testEquals() {
		assertEquals(new DrugSet(), d_empty);
		assertEquals(new DrugSet().hashCode(), d_empty.hashCode());
		assertEquals(DrugSet.createTrivial(d_singleDrug), d_single);
		assertEquals(DrugSet.createTrivial(d_singleDrug).hashCode(), d_single.hashCode());
		assertEquals(DrugSet.createTrivial(d_multiList), d_multi);
		assertEquals(DrugSet.createTrivial(d_multiList).hashCode(), d_multi.hashCode());
		
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
		assertTrue(d_empty.deepEquals(new DrugSet()));
		assertTrue(d_single.deepEquals(DrugSet.createTrivial(d_singleDrug)));
		assertTrue(d_multi.deepEquals(DrugSet.createTrivial(d_multiList)));

		// nearly identical contents
		Drug drug = new Drug(d_singleDrug.getName(), "ATCFORYOUMYFRIEND");
		assertFalse(d_single.deepEquals(DrugSet.createTrivial(drug)));
	}
	
	@Test
	public void testCompareTo() {
		assertEquals(0, d_empty.compareTo(d_empty));
		assertEquals(0, d_empty.compareTo(new DrugSet()));
		assertEquals(0, d_single.compareTo(DrugSet.createTrivial(d_singleDrug)));
		assertEquals(0, d_multi.compareTo(DrugSet.createTrivial(d_multiList)));
		
		assertTrue(d_empty.compareTo(d_single) < 0); // {} < {Escitalopram}
		assertTrue(d_single.compareTo(DrugSet.createTrivial(ExampleData.buildDrugCitalopram())) > 0); // {Escitalopram} > {Citalopram} 
		assertEquals(d_empty.compareTo(d_single), -d_single.compareTo(d_empty));
		DrugSet two1 = DrugSet.createTrivial(Arrays.asList(new Drug[] {
				d_singleDrug, ExampleData.buildDrugCitalopram()
		}));
		assertTrue(d_single.compareTo(two1) > 0); // {Escitalopram} > {Citalopram, Escitalopram}
		DrugSet two2 = DrugSet.createTrivial(Arrays.asList(new Drug[] {
				d_singleDrug, ExampleData.buildDrugFluoxetine()
		}));
		assertTrue(d_single.compareTo(two2) < 0); // {Escitalopram} < {Escitalopram, Fluoxetine}
	}
}
