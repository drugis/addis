/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package nl.rug.escher.addis.gui;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatter;

import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;

public class DoseView implements ViewBuilder {
	PresentationModel<Dose> d_model;
	private JFormattedTextField d_quantity;
	private JComboBox d_unit;
	private NotEmptyValidator d_validator;
	
	public DoseView(PresentationModel<Dose> dose) {
		d_model = dose;
	}
	
	public DoseView(PresentationModel<Dose> dose, NotEmptyValidator validator) {
		d_model = dose;
		d_validator = validator;
	}	
	
	public void initComponents() {
		d_quantity = new JFormattedTextField(new DefaultFormatter());
		PropertyConnector.connectAndUpdate(d_model.getModel(Dose.PROPERTY_QUANTITY), d_quantity, "value");
		d_quantity.setColumns(8);
		
		SelectionInList<SIUnit> unitSelectionInList = new SelectionInList<SIUnit>(
				SIUnit.values(),
				d_model.getModel(Dose.PROPERTY_UNIT));
		d_unit = BasicComponentFactory.createComboBox(unitSelectionInList);
		ComboBoxPopupOnFocusListener.add(d_unit);
		
		if (d_validator != null) {
			d_validator.add(d_quantity);
			d_validator.add(d_unit);			
		}		
	}

	public JComponent buildPanel() {
		initComponents();
		JPanel panel = new JPanel();
		panel.add(d_quantity);
		panel.add(d_unit);
		return panel;
	}
}
