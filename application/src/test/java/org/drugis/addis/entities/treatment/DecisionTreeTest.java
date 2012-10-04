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

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.FixedDose;
import org.junit.Test;

public class DecisionTreeTest {

	private final TreatmentCategorization d_tc = TreatmentCategorization.createDefault();

	@Test
	public void testTrivialDecision() {
		final DecisionTreeNode root = new LeafNode();
		final DecisionTree tree = new DecisionTree(root);
		assertEquals(root, tree.decide("Tomato"));
	}

	@Test
	public void testSimpleDecision() {
		final DecisionTreeNode root = new ChoiceNode(Object.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		tree.addEdge(new TypeEdge(String.class), root, new LeafNode(new Category(d_tc, "str")));
		tree.addEdge(new TypeEdge(Integer.class), root, new LeafNode(new Category(d_tc, "int")));
		assertEquals("str", tree.decide("Tomato").getName());
		assertEquals("int", tree.decide(42).getName());
	}

	@Test(expected=IllegalStateException.class)
	public void testUnclassifiable() {
		final DecisionTreeNode root = new ChoiceNode(Object.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		tree.addEdge(new TypeEdge(String.class), root, new LeafNode(new Category(d_tc, "str")));
		tree.addEdge(new TypeEdge(Integer.class), root, new LeafNode(new Category(d_tc, "int")));
		tree.decide(3.0);
	}

	@Test
	public void testMultiLevelDecision() {
		final DoseUnit unit = DoseUnit.createMilliGramsPerDay();

		final DecisionTreeNode root = new ChoiceNode(Object.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		tree.addEdge(new TypeEdge(String.class), root, new LeafNode(new Category(d_tc, "str")));
		final ChoiceNode quantityChoice = new ChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		tree.addEdge(new TypeEdge(FixedDose.class), root, quantityChoice);
		tree.addEdge(new RangeEdge(0.0, false, 20.0, false), quantityChoice, new LeafNode(new Category(d_tc, "low")));
		tree.addEdge(new RangeEdge(20.0, true, Double.POSITIVE_INFINITY, true), quantityChoice, new LeafNode(new Category(d_tc, "high")));
		assertEquals("str", tree.decide("Tomato").getName());
		assertEquals("low", tree.decide(new FixedDose(20.0, unit)).getName());
		assertEquals("high", tree.decide(new FixedDose(42.0, unit)).getName());
	}

	@Test
	public void testFindMatchingRangeEdge() {
		final DecisionTreeNode root = new ChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		final DecisionTree tree = new DecisionTree(root);
		final RangeEdge lowEdge = new RangeEdge(0.0, false, 20.0, false);
		final RangeEdge medEdge = new RangeEdge(20.0, true, 80.0, false);
		final RangeEdge higEdge = new RangeEdge(80.0, true, Double.POSITIVE_INFINITY, false);
		tree.addEdge(lowEdge, root, new LeafNode(new Category(d_tc, "low")));
		tree.addEdge(medEdge, root, new LeafNode(new Category(d_tc, "medium")));
		tree.addEdge(higEdge, root, new LeafNode(new Category(d_tc, "high")));

		assertEquals(lowEdge, tree.findMatchingEdge(root, 5.0));
		assertEquals(medEdge, tree.findMatchingEdge(root, 25.0));
		assertEquals(higEdge, tree.findMatchingEdge(root, 85.0));
	}

	@Test
	public void testSetChild() {
		final DecisionTreeNode root = new ChoiceNode(Object.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		final TypeEdge edge = new TypeEdge(Integer.class);
		tree.addEdge(new TypeEdge(String.class), root, new LeafNode(new Category(d_tc, "str")));
		tree.addEdge(edge, root, new LeafNode(new Category(d_tc, "double")));
		assertEquals("double", tree.decide(42).getName());

		tree.replaceChild(edge, new LeafNode(new Category(d_tc, "int")));
		assertEquals("int", tree.decide(42).getName());
	}

	@Test
	public void testToString() {
		TreatmentCategorization catz1 = ExampleData.buildCategorizationFixedDose(ExampleData.buildDrugFluoxetine());
		assertEquals("Class Fixed", catz1.getDecisionTree().getLabel(catz1.getCategories().get(0)));

		TreatmentCategorization catz2 = ExampleData.buildCategorizationUpto20mg(ExampleData.buildDrugFluoxetine());
		assertEquals("(Class Fixed AND Quantity 0.00 ≤ x ≤ 20.00) OR (Class Flexible AND Max Dose 0.00 ≤ x ≤ 20.00)",
				catz2.getDecisionTree().getLabel(catz2.getCategories().get(0)));

	}


}
