package org.drugis.common;

import static org.junit.Assert.*;

import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.junit.Test;
import static org.drugis.common.JUnitUtil.assertNotEquals;

public class IntervalTest {
	
	@Test
	public void testDoubleInterval() {
		Interval<Double> in = new Interval<Double>(0.0, 1.0);
		assertEquals(0.0, in.getLowerBound(), 0.000001);
		assertEquals(1.0, in.getUpperBound(), 0.000001);
	}
	
	@Test
	public void testIntInterval() {
		Interval<Integer> in = new Interval<Integer>(0,1);
		assertEquals(new Integer(0), in.getLowerBound());
		assertEquals(new Integer(1), in.getUpperBound());
	}
	
	@Test
	public void testGetLength() {
		Interval<Double> in = new Interval<Double>(1.0, 6.0);
		assertEquals(5.0, in.getLength(), 0.00001);
	}
	
	@Test
	public void testEquals() {
		Interval<Double> in = new Interval<Double>(1.0, 6.0);
		Interval<Integer> in2 = new Interval<Integer>(1, 6);
		assertNotEquals(in, in2);
		Double d = new Double(1.0);
		Integer i = new Integer(6);
		assertNotEquals(d, in);
		assertNotEquals(i, in2);
		Interval<Double> in3 = new Interval<Double>(1.0, 6.0);
		Interval<Integer> in4 = new Interval<Integer>(1, 6);
		assertEquals(in, in3);
		assertEquals(in.hashCode(), in3.hashCode());
		assertEquals(in2, in4);
		assertEquals(in2.hashCode(), in4.hashCode());
		Interval<Double> in5 = new Interval<Double>(2.0, 5.0);
		assertNotEquals(in, in5);
		
		ConfidenceInterval in6 = new ConfidenceInterval(1.0, 2.0, 5.0);
		assertNotEquals(in6, in5);
		assertNotEquals(in5, in6);
	}

	@Test
	public void testToString() {
		Interval<Double> in = new Interval<Double>(1.0, 6.0);
		assertEquals("1.0-6.0", in.toString());
	}
}
