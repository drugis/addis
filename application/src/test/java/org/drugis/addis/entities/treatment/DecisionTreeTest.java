package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.FixedDose;
import org.junit.Test;

public class DecisionTreeTest {
	@Test
	public void testTrivialDecision() {
		final DecisionTreeNode root = new LeafNode();
		final DecisionTree tree = new DecisionTree(root);
		assertEquals(root, tree.getCategory("Tomato"));
	}

	@Test
	public void testSimpleDecision() {
		final DecisionTreeNode root = new ChoiceNode(Object.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		tree.addEdge(new TypeEdge(String.class), root, new LeafNode(new Category("str")));
		tree.addEdge(new TypeEdge(Integer.class), root, new LeafNode(new Category("int")));
		assertEquals("str", tree.getCategory("Tomato").getName());
		assertEquals("int", tree.getCategory(42).getName());
	}

	@Test(expected=IllegalStateException.class)
	public void testUnclassifiable() {
		final DecisionTreeNode root = new ChoiceNode(Object.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		tree.addEdge(new TypeEdge(String.class), root, new LeafNode(new Category("str")));
		tree.addEdge(new TypeEdge(Integer.class), root, new LeafNode(new Category("int")));
		tree.getCategory(3.0);
	}

	@Test
	public void testMultiLevelDecision() {
		final DoseUnit unit = DoseUnit.MILLIGRAMS_A_DAY;

		final DecisionTreeNode root = new ChoiceNode(Object.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		tree.addEdge(new TypeEdge(String.class), root, new LeafNode(new Category("str")));
		final ChoiceNode quantityChoice = new ChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		tree.addEdge(new TypeEdge(FixedDose.class), root, quantityChoice);
		tree.addEdge(new RangeEdge(0.0, false, 20.0, false), quantityChoice, new LeafNode(new Category("low")));
		tree.addEdge(new RangeEdge(20.0, true, Double.POSITIVE_INFINITY, true), quantityChoice, new LeafNode(new Category("high")));
		assertEquals("str", tree.getCategory("Tomato").getName());
		assertEquals("low", tree.getCategory(new FixedDose(20.0, unit)).getName());
		assertEquals("high", tree.getCategory(new FixedDose(42.0, unit)).getName());
	}

	@Test
	public void testFindMatchingRangeEdge() {
		final DecisionTreeNode root = new ChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY);
		final DecisionTree tree = new DecisionTree(root);
		final RangeEdge lowEdge = new RangeEdge(0.0, false, 20.0, false);
		final RangeEdge medEdge = new RangeEdge(20.0, true, 80.0, false);
		final RangeEdge higEdge = new RangeEdge(80.0, true, Double.POSITIVE_INFINITY, false);
		tree.addEdge(lowEdge, root, new LeafNode(new Category("low")));
		tree.addEdge(medEdge, root, new LeafNode(new Category("medium")));
		tree.addEdge(higEdge, root, new LeafNode(new Category("high")));

		assertEquals(lowEdge, tree.findMatchingEdge(root, 5.0));
		assertEquals(medEdge, tree.findMatchingEdge(root, 25.0));
		assertEquals(higEdge, tree.findMatchingEdge(root, 85.0));
	}

	@Test
	public void testSetChild() {
		final DecisionTreeNode root = new ChoiceNode(Object.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		final TypeEdge edge = new TypeEdge(Integer.class);
		tree.addEdge(new TypeEdge(String.class), root, new LeafNode(new Category("str")));
		tree.addEdge(edge, root, new LeafNode(new Category("double")));
		assertEquals("double", tree.getCategory(42).getName());

		tree.replaceChild(edge, new LeafNode(new Category("int")));
		assertEquals("int", tree.getCategory(42).getName());
	}
}
