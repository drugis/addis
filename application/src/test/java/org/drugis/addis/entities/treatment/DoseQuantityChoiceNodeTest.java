package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.FixedDose;
import org.junit.Test;

public class DoseQuantityChoiceNodeTest {
	@Test
	public void testGetValue() {
		FixedDose dose = new FixedDose(13, ExampleData.MILLIGRAMS_A_DAY);
		ChoiceNode choiceNode = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, ExampleData.MILLIGRAMS_A_DAY);
		assertEquals(dose.getQuantity(), choiceNode.getValue(dose));
	}
	
	@Test
	public void testGetValueConversion() {
		FixedDose dose = new FixedDose(2400, ExampleData.MILLIGRAMS_A_DAY);
		ChoiceNode choiceNode = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, ExampleData.KILOGRAMS_PER_HOUR);
		assertEquals(0.0001, (Double)choiceNode.getValue(dose), 0.000001);
	}
}
