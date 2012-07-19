package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DosedDrugTreatmentTest {
	private DosedDrugTreatment d_treatment;

	@Before
	public void setUp() {
		d_treatment = new DosedDrugTreatment("", ExampleData.buildDrugCandesartan(), false);
	}
	
	@Test
	public void testInitialization() {
		Drug drugCandesartan = ExampleData.buildDrugCandesartan();
		DoseDecisionTree tree = d_treatment.getDecisionTree();
		
		assertEquals(drugCandesartan, d_treatment.getDrug());
		assertTrue(tree.getRoot() instanceof EmptyNode);
	}
	
	@Test 
	public void testAddCategory() {
		CategoryNode categoryNode = new CategoryNode("foo");
		d_treatment.addCategory(categoryNode);
		assertEquals(Arrays.asList(categoryNode), d_treatment.getCategories());
	}
	
	@Test
	public void testCategorization() {
		DoseDecisionTree tree = d_treatment.getDecisionTree();
		TypeNode fixedDoseNode = new TypeNode(FixedDose.class);
		tree.setChild(tree.getRoot(), fixedDoseNode);
		tree.setChild(fixedDoseNode, new CategoryNode("Fixed Dose"));
		FixedDose fixedDose = new FixedDose();
		assertEquals("Fixed Dose", d_treatment.getCategory(fixedDose).getName());
		
		TypeNode flexibleDoseNode = new TypeNode(FlexibleDose.class);
		tree.setChild(tree.getRoot(), flexibleDoseNode);
		RangeNode rangeNode = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE);
		tree.setChild(flexibleDoseNode, rangeNode);
		tree.setChild(rangeNode, new CategoryNode("Flexible Dose"));
		
		List<RangeNode> ranges = tree.splitChildRange(flexibleDoseNode, 20.0, true);
		RangeNode left = ranges.get(0);
		RangeNode right = ranges.get(1);

		tree.setChild(left, new CategoryNode("Flexible Dose"));
		tree.setChild(right, new ExcludeNode());
		
		FlexibleDose lowDose = new FlexibleDose(new Interval<Double>(0.0, 15.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose highDose = new FlexibleDose(new Interval<Double>(20.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);

		assertEquals("Flexible Dose", d_treatment.getCategory(lowDose).getName());
		assertEquals(ExcludeNode.NAME, d_treatment.getCategory(highDose).getName());

		FlexibleDose superHighDose = new FlexibleDose(new Interval<Double>(20.0, 700.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose someDose = new FlexibleDose(new Interval<Double>(20.0, 300.0), ExampleData.MILLIGRAMS_A_DAY);

		List<RangeNode> ranges2 = tree.splitChildRange(flexibleDoseNode, 500.0, true);
		RangeNode highRight = ranges2.get(1);
		tree.setChild(highRight, new CategoryNode("Super high dose"));
		assertEquals("Super high dose", d_treatment.getCategory(superHighDose).getName());
		JUnitUtil.assertNotEquals("Super high dose", d_treatment.getCategory(someDose).getName());

	}

	@Test
	public void testMultipleTypes() {
		ExcludeNode excludeNode = new ExcludeNode();

		CategoryNode someCatNode = new CategoryNode("dog");
		CategoryNode unknownNode = new CategoryNode("unknown");
		TypeNode fixedNode = new TypeNode(FixedDose.class);
		TypeNode flexibleNode = new TypeNode(FlexibleDose.class);
		TypeNode unknownDose = new TypeNode(UnknownDose.class);

		DoseDecisionTree tree = d_treatment.getDecisionTree();
		tree.setChild(tree.getRoot(), fixedNode);
		tree.setChild(tree.getRoot(), flexibleNode);
		tree.setChild(tree.getRoot(), unknownDose);

		tree.setChild(fixedNode, someCatNode);
		tree.setChild(flexibleNode, excludeNode);
		tree.setChild(unknownDose, unknownNode);

		assertEquals(someCatNode, d_treatment.getCategory(new FixedDose()));
		assertEquals(excludeNode, d_treatment.getCategory(new FlexibleDose()));
		assertEquals(unknownNode, d_treatment.getCategory(new UnknownDose()));
	}
	
	@Test
	public void testSplitCategory() {
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false);
		DoseDecisionTree tree = d_treatment.getDecisionTree();
		DecisionTreeNode root = tree.getRoot();
		tree.setChild(root, node1);
		
		List<RangeNode> ranges = tree.splitChildRange(root, 1500, true);
		// (1000..1500), [1500..2000]

		assertEquals(2, tree.getChildCount(root));
		RangeNode leftSide = ranges.get(0);
		RangeNode rightSide = ranges.get(1);
		assertTrue(tree.getChildren(root).contains(rightSide ));
		
		assertEquals(1000, leftSide.getRangeLowerBound(), RangeNode.EPSILON);
		assertEquals(1500, leftSide.getRangeUpperBound(), RangeNode.EPSILON);		
		assertEquals(1500, rightSide.getRangeLowerBound(), RangeNode.EPSILON);
		assertEquals(2000, rightSide.getRangeUpperBound(), RangeNode.EPSILON);
		
		assertTrue(leftSide.isRangeLowerBoundOpen());
		assertFalse(rightSide.isRangeLowerBoundOpen());
		
		assertTrue(leftSide.isRangeUpperBoundOpen());
		assertFalse(rightSide.isRangeUpperBoundOpen());

		// (1000..1250], (1250..1500), [1500..2000]
		List<RangeNode> ranges2 = tree.splitChildRange(root, 1250, false);
		CategoryNode midCatNode = new CategoryNode("midrange");
		RangeNode lowRange = ranges2.get(0);
		RangeNode midRange = ranges2.get(1);
		tree.setChild(midRange, midCatNode);
		
		
		assertEquals(1000, lowRange.getRangeLowerBound(), RangeNode.EPSILON);
		assertEquals(1250, lowRange.getRangeUpperBound(), RangeNode.EPSILON);		
		assertEquals(1250, midRange.getRangeLowerBound(), RangeNode.EPSILON);
		assertEquals(1500, midRange.getRangeUpperBound(), RangeNode.EPSILON);
		assertEquals(1500, rightSide.getRangeLowerBound(), RangeNode.EPSILON);
		assertEquals(2000, rightSide.getRangeUpperBound(), RangeNode.EPSILON);
		
		assertTrue(lowRange.isRangeLowerBoundOpen());
		assertTrue(midRange.isRangeLowerBoundOpen());
		assertFalse(rightSide.isRangeLowerBoundOpen());
		
		assertFalse(lowRange.isRangeUpperBoundOpen());
		assertTrue(midRange.isRangeUpperBoundOpen());
		assertFalse(rightSide.isRangeUpperBoundOpen());
	}


	@Test
	public void testDecideSimple() { 
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 0, true, 40, false);
		FlexibleDose flexDose1 = new FlexibleDose(new Interval<Double>(20.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double>(200.0, 300.0), ExampleData.MILLIGRAMS_A_DAY);

		assertTrue(node1.decide(flexDose1));
		assertFalse(node1.decide(flexDose2));		
	}

}
