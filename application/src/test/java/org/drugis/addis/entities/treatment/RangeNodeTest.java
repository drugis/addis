package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.common.Interval;
import org.junit.Test;

public class RangeNodeTest {
	@Test
	public void testInitialization() {
		RangeNode node = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, 1, false, 2, true);
		
		assertEquals(1, node.getRangeLowerBound(), RangeNode.EPSILON);
		assertEquals(2, node.getRangeUpperBound(), RangeNode.EPSILON);
		assertFalse(node.isRangeLowerBoundOpen());
		assertTrue(node.isRangeUpperBoundOpen());
	}
		
	@Test 
	public void testDecide() {
		RangeNode node1 = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 0, true, 40, false);
		
		FlexibleDose flexDose1 = new FlexibleDose(new Interval<Double>(10.0, 15.0), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose flexDose2 = new FlexibleDose(new Interval<Double>(25.0, 30.0), ExampleData.MILLIGRAMS_A_DAY);
		assertTrue(node1.decide(flexDose1));
		assertTrue(node1.decide(flexDose2));
		
		RangeNode splittedNode = node1.splitOnValue(20, false);
		assertTrue(node1.decide(flexDose1));
		assertFalse(node1.decide(flexDose2));
		
		assertFalse(splittedNode.decide(flexDose1));
		assertTrue(splittedNode.decide(flexDose2));
	}
	
	@Test
	public void testDecideEdges() {
		RangeNode minNode = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, 0, true, 40, true);
		
		FlexibleDose tooLowDose = new FlexibleDose(new Interval<Double>(0.0, 15.0), ExampleData.MILLIGRAMS_A_DAY);
		assertFalse(minNode.decide(tooLowDose));
		
		RangeNode maxNode = new RangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, 0, true, 40, false);
		maxNode.splitOnValue(20, true);
		
		FlexibleDose maxDoseLowCat = new FlexibleDose(new Interval<Double>(10.0, 20.0 - RangeNode.EPSILON), ExampleData.MILLIGRAMS_A_DAY);
		FlexibleDose maxDoseHighCat = new FlexibleDose(new Interval<Double>(20.0, 40.0), ExampleData.MILLIGRAMS_A_DAY);
		assertTrue(maxNode.decide(maxDoseLowCat));
		assertFalse(maxNode.decide(maxDoseHighCat));
	}
}
