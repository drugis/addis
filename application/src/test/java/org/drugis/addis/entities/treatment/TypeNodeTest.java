package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.junit.Test;

public class TypeNodeTest {
	@Test
	public void testInitialization() {
		TypeNode fixedNode = new TypeNode(FixedDose.class);
		FixedDose fixedDose = new FixedDose();
		FlexibleDose flexibleDose = new FlexibleDose();

		assertTrue(fixedNode.decide(fixedDose));
		assertFalse(fixedNode.decide(flexibleDose));
	}
}
