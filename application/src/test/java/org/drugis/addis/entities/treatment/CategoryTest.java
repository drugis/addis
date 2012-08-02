package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class CategoryTest {
	private TreatmentCategorization d_catz1;
	private TreatmentCategorization d_catz2;
	
	@Before
	public void setUp() {
		d_catz1 = TreatmentCategorization.createDefault("The best", ExampleData.buildDrugEscitalopram(), DoseUnit.MILLIGRAMS_A_DAY);
		d_catz2 = TreatmentCategorization.createDefault("The worst", ExampleData.buildDrugEscitalopram(), DoseUnit.MILLIGRAMS_A_DAY);
	}
	
	@Test
	public void testGetSetName() {
		JUnitUtil.testSetter(new Category(d_catz1), TypeWithName.PROPERTY_NAME, "", "XXX");
	}
	
	@Test
	public void testGetCategorization() {
		assertEquals(d_catz1, new Category(d_catz1, "A").getCategorization());
	}
	
	@Test
	public void testGetDependencies() {
		Category cat = new Category(d_catz1, "A");
		Set<Entity> expected = new HashSet<Entity>(d_catz1.getDependencies());
		expected.add(d_catz1);
		assertEquals(expected, cat.getDependencies());
	}
	
	@Test
	public void testEqualsHashCodeCompare() {
		Category catA = new Category(d_catz1, "A");
		Category catAdup = new Category(d_catz1, "A");
		Category catB = new Category(d_catz1, "B");
		Category catA2 = new Category(d_catz2, "A");
		
		// With equal categorizations
		assertTrue(catA.equals(catAdup));
		assertTrue(catAdup.equals(catA));
		assertEquals(catA.hashCode(), catAdup.hashCode());
		assertFalse(catA.equals(catB));
		assertFalse(catA.equals(ExampleData.buildDrugFluoxetine()));
		assertFalse(catA.equals(null));
		assertEquals(0, catA.compareTo(catAdup));
		assertTrue(catA.compareTo(catB) < 0);
		assertTrue(catB.compareTo(catA) > 0);
		
		// With different categorizations
		assertFalse(catA.equals(catA2));
		assertTrue(catA.compareTo(catA2) < 0);
		assertTrue(catA2.compareTo(catA) > 0);
		assertTrue(catB.compareTo(catA2) < 0);
	}
	
	@Test
	public void testTrivialCategorization() {
		assertFalse(new Category(d_catz1).isTrivial());
		TreatmentCategorization trivial = TreatmentCategorization.createTrivial(ExampleData.buildDrugFluoxetine());
		assertTrue(trivial.getCategories().get(0).isTrivial());
	}
}
