package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.util.BoundedInterval;
import org.junit.Test;

public class RangeEdgeTest {
	@Test
	public void testInitialization() {
		RangeEdge edge = new RangeEdge(1.0, false, 2.0, true);
		
		assertEquals(1, edge.getLowerBound(), BoundedInterval.EPSILON);
		assertEquals(2, edge.getUpperBound(), BoundedInterval.EPSILON);
		assertFalse(edge.isLowerBoundOpen());
		assertTrue(edge.isUpperBoundOpen());
	}
	
	@Test 
	public void testDecide() {
		RangeEdge edge = new RangeEdge(0.0, true, 40.0, false);
		assertTrue(edge.decide(15.0));
		assertTrue(edge.decide(30.0));
		
		assertFalse(edge.decide(50.0));
		assertFalse(edge.decide(-0.1));
	}
	
	@Test
	public void testDecideBoundary() {
		RangeEdge edge = new RangeEdge(0.0, true, 40.0, false);		
		assertFalse(edge.decide(0.0));
		assertTrue(edge.decide(40.0));
	}
}
