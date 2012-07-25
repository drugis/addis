package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.junit.Test;

public class TypeEdgeTest {
	@Test
	public void testExactMatch() {
		TypeEdge fixedNode = new TypeEdge(FixedDose.class);
		assertTrue(fixedNode.decide(FixedDose.class));
		assertFalse(fixedNode.decide(FlexibleDose.class));
		
		TypeEdge flexNode = new TypeEdge(FlexibleDose.class);
		assertFalse(flexNode.decide(FixedDose.class));
		assertTrue(flexNode.decide(FlexibleDose.class));
	}
}
