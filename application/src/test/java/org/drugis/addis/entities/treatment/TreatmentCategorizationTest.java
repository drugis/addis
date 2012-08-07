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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class TreatmentCategorizationTest {
	private TreatmentCategorization d_treatment;

	@Before
	public void setUp() {
		d_treatment = TreatmentCategorization.createDefault("", ExampleData.buildDrugCandesartan(), DoseUnit.MILLIGRAMS_A_DAY);
	}

	@Test
	public void testInitialization() {
		final DecisionTree tree = d_treatment.getDecisionTree();
		assertEquals(ExampleData.buildDrugCandesartan(), d_treatment.getDrug());
		assertEquals(DoseUnit.MILLIGRAMS_A_DAY, d_treatment.getDoseUnit());
		assertDefaultTree(tree);
		assertFalse(d_treatment.isTrivial());
	}

	@Test
	public void testAddCategory() {
		final Category category = new Category(d_treatment, "foo");
		d_treatment.addCategory(category);
		assertEquals(Arrays.asList(category), d_treatment.getCategories());
	}
	
	@Test
	public void testGetDependencies() {
		final Category category = new Category(d_treatment, "foo");
		d_treatment.addCategory(category);
		Set<Entity> expected = new HashSet<Entity>();
		expected.add(d_treatment.getDrug());
		expected.add(d_treatment.getDoseUnit().getUnit());
		assertEquals(expected, d_treatment.getDependencies());
	}

	@Test
	public void testCategorization() {
		final DecisionTree tree = d_treatment.getDecisionTree();
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FixedDose.class), new LeafNode(new Category(d_treatment, "Fixed Dose")));
		final FixedDose fixedDose = new FixedDose();
		assertEquals("Fixed Dose", d_treatment.getCategory(fixedDose).getName());

		final ChoiceNode maxDoseChoice = new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, DoseUnit.MILLIGRAMS_A_DAY);
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FlexibleDose.class), maxDoseChoice);

		tree.addChild(new RangeEdge(0.0, false, 20.0, false), maxDoseChoice, new LeafNode(new Category(d_treatment, "Flexible Dose")));
		tree.addChild(new RangeEdge(20.0, true, Double.POSITIVE_INFINITY, false), maxDoseChoice, new LeafNode());

		final FlexibleDose lowDose = new FlexibleDose(new Interval<Double>(0.0, 15.0), DoseUnit.MILLIGRAMS_A_DAY);
		final FlexibleDose highDose = new FlexibleDose(new Interval<Double>(20.0, 30.0), DoseUnit.MILLIGRAMS_A_DAY);

		assertEquals("Flexible Dose", d_treatment.getCategory(lowDose).getName());
		assertNull(d_treatment.getCategory(highDose));
	}

	@Test
	public void testSplitRange() {
		final DecisionTree tree = d_treatment.getDecisionTree();
		final ChoiceNode choice = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, DoseUnit.MILLIGRAMS_A_DAY);
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FixedDose.class), choice);

		final LeafNode child = new LeafNode(new Category(d_treatment, "Low Dose"));
		tree.addChild(new RangeEdge(0.0, false, Double.POSITIVE_INFINITY, false), choice, child);
		d_treatment.splitRange(choice, 20.0, false);

		assertNull(tree.decide(new FixedDose(30.0, DoseUnit.MILLIGRAMS_A_DAY)).getCategory());
		assertEquals(child, tree.decide(new FixedDose(18.0, DoseUnit.MILLIGRAMS_A_DAY)));
		assertEquals(child, tree.decide(new FixedDose(20.0, DoseUnit.MILLIGRAMS_A_DAY)));

		d_treatment.splitRange(choice, 10.0, false);
		assertEquals(child, tree.decide(new FixedDose(10.0, DoseUnit.MILLIGRAMS_A_DAY)));
		assertNull(tree.decide(new FixedDose(18.0, DoseUnit.MILLIGRAMS_A_DAY)).getCategory());

		final DecisionTreeNode medium = new LeafNode(new Category(d_treatment, "Med Dose"));
		tree.replaceChild(tree.findMatchingEdge(choice, 18.0), medium);
		assertEquals(medium, tree.decide(new FixedDose(18.0, DoseUnit.MILLIGRAMS_A_DAY)));
	}

	@Test
	public void testMultipleTypes() {
		final LeafNode excludeNode = new LeafNode();
		final LeafNode someCatNode = new LeafNode(new Category(d_treatment, "dog"));
		final LeafNode unknownNode = new LeafNode(new Category(d_treatment, "unknown"));

		final DecisionTree tree = d_treatment.getDecisionTree();
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FixedDose.class), someCatNode);
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FlexibleDose.class), excludeNode);
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), UnknownDose.class), unknownNode);

		assertEquals(someCatNode.getCategory(), d_treatment.getCategory(new FixedDose()));
		assertEquals(excludeNode.getCategory(), d_treatment.getCategory(new FlexibleDose()));
		assertEquals(unknownNode.getCategory(), d_treatment.getCategory(new UnknownDose()));
	}
	
	@Test
	public void testTrivialCategorization() {
		TreatmentCategorization trivial = TreatmentCategorization.createTrivial(ExampleData.buildDrugSertraline());
		assertTrue(trivial.getRootNode() instanceof LeafNode);
		LeafNode root = (LeafNode) trivial.getRootNode();
		assertNotNull(root.getCategory());
		assertNotNull(trivial.getCategory(new UnknownDose()));
		assertNotNull(trivial.getCategory(new FixedDose(20.0, DoseUnit.MILLIGRAMS_A_DAY)));
		assertNotNull(trivial.getCategory(new FlexibleDose(new Interval<Double>(10.0, 1234.0), DoseUnit.MILLIGRAMS_A_DAY)));
		assertTrue(trivial.isTrivial());
		
		assertEquals(root.getCategory(), trivial.getCategories().get(0));
	}
	
	@Test
	public void testEqualsHashCodeCompareTo() {
		TreatmentCategorization catA = TreatmentCategorization.createTrivial(ExampleData.buildDrugFluoxetine());
		TreatmentCategorization catAdup = TreatmentCategorization.createTrivial(ExampleData.buildDrugFluoxetine());
		TreatmentCategorization catB = TreatmentCategorization.createTrivial(ExampleData.buildDrugFluoxetine());
		catB.setName("B");
		TreatmentCategorization catA2 = TreatmentCategorization.createTrivial(ExampleData.buildDrugParoxetine());
		
		// With equal drugs
		assertTrue(catA.equals(catAdup));
		assertTrue(catAdup.equals(catA));
		assertEquals(catA.hashCode(), catAdup.hashCode());
		assertFalse(catA.equals(catB));
		assertFalse(catA.equals(ExampleData.buildDrugFluoxetine()));
		assertFalse(catA.equals(null));
		assertEquals(0, catA.compareTo(catAdup));
		assertTrue(catA.compareTo(catB) < 0);
		assertTrue(catB.compareTo(catA) > 0);
		
		// With different drugs 
		assertFalse(catA.equals(catA2));
		assertTrue(catA.compareTo(catA2) < 0);
		assertTrue(catA2.compareTo(catA) > 0);
		assertTrue(catB.compareTo(catA2) < 0);
	}
	
	@Test
	public void testDeepEquals() {
		TreatmentCategorization catz1 = TreatmentCategorization.createTrivial(ExampleData.buildDrugFluoxetine());
		TreatmentCategorization catz2 = TreatmentCategorization.createTrivial(ExampleData.buildDrugFluoxetine());
		assertTrue(catz1.deepEquals(catz2));
		
		catz1.getCategories().get(0).setName("Include");
		assertFalse(catz1.deepEquals(catz2));
		catz2.getCategories().get(0).setName("Include");
		assertTrue(catz1.deepEquals(catz2));
		
		catz1.setName("CATZ");
		assertFalse(catz1.deepEquals(catz2));
		catz2.setName("CATZ");
		assertTrue(catz1.deepEquals(catz2));
		
		catz1.setDrug(new Drug(catz1.getDrug().getName(), "FAKEATCCODE"));
		assertFalse(catz1.deepEquals(catz2));
	}

	public static void assertDefaultTree(final DecisionTree tree) {
		assertTrue(tree.getRoot() instanceof ChoiceNode);
		assertEquals(AbstractDose.class, ((ChoiceNode)tree.getRoot()).getBeanClass());
		assertEquals("class", ((ChoiceNode)tree.getRoot()).getPropertyName());

		final Collection<DecisionTreeNode> children = tree.getChildren(tree.getRoot());
		assertEquals(3, children.size());
		for (final DecisionTreeNode child : children) {
			assertTrue(child instanceof LeafNode);
			assertNull(((LeafNode)child).getCategory()); // EXCLUDE
		}

		final Collection<DecisionTreeEdge> edges = tree.getOutEdges(tree.getRoot());
		final Class<?> type = FixedDose.class;
		final DecisionTreeEdge find = findTypeEdge(edges, type);
		assertNotNull(find);
	}

	private static DecisionTreeEdge findTypeEdge(final Collection<DecisionTreeEdge> edges, final Class<?> type) {
		final DecisionTreeEdge find = CollectionUtils.find(edges, new Predicate<DecisionTreeEdge>() {
			@Override
			public boolean evaluate(final DecisionTreeEdge object) {
				return (object instanceof TypeEdge) && object.decide(type);
			}
		});
		return find;
	}
}
