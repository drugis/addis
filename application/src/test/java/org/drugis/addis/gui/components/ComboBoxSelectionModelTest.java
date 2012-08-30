package org.drugis.addis.gui.components;

import static org.junit.Assert.assertEquals;

import javax.swing.JComboBox;

import org.junit.Test;

public class ComboBoxSelectionModelTest {
	@Test
	public void testValueMatchesSelection() {
		Object[] items = {"item 1", ""};
		JComboBox combo = new JComboBox(items);
		ComboBoxSelectionModel model = new ComboBoxSelectionModel(combo);
		
		assertEquals(items[0], model.getValue());
		combo.setSelectedIndex(1);
		assertEquals(items[1], model.getValue());
		combo.setSelectedIndex(0);
		assertEquals(items[0], model.getValue());
		combo.setSelectedIndex(-1);
		assertEquals(null, model.getValue());
	}
}
