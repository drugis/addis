package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DosedDrugTreatmentTest {
	private static final DoseUnit MG_DAY = ExampleData.MILLIGRAMS_A_DAY;
	
	private DosedDrugTreatment d_treatment;

	@Before
	public void setUp() {
		d_treatment = new DosedDrugTreatment("", ExampleData.buildDrugCandesartan());
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
		tree.addChild(tree.getRoot(), fixedDoseNode);
		tree.addChild(fixedDoseNode, new CategoryNode("Fixed Dose"));
		FixedDose fixedDose = new FixedDose();
		assertEquals("Fixed Dose", d_treatment.getNode(fixedDose).getName());
		
		TypeNode flexibleDoseNode = new TypeNode(FlexibleDose.class);
		tree.addChild(tree.getRoot(), flexibleDoseNode);
		RangeNode rangeNode = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE);
		tree.addChild(flexibleDoseNode, rangeNode);
		tree.addChild(rangeNode, new CategoryNode("Flexible Dose"));
		
		RangeNode right = tree.addCutOff(flexibleDoseNode, 20.0, true);
		tree.addChild(right, new ExcludeNode());
		
		FlexibleDose lowDose = new FlexibleDose(new Interval<Double>(0.0, 15.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose highDose = new FlexibleDose(new Interval<Double>(20.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);

		assertEquals("Flexible Dose", d_treatment.getNode(lowDose).getName());
		assertEquals(ExcludeNode.NAME, d_treatment.getNode(highDose).getName());

		FlexibleDose superHighDose = new FlexibleDose(new Interval<Double>(20.0, 700.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose someDose = new FlexibleDose(new Interval<Double>(20.0, 300.0), ExampleData.MILLIGRAMS_A_DAY);

		RangeNode highRight = tree.addCutOff(flexibleDoseNode, 500.0, true);
		tree.addChild(highRight, new CategoryNode("Super high dose"));
		assertEquals("Super high dose", d_treatment.getNode(superHighDose).getName());
		JUnitUtil.assertNotEquals("Super high dose", d_treatment.getNode(someDose).getName());

	}

	
	

//	@Test
//	public void testMultipleTypes() {
//		CategoryNode someCatNode = new CategoryNode("dog");
//		CategoryNode unknownNode = new CategoryNode("unknown");
//		TypeNode typeNode = new TypeNode(FixedDose.class, d_child);
//		typeNode.setType(FlexibleDose.class, someCatNode);
//		typeNode.setType(UnknownDose.class, unknownNode);
//		
//		assertEquals(d_child, typeNode.decide(new FixedDose()));
//		assertEquals(someCatNode, typeNode.decide(new FlexibleDose()));
//		assertEquals(unknownNode, typeNode.decide(new UnknownDose()));
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testIllegalArgument() {
//		TypeNode typeNode = new TypeNode(FlexibleDose.class, d_child);
//		typeNode.decide(new UnknownDose());
//	}
	
	
	
//	@Test
//	public void testSplitCategory() {
//		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false, d_excludeNode);
//		int childIdx = node1.addCutOff(1500, true);
//		
//		assertEquals(node1.getChildCount(), 2);
//		assertEquals(d_excludeNode, node1.getChildNode(childIdx));
//		
//		assertEquals(1000, node1.getRangeLowerBound(0), RangeNode.EPSILON);
//		assertEquals(1500, node1.getRangeUpperBound(0), RangeNode.EPSILON);		
//		assertEquals(1500, node1.getRangeLowerBound(1), RangeNode.EPSILON);
//		assertEquals(2000, node1.getRangeUpperBound(1), RangeNode.EPSILON);
//		
//		assertTrue(node1.isRangeLowerBoundOpen(0));
//		assertFalse(node1.isRangeLowerBoundOpen(1));
//		
//		assertTrue(node1.isRangeUpperBoundOpen(0));
//		assertFalse(node1.isRangeUpperBoundOpen(1));
//
//	}

	
//	@Test
//	public void testSplitTwice() { 
//		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false, d_excludeNode);
//		int childIdx = node1.addCutOff(1500, false);
//		// (1000..1500], (1500..2000]
//		
//		assertEquals(node1.getChildCount(), 2);
//		assertEquals(d_excludeNode, node1.getChildNode(childIdx));
//		
//		assertEquals(1000, node1.getRangeLowerBound(0), RangeNode.EPSILON);
//		assertEquals(1500, node1.getRangeUpperBound(0), RangeNode.EPSILON);		
//		assertEquals(1500, node1.getRangeLowerBound(1), RangeNode.EPSILON);
//		assertEquals(2000, node1.getRangeUpperBound(1), RangeNode.EPSILON);
//		
//		assertTrue(node1.isRangeLowerBoundOpen(0));
//		assertTrue(node1.isRangeLowerBoundOpen(1));
//
//		assertFalse(node1.isRangeUpperBoundOpen(0));
//		assertFalse(node1.isRangeUpperBoundOpen(1));
//
//		CategoryNode midCatNode = new CategoryNode("midrange");
//		int childIdx2 = node1.addCutOff(1250, false, midCatNode);
//		
//		// (1000..1250], (1250..1500], (1500..2000]
//
//		assertEquals(node1.getChildCount(), 3);
//		assertEquals(midCatNode, node1.getChildNode(childIdx2));
//		
//		assertEquals(1000, node1.getRangeLowerBound(0), RangeNode.EPSILON);
//		assertEquals(1250, node1.getRangeUpperBound(0), RangeNode.EPSILON);		
//		assertEquals(1250, node1.getRangeLowerBound(1), RangeNode.EPSILON);
//		assertEquals(1500, node1.getRangeUpperBound(1), RangeNode.EPSILON);
//		assertEquals(1500, node1.getRangeLowerBound(2), RangeNode.EPSILON);
//		assertEquals(2000, node1.getRangeUpperBound(2), RangeNode.EPSILON);
//		
//		assertTrue(node1.isRangeLowerBoundOpen(0));
//		assertTrue(node1.isRangeLowerBoundOpen(1));
//		assertTrue(node1.isRangeLowerBoundOpen(2));
//		
//		assertFalse(node1.isRangeUpperBoundOpen(0));
//		assertFalse(node1.isRangeUpperBoundOpen(1));
//		assertFalse(node1.isRangeUpperBoundOpen(2));
//
//	}
//	
//	@Test
//	public void testDecideSimple() { 
//		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 0, true, 40, false, d_excludeNode);
//		FlexibleDose flexDose1 = new FlexibleDose(new Interval<Double>(20.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);
//		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double>(200.0, 300.0), ExampleData.MILLIGRAMS_A_DAY);
//
//		Object someObject = new Object();
//
//		assertEquals(d_excludeNode, node1.decide(flexDose1));
//		
//		expected.expect(IllegalArgumentException.class);
//		node1.decide(flexDose2);
//		
//		expected.expect(IllegalArgumentException.class);
//		node1.decide(someObject);
//		
//	}

}
