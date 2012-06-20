package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RangeNodeTest {
	private ExcludeNode d_excludeNode;
	
	@Rule
    public final ExpectedException expected = ExpectedException.none();
	
	@Before
	public void setUp() {
		d_excludeNode = new ExcludeNode();
	}
	
	@Test
	public void testInitialization() {
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false, d_excludeNode);
		RangeNode node2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, 1, false, 2, true, node1);
		
		
		assertEquals(node1, node2.getChildNode(0));
		assertEquals(1, node2.getRangeLowerBound(0), RangeNode.EPSILON);
		assertEquals(2, node2.getRangeUpperBound(0), RangeNode.EPSILON);
		assertFalse(node2.isRangeLowerBoundOpen(0));
		assertTrue(node2.isRangeUpperBoundOpen(0));
		assertEquals(1, node2.getChildCount());
		
		expected.expect(IndexOutOfBoundsException.class);
		node1.getChildNode(2);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testIndexCorrectness() {
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false, d_excludeNode);
		
		node1.getChildNode(1);
	}
	
	@Test 
	public void setChildNode() { 
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false, d_excludeNode);
		RangeNode node2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false, d_excludeNode);

		node1.setChildNode(0, node2);

		assertEquals(node1.getChildNode(0), node2);
		assertEquals(((RangeNode)node1).isRangeLowerBoundOpen(0), true);
		assertEquals(((RangeNode)node1).isRangeUpperBoundOpen(0), false);
	}
	
	@Test
	public void testSplitCategory() {
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false, d_excludeNode);
		int childIdx = node1.addCutOff(1500, true);
		
		assertEquals(node1.getChildCount(), 2);
		assertEquals(d_excludeNode, node1.getChildNode(childIdx));
		
		assertEquals(1000, node1.getRangeLowerBound(0), RangeNode.EPSILON);
		assertEquals(1500, node1.getRangeUpperBound(0), RangeNode.EPSILON);		
		assertEquals(1500, node1.getRangeLowerBound(1), RangeNode.EPSILON);
		assertEquals(2000, node1.getRangeUpperBound(1), RangeNode.EPSILON);
		
		assertTrue(node1.isRangeLowerBoundOpen(0));
		assertFalse(node1.isRangeLowerBoundOpen(1));
		
		assertTrue(node1.isRangeUpperBoundOpen(0));
		assertFalse(node1.isRangeUpperBoundOpen(1));

	}
	
	@Test
	public void testSplitTwice() { 
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000, true, 2000, false, d_excludeNode);
		int childIdx = node1.addCutOff(1500, false);
		// (1000..1500], (1500..2000]
		
		assertEquals(node1.getChildCount(), 2);
		assertEquals(d_excludeNode, node1.getChildNode(childIdx));
		
		assertEquals(1000, node1.getRangeLowerBound(0), RangeNode.EPSILON);
		assertEquals(1500, node1.getRangeUpperBound(0), RangeNode.EPSILON);		
		assertEquals(1500, node1.getRangeLowerBound(1), RangeNode.EPSILON);
		assertEquals(2000, node1.getRangeUpperBound(1), RangeNode.EPSILON);
		
		assertTrue(node1.isRangeLowerBoundOpen(0));
		assertTrue(node1.isRangeLowerBoundOpen(1));

		assertFalse(node1.isRangeUpperBoundOpen(0));
		assertFalse(node1.isRangeUpperBoundOpen(1));

		CategoryNode midCatNode = new CategoryNode("midrange");
		int childIdx2 = node1.addCutOff(1250, false, midCatNode);
		
		// (1000..1250], (1250..1500], (1500..2000]

		assertEquals(node1.getChildCount(), 3);
		assertEquals(midCatNode, node1.getChildNode(childIdx2));
		
		assertEquals(1000, node1.getRangeLowerBound(0), RangeNode.EPSILON);
		assertEquals(1250, node1.getRangeUpperBound(0), RangeNode.EPSILON);		
		assertEquals(1250, node1.getRangeLowerBound(1), RangeNode.EPSILON);
		assertEquals(1500, node1.getRangeUpperBound(1), RangeNode.EPSILON);
		assertEquals(1500, node1.getRangeLowerBound(2), RangeNode.EPSILON);
		assertEquals(2000, node1.getRangeUpperBound(2), RangeNode.EPSILON);
		
		assertTrue(node1.isRangeLowerBoundOpen(0));
		assertTrue(node1.isRangeLowerBoundOpen(1));
		assertTrue(node1.isRangeLowerBoundOpen(2));
		
		assertFalse(node1.isRangeUpperBoundOpen(0));
		assertFalse(node1.isRangeUpperBoundOpen(1));
		assertFalse(node1.isRangeUpperBoundOpen(2));

	}
	
	@Test
	public void testDecideSimple() { 
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 0, true, 40, false, d_excludeNode);
		FlexibleDose flexDose1 = new FlexibleDose(new Interval<Double>(20.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double>(200.0, 300.0), ExampleData.MILLIGRAMS_A_DAY);

		Object someObject = new Object();

		assertEquals(d_excludeNode, node1.decide(flexDose1));
		
		expected.expect(IllegalArgumentException.class);
		node1.decide(flexDose2);
		
		expected.expect(IllegalArgumentException.class);
		node1.decide(someObject);
		
	}
	
	@Test 
	public void testDecide() {
		ExcludeNode dummy = new ExcludeNode();
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 0, true, 40, false, d_excludeNode);
		node1.addCutOff(20, false);
		node1.setChildNode(1, dummy);
		
		FlexibleDose flexDose1 = new FlexibleDose(new Interval<Double>(10.0, 15.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double>(25.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);
		assertEquals(d_excludeNode, node1.decide(flexDose1));
		assertEquals(dummy, node1.decide(flexDose2));
	}
	
	@Test
	public void testDecideEdges() {
		ExcludeNode dummy = new ExcludeNode();
		RangeNode minNode = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, 0, true, 40, true, d_excludeNode);
		
		FlexibleDose tooLowDose = new FlexibleDose(new Interval<Double>(0.0, 15.0), ExampleData.MILLIGRAMS_A_DAY);
		expected.expect(IllegalArgumentException.class);
		minNode.decide(tooLowDose);
		
		RangeNode maxNode = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 0, true, 40, false, d_excludeNode);
		maxNode.addCutOff(20, true);
		maxNode.setChildNode(1, dummy);
		
		FlexibleDose maxDoseLowCat = new FlexibleDose(new Interval<Double>(10.0, 20.0 - RangeNode.EPSILON), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose maxDoseHighCat = new FlexibleDose(new Interval<Double>(20.0, 40.0), ExampleData.MILLIGRAMS_A_DAY);
		assertEquals(d_excludeNode, maxNode.decide(maxDoseLowCat));
		assertEquals(dummy, maxNode.decide(maxDoseHighCat));
	}
}
