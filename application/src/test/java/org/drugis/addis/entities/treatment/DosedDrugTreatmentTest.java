package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class DosedDrugTreatmentTest {
	private DosedDrugTreatment d_treatment;

	@Before
	public void setUp() {
		d_treatment = new DosedDrugTreatment("", ExampleData.buildDrugCandesartan(), ExampleData.MILLIGRAMS_A_DAY);
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

	@Test
	public void testInitialization() {
		final DecisionTree tree = d_treatment.getDecisionTree();
		assertEquals(ExampleData.buildDrugCandesartan(), d_treatment.getDrug());
		assertEquals(ExampleData.MILLIGRAMS_A_DAY, d_treatment.getDoseUnit());
		assertDefaultTree(tree);
	}

	@Test
	public void testAddCategory() {
		final Category category = new Category("foo");
		d_treatment.addCategory(category);
		assertEquals(Arrays.asList(category), d_treatment.getCategories());
	}

	@Test
	public void testCategorization() {
		final DecisionTree tree = d_treatment.getDecisionTree();
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FixedDose.class), new LeafNode(new Category("Fixed Dose")));
		final FixedDose fixedDose = new FixedDose();
		assertEquals("Fixed Dose", d_treatment.getCategory(fixedDose).getName());

		final ChoiceNode maxDoseChoice = new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, ExampleData.MILLIGRAMS_A_DAY);
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FlexibleDose.class), maxDoseChoice);

		tree.addChild(new RangeEdge(0.0, false, 20.0, false), maxDoseChoice, new LeafNode(new Category("Flexible Dose")));
		tree.addChild(new RangeEdge(20.0, true, Double.POSITIVE_INFINITY, false), maxDoseChoice, new LeafNode());

		final FlexibleDose lowDose = new FlexibleDose(new Interval<Double>(0.0, 15.0), ExampleData.MILLIGRAMS_A_DAY);
		final FlexibleDose highDose = new FlexibleDose(new Interval<Double>(20.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);

		assertEquals("Flexible Dose", d_treatment.getCategory(lowDose).getName());
		assertEquals(LeafNode.NAME_EXCLUDE, d_treatment.getCategory(highDose).getName());
	}

	@Test
	public void testSplitRange() {
		final DecisionTree tree = d_treatment.getDecisionTree();
		final ChoiceNode choice = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, ExampleData.MILLIGRAMS_A_DAY);
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FixedDose.class), choice);

		final LeafNode child = new LeafNode(new Category("Low Dose"));
		tree.addChild(new RangeEdge(0.0, false, Double.POSITIVE_INFINITY, false), choice, child);
		d_treatment.splitRange(choice, 20.0, false);

		assertNull(tree.getCategory(new FixedDose(30.0, ExampleData.MILLIGRAMS_A_DAY)).getCategory());
		assertEquals(child, tree.getCategory(new FixedDose(18.0, ExampleData.MILLIGRAMS_A_DAY)));
		assertEquals(child, tree.getCategory(new FixedDose(20.0, ExampleData.MILLIGRAMS_A_DAY)));

		d_treatment.splitRange(choice, 10.0, false);
		assertEquals(child, tree.getCategory(new FixedDose(10.0, ExampleData.MILLIGRAMS_A_DAY)));
		assertNull(tree.getCategory(new FixedDose(18.0, ExampleData.MILLIGRAMS_A_DAY)).getCategory());

		final DecisionTreeNode medium = new LeafNode(new Category("Med Dose"));
		tree.replaceChild(tree.findMatchingEdge(choice, 18.0), medium);
		assertEquals(medium, tree.getCategory(new FixedDose(18.0, ExampleData.MILLIGRAMS_A_DAY)));
	}

	@Test
	public void testMultipleTypes() {
		final LeafNode excludeNode = new LeafNode();
		final LeafNode someCatNode = new LeafNode(new Category("dog"));
		final LeafNode unknownNode = new LeafNode(new Category("unknown"));

		final DecisionTree tree = d_treatment.getDecisionTree();
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FixedDose.class), someCatNode);
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FlexibleDose.class), excludeNode);
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), UnknownDose.class), unknownNode);

		assertEquals(someCatNode, d_treatment.getCategory(new FixedDose()));
		assertEquals(excludeNode, d_treatment.getCategory(new FlexibleDose()));
		assertEquals(unknownNode, d_treatment.getCategory(new UnknownDose()));
	}
}
