package org.drugis.addis.gui.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jgoodies.binding.value.ValueHolder;

public class NonEmptyValueModelTest {
	@Test
	public void testModel() {
		ValueHolder value = new ValueHolder("");
		NonEmptyValueModel nonEmpty = new NonEmptyValueModel(value);
		
		assertFalse(nonEmpty.getValue());
		value.setValue("not empty");
		assertTrue(nonEmpty.getValue());
		value.setValue(null);
		assertFalse(nonEmpty.getValue());
	}
}
