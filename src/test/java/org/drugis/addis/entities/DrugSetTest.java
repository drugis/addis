package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.drugis.common.JUnitUtil.assertNotEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

public class DrugSetTest {
	private DrugSet d_empty;
	private DrugSet d_single;
	private DrugSet d_multi;
	private List<Drug> d_multiList;
	private Drug d_singleDrug;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		d_empty = new DrugSet();
		d_singleDrug = ExampleData.buildDrugEscitalopram();
		d_single = new DrugSet(d_singleDrug);
		d_multiList = Arrays.asList(new Drug[] {
				ExampleData.buildDrugCandesartan(),
				ExampleData.buildDrugViagra(),
				ExampleData.buildDrugViagra()
			});
		d_multi = new DrugSet(d_multiList);
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetName() {
		assertEquals("", d_empty.getDescription());
		assertEquals(d_singleDrug.getName(), d_single.getDescription());
		assertEquals("Candesartan + Viagra", d_multi.getDescription());
		
		// test alphabetic
		List<Drug> more = Arrays.asList(new Drug[] {
			ExampleData.buildDrugSertraline(),			
			ExampleData.buildDrugParoxetine(),
			ExampleData.buildDrugFluoxetine(),
			ExampleData.buildDrugViagra()
		});
		assertEquals("Fluoxetine + Paroxetine + Sertraline + Viagra", new DrugSet(more).getDescription());
	}
	
	@Test
	public void testEquals() {
		assertEquals(new DrugSet(), d_empty);
		assertEquals(new DrugSet().hashCode(), d_empty.hashCode());
		assertEquals(new DrugSet(d_singleDrug), d_single);
		assertEquals(new DrugSet(d_singleDrug).hashCode(), d_single.hashCode());
		assertEquals(new DrugSet(d_multiList), d_multi);
		assertEquals(new DrugSet(d_multiList).hashCode(), d_multi.hashCode());
		
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
		assertTrue(d_single.deepEquals(new DrugSet(d_singleDrug)));
		assertTrue(d_multi.deepEquals(new DrugSet(d_multiList)));

		// nearly identical contents
		Drug drug = new Drug(d_singleDrug.getName(), "ATCFORYOUMYFRIEND");
		assertFalse(d_single.deepEquals(new DrugSet(drug)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCompareTo() {
		assertEquals(0, d_empty.compareTo(d_empty));
		assertEquals(0, d_empty.compareTo(new DrugSet()));
		assertEquals(0, d_single.compareTo(new DrugSet(d_singleDrug)));
		assertEquals(0, d_multi.compareTo(new DrugSet(d_multiList)));
		
		assertTrue(d_empty.compareTo(d_single) < 0); // {} < {Escitalopram}
		assertTrue(d_single.compareTo(new DrugSet(ExampleData.buildDrugCitalopram())) > 0); // {Escitalopram} > {Citalopram} 
		assertEquals(d_empty.compareTo(d_single), -d_single.compareTo(d_empty));
		DrugSet two1 = new DrugSet(Arrays.asList(new Drug[] {
				d_singleDrug, ExampleData.buildDrugCitalopram()
		}));
		assertTrue(d_single.compareTo(two1) > 0); // {Escitalopram} > {Citalopram, Escitalopram}
		DrugSet two2 = new DrugSet(Arrays.asList(new Drug[] {
				d_singleDrug, ExampleData.buildDrugFluoxetine()
		}));
		assertTrue(d_single.compareTo(two2) < 0); // {Escitalopram} < {Escitalopram, Fluoxetine}
	}
}
