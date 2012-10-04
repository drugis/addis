/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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
