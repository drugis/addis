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

package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class CategoryTest {
	private TreatmentCategorization d_catz1;
	private TreatmentCategorization d_catz2;

	@Before
	public void setUp() {
		d_catz1 = TreatmentCategorization.createDefault("The best", ExampleData.buildDrugEscitalopram(), DoseUnit.createMilliGramsPerDay());
		d_catz2 = TreatmentCategorization.createDefault("The worst", ExampleData.buildDrugEscitalopram(), DoseUnit.createMilliGramsPerDay());
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

		// Trivial categories should not have their owner as a dependency. The
		// owner does *not* need to be explicitly in the domain.
		Category trivial = Category.createTrivial(ExampleData.buildDrugCitalopram());
		assertEquals(trivial.getCategorization().getDependencies(), trivial.getDependencies());
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
	public void testDeepEquals() {
		Category catA = new Category(d_catz1, "A");
		Category catAdup = new Category(d_catz1, "A");
		Category catB = new Category(d_catz1, "B");
		Category catA2 = new Category(d_catz2, "A");

		// Consistency with equals()
		assertTrue(catA.deepEquals(catAdup));
		assertTrue(catAdup.deepEquals(catA));
		assertFalse(catA.deepEquals(catB));
		assertFalse(catA.deepEquals(ExampleData.buildDrugFluoxetine()));
		assertFalse(catA.deepEquals(null));
		assertFalse(catA.deepEquals(catA2));

		d_catz2.setName(d_catz1.getName());
		assertTrue(catA.deepEquals(catA2));
		d_catz2.setDrug(new Drug(d_catz1.getDrug().getName(), "ANOTHERATC"));
		assertFalse(catA.deepEquals(catA2));
	}

	@Test
	public void testDeepEqualsWithTreeModification() {
		Category cat1 = new Category(d_catz1, "A");
		Category cat2 = new Category(d_catz2, "A");

		d_catz2.setName(d_catz1.getName());

		DecisionTree tree1 = d_catz1.getDecisionTree();
		final ChoiceNode choice1 = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, DoseUnit.createMilliGramsPerDay());
		tree1.replaceChild(tree1.findMatchingEdge(tree1.getRoot(), FixedDose.class), choice1);

		tree1.addChild(new RangeEdge(0.0, false, Double.POSITIVE_INFINITY, false), choice1, new LeafNode(cat1));
		d_catz1.splitRange(choice1, 20.0, false);

		assertFalse(cat1.deepEquals(cat2));

		DecisionTree tree2 = d_catz2.getDecisionTree();
		final ChoiceNode choice2 = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, DoseUnit.createMilliGramsPerDay());
		tree2.replaceChild(tree2.findMatchingEdge(tree2.getRoot(), FixedDose.class), choice2);

		tree2.addChild(new RangeEdge(0.0, false, Double.POSITIVE_INFINITY, false), choice2, new LeafNode(cat2));
		d_catz2.splitRange(choice2, 20.0, false);

		assertTrue(cat1.deepEquals(cat2));
	}

	@Test
	public void testTrivialCategorization() {
		assertFalse(new Category(d_catz1).isTrivial());
		TreatmentCategorization trivial = TreatmentCategorization.createTrivial(ExampleData.buildDrugFluoxetine());
		assertTrue(trivial.getCategories().get(0).isTrivial());

		Category cat = Category.createTrivial(ExampleData.buildDrugCandesartan());
		assertTrue(cat.isTrivial());
		assertSame(cat, cat.getCategorization().getCategories().get(0));
		assertEquals(ExampleData.buildDrugCandesartan(), cat.getCategorization().getDrug());
	}

	@Test
	public void testGetLabel() {
		Category trivial = Category.createTrivial(ExampleData.buildDrugFluoxetine());
		assertEquals(trivial.getCategorization().getDrug().getLabel(), trivial.getLabel());

		Category catA = new Category(d_catz1, "A");
		String expected = catA.getCategorization().getDrug().getLabel() + " " + catA.getName();
		assertEquals(expected, catA.getLabel());

	}

	@Test
	public void testMatch() {
		UnknownDose unknownDose = new UnknownDose();
		FixedDose fixedDose20 = new FixedDose(20.0, DoseUnit.createMilliGramsPerDay());
		FixedDose fixedDose25 = new FixedDose(25.0, DoseUnit.createMilliGramsPerDay());
		FlexibleDose flexDose20 = new FlexibleDose(0.0, 20.0, DoseUnit.createMilliGramsPerDay());
		FlexibleDose flexDose25 = new FlexibleDose(0.0, 25.0, DoseUnit.createMilliGramsPerDay());

		Drug fluox = ExampleData.buildDrugFluoxetine();
		Drug sertr = ExampleData.buildDrugSertraline();

		TreatmentCategorization catz1 = ExampleData.buildCategorizationFixedDose(fluox);
		Category fixedCat = catz1.getCategories().get(0);
		assertTrue(fixedCat.match(fixedDose20));
		assertFalse(fixedCat.match(unknownDose));
		assertFalse(fixedCat.match(flexDose20));

		TreatmentCategorization catz2 = ExampleData.buildCategorizationKnownDose(fluox);
		Category knownCat = catz2.getCategories().get(0);
		assertTrue(knownCat.match(fixedDose20));
		assertFalse(knownCat.match(unknownDose));
		assertTrue(knownCat.match(flexDose20));

		TreatmentCategorization catz3 = ExampleData.buildCategorizationUpto20mg(fluox);
		Category upto20 = catz3.getCategories().get(0);
		assertTrue(upto20.match(fixedDose20));
		assertFalse(upto20.match(fixedDose25));
		assertFalse(upto20.match(unknownDose));
		assertTrue(upto20.match(flexDose20));
		assertFalse(upto20.match(flexDose25));

		assertTrue(fixedCat.match(new DrugTreatment(fluox, fixedDose20)));
		assertFalse(fixedCat.match(new DrugTreatment(fluox, flexDose20)));
		assertTrue(upto20.match(new DrugTreatment(fluox, flexDose20)));
		assertFalse(upto20.match(new DrugTreatment(fluox, fixedDose25)));
		assertFalse(fixedCat.match(new DrugTreatment(sertr, fixedDose20)));
	}
}
