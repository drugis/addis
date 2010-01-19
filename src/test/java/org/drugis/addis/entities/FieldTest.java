package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class FieldTest {
	private static final long serialVersionUID = 5552474055585185539L;
	
	@Test
	public void testGetValue() {
		Field <String> testField = new Field <String> ("test text");
		assertEquals("test text", testField.getValue());
	}
	
	@Test(expected = IllegalAccessException.class)
	public void testSetValue() throws IllegalAccessException {
		new Field<String>("").setValue("shouldn't work");
	}

	@Test
	public void testSetNote() {
		Field <String> testField = new Field <String> ("");
		JUnitUtil.testSetter(testField, Field.PROPERTY_NOTE, testField.getNote(), new Note("testText"));
	}

}
