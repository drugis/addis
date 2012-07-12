package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FixedDose;
import org.junit.Test;

public class DoseRangeNodeTest {

	@Test
	public void testConvertRange() {
		DoseRangeNode node = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 2400, false, 24000, true, ExampleData.MILLIGRAMS_A_DAY);
		
		FixedDose fixd1 = new FixedDose(2400, ExampleData.MILLIGRAMS_A_DAY);
		FixedDose fixd2 = new FixedDose(0.0001, ExampleData.KILOGRAMS_PER_HOUR);
		assertTrue(fixd1.equals(fixd2));
		
		assertTrue(node.decide(fixd1));
		assertTrue(node.decide(fixd2));
	}
	
	@Test
	public void testCompare() {
		DoseRangeNode node1 = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 2400, false, 
				24000, true, ExampleData.MILLIGRAMS_A_DAY);
		DoseRangeNode node2 = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 2300, false, 
				24000, true, ExampleData.MILLIGRAMS_A_DAY);
		DoseRangeNode node3 = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 2400, false, 
				23000, true, ExampleData.MILLIGRAMS_A_DAY);
		DoseRangeNode node4 = new DoseRangeNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, 2200, false, 
				22000, true, ExampleData.MILLIGRAMS_A_DAY);
		
		// reflexivity
		assertEquals(0, node1.compareTo(node1));
		// commutativity (lower bound)
		assertTrue(node1.compareTo(node2) > 0);
		assertTrue(node2.compareTo(node1) < 0);
		// commutativity (upper bound)
		assertTrue(node1.compareTo(node3) > 0);
		assertTrue(node3.compareTo(node1) < 0);
		
		// lower has priority over upper
		assertTrue(node2.compareTo(node3) < 0);
		
		// transitivity
		assertTrue(node4.compareTo(node1) < 0);
		assertTrue(node4.compareTo(node2) < 0);
	}
}
