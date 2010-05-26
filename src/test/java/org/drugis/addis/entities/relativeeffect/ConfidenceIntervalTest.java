package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.*;
import static org.drugis.common.JUnitUtil.assertNotEquals;
import org.junit.Test;

public class ConfidenceIntervalTest {
	@Test public void testGetPointEstimate() {
		ConfidenceInterval ci1 = new ConfidenceInterval(1.044, 0.0, 2.0);
		assertEquals(1.044, ci1.getPointEstimate(), 0.00000001);
	}
	
	@Test public void testToString() {
		ConfidenceInterval ci1 = new ConfidenceInterval(1.044, 0.0, 2.0);
		assertEquals("1.04 (0.00, 2.00)", ci1.toString());
	}
	
	@Test public void testEquals() {
		ConfidenceInterval ci1 = new ConfidenceInterval(1.0, 0.0, 2.0);
		ConfidenceInterval ci2 = new ConfidenceInterval(1.0, 0.0, 2.0);
		ConfidenceInterval ci3 = new ConfidenceInterval(0.5, 0.0, 2.0);
		ConfidenceInterval ci4 = new ConfidenceInterval(1.0, 0.5, 2.0);
		ConfidenceInterval ci5 = new ConfidenceInterval(1.0, 0.0, 2.5);
		
		assertEquals(ci1, ci2);
		assertEquals(ci1.hashCode(), ci2.hashCode());
		assertNotEquals(ci1, ci3);
		assertNotEquals(ci1, ci4);
		assertNotEquals(ci1, ci5);
	}
}
