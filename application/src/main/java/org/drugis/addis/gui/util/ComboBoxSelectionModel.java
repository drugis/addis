package org.drugis.addis.gui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import com.jgoodies.binding.value.AbstractValueModel;

public class ComboBoxSelectionModel extends AbstractValueModel {
	private static final long serialVersionUID = -6232164102805810781L;
	
	private JComboBox d_comboBox;

	public ComboBoxSelectionModel(JComboBox comboBox) {
		d_comboBox = comboBox;
		ComboBoxListener listener = new ComboBoxListener();
		comboBox.addActionListener(listener);
		comboBox.addItemListener(listener);
	}
	
	@Override
	public Object getValue() {
		return d_comboBox.getSelectedItem();
	}

	@Override
	public void setValue(Object newValue) {
		throw new RuntimeException("Modification not allowed");
	}
	
	
	private class ComboBoxListener implements ActionListener, ItemListener{
		public void actionPerformed(ActionEvent ev) {
			fireValueChange(null, getValue());
		}

		public void itemStateChanged(ItemEvent ev) {
			fireValueChange(null, getValue());
		}
	}
}
