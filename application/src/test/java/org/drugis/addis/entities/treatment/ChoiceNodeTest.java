package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.junit.Test;

public class ChoiceNodeTest {
	@Test
	public void testGetValue() {
		Drug drug = new Drug("Fluoxetine", "3");
		ChoiceNode choiceNode = new ChoiceNode(Drug.class, Drug.PROPERTY_NAME);
		assertEquals(drug.getName(), choiceNode.getValue(drug));
		assertEquals("Name", choiceNode.getName());
	}
	
	@Test
	public void testGetValueClass() {
		ChoiceNode choiceNode = new ChoiceNode(String.class, "class");
		assertEquals(String.class, choiceNode.getValue("X"));
		assertEquals("Class", choiceNode.getName());
	}
	
	@Test
	public void testMultiWordName() {
		ChoiceNode choiceNode = new ChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE);
		assertEquals("Min Dose", choiceNode.getName());
	}
	
	@Test(expected=RuntimeException.class)
	public void testIllegalProperty() {
		new ChoiceNode(Drug.class, FixedDose.PROPERTY_QUANTITY);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalObject() {
		new ChoiceNode(Drug.class, Drug.PROPERTY_NAME).getValue("X");
	}
}
