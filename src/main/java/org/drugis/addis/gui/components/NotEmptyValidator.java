/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.JTextComponent;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.toedter.calendar.JDateChooser;

@SuppressWarnings("serial")
public class NotEmptyValidator extends AbstractValueModel{
	
	private List<JComponent> d_fields = new ArrayList<JComponent>();
	private DocumentListener d_myTextListener = new MyTextListener();
	private ComboBoxListener d_myActionListener = new ComboBoxListener();
	private MyListDataListener d_myListDataChangeListener = new MyListDataListener();
	private List<ValueModel> d_valModels = new ArrayList<ValueModel>();
	
	public NotEmptyValidator() {
		
	}
	
	//FIXME: @DeprecatedAtSomePoint
	public void add(JComponent field) {
		if (field instanceof JTextComponent) {
			((JTextComponent) field).getDocument().addDocumentListener(d_myTextListener);
			d_fields.add(field);
		} else if (field instanceof JComboBox) {
			((JComboBox) field).addActionListener(d_myActionListener);
			((JComboBox) field).addItemListener(d_myActionListener);
			d_fields.add(field);
		} else if (field instanceof JDateChooser) {
			// FIXME: shouldn't this listen to something?
			d_fields.add(field);
		} else if (field instanceof JList) {
			((JList) field).getModel().addListDataListener(d_myListDataChangeListener);
			d_fields.add(field);
		} else {
			throw new RuntimeException("Unknown component type in validator");
		}
		update();
	}
	
	public void remove(JComponent field) {
		if (field instanceof JTextComponent) {
			((JTextComponent) field).getDocument().removeDocumentListener(d_myTextListener);
			d_fields.remove(field);
		} else if (field instanceof JComboBox) {
			((JComboBox) field).removeActionListener(d_myActionListener);
			((JComboBox) field).removeItemListener(d_myActionListener);
			d_fields.remove(field);
		} else if (field instanceof JDateChooser) {
			d_fields.remove(field);
		} else if (field instanceof JList) {
			((JList) field).getModel().removeListDataListener(d_myListDataChangeListener);
			d_fields.remove(field);
		}
	}
	
	/**
	 * Remove all fields from the validator.
	 */
	public void clear() {
		for (int i = d_fields.size() - 1; i >= 0; --i) {
			remove(d_fields.get(i));
		}
	}
	
	public void update() {
		fireValueChange(null, getValue());
	}

	private boolean checkFieldsEmpty() {
		boolean empty = false;
		for (JComponent f : d_fields) {
			if (f instanceof JTextComponent){
				JTextComponent tf = (JTextComponent) f;
				if (tf.getText().length() == 0) {
					empty = true;
					break;
				}
			} else if (f instanceof JComboBox) {				
				if (((JComboBox) f).getSelectedItem() == null) {
					empty = true;
					break;
				}
			} else if (f instanceof JDateChooser) {
				if (((JDateChooser)f).getDate() == null){
					empty = true;
					break;
				}	
			} else if (f instanceof JList){
				JList list = (JList) f;
				if (list.getModel().getSize() < 2) {
					empty = true;
					break;
				}
			}
			
		}
		for (ValueModel vm: d_valModels) {
			if (vm.getValue().equals("")) {
				empty = true;
				break;
			}
		}
		return empty;
	}
	
	
	public void setValue(Object newValue) {
	}
	
	public Boolean getValue() {
		return !checkFieldsEmpty();
	}
	
	private class MyListDataListener implements ListDataListener {
		public void contentsChanged(ListDataEvent arg0) {
			update();
		}

		public void intervalAdded(ListDataEvent arg0) {
			update();
		}

		public void intervalRemoved(ListDataEvent arg0) {
			update();
		}
	}
	
	private class ComboBoxListener implements ActionListener, ItemListener{
		public void actionPerformed(ActionEvent arg0) {
			update();
		}

		public void itemStateChanged(ItemEvent arg0) {
			update();
		}
	}
	
	private class MyTextListener implements DocumentListener {
		public void changedUpdate(DocumentEvent arg0) {
			update();			
		}

		public void insertUpdate(DocumentEvent arg0) {
			update();
		}

		public void removeUpdate(DocumentEvent arg0) {
			update();
		}
	}

	public void add(ValueModel model) {
		d_valModels.add(model);
		model.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				update();
			}
		});
	}
}
