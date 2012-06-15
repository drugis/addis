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
	public void testInitialization() throws Exception {
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000,true , 2000, false, d_excludeNode);
		RangeNode node2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, 1, false, 2, true, node1);
		
		
		assertEquals(node1, node2.getChildNode(0));
		assertEquals(1, node2.getRangeLowerBound(0), 0.000001);
		assertEquals(2, node2.getRangeUpperBound(0), 0.000001);
		assertFalse(node2.isRangeLowerBoundOpen(0));
		assertTrue(node2.isRangeUpperBoundOpen(0));
		assertEquals(1, node2.getChildCount());
		
		expected.expect(IndexOutOfBoundsException.class);
		node1.getChildNode(2);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testIndexCorrectness() {
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000,true , 2000, false, d_excludeNode);
		RangeNode node2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, 1, false, 2, true, node1);
		
		node2.getChildNode(1);
	}
	
	@Test (expected = IndexOutOfBoundsException.class)
	public void setChildNode() { 
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000,true , 2000, false, d_excludeNode);
		RangeNode node2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000,true , 2000, false, d_excludeNode);
		RangeNode node3 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000,true , 2000, false, d_excludeNode);

		node1.setChildNode(0, node2);
		node1.setChildNode(1, node3);

		assertEquals(node1.getChildNode(0), node2);
		assertEquals(node1.getChildNode(1), node3);
	}
	
	@Test
	public void testSplitCategory() {
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000,true , 2000, false, d_excludeNode);
		RangeNode node2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, 20, false, 40, true, node1);
		FlexibleDose flexDose1 = new FlexibleDose(new Interval<Double>(20.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double>(30.0, 39.999), ExampleData.MILLIGRAMS_A_DAY);
		
		node2.addCutOff(30, false);
		
		assertEquals(2, node2.getChildCount());
		assertEquals(node1, node2.getChildNode(1));
		node2.setChildNode(1, d_excludeNode);
		assertEquals(d_excludeNode, node2.getChildNode(1));

	}
	
	@Test
	public void testDecide() { 
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 1000,true , 2000, false, d_excludeNode);
		RangeNode node2 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, 20, false, 40, true, node1);
		FlexibleDose flexDose1 = new FlexibleDose(new Interval<Double>(20.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double>(30.0, 39.999), ExampleData.MILLIGRAMS_A_DAY);
		
		node2.addCutOff(30, false);
		
		assertEquals(node1, node2.decide(flexDose1));
		assertEquals(node1, node2.decide(flexDose2));
		
		node2.setChildNode(1, d_excludeNode);
		assertEquals(node1, node2.decide(flexDose1));
		assertEquals(d_excludeNode, node2.decide(flexDose2));
		
	}
}
