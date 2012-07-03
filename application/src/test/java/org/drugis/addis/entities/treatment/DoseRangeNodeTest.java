package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FixedDose;
import org.junit.Test;

public class DoseRangeNodeTest {

	@Test
	public void testConvertRange() {
		CategoryNode child = new CategoryNode("High dose");
		DoseRangeNode node = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 2400, false, 24000, true, ExampleData.MILLIGRAMS_A_DAY, child);
		
		FixedDose fixd1 = new FixedDose(2400, ExampleData.MILLIGRAMS_A_DAY);
		FixedDose fixd2 = new FixedDose(0.0001, ExampleData.KILOGRAMS_PER_HOUR);
		assertTrue(fixd1.equals(fixd2));
		
		assertTrue(node.decide(fixd1));
		assertTrue(node.decide(fixd2));
	}
}
