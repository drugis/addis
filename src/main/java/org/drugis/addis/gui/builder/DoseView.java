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

package org.drugis.addis.gui.builder;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.common.gui.ViewBuilder;


import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;

public class DoseView implements ViewBuilder {
	PresentationModel<FlexibleDose> d_model;
	private JComboBox d_unit;
	private NotEmptyValidator d_validator;
	private JFormattedTextField d_quantityMin;
	private JFormattedTextField d_quantityMax;
	
	public DoseView(PresentationModel<FlexibleDose> dose) {
		d_model = dose;
	}
	
	public DoseView(PresentationModel<FlexibleDose> dose, NotEmptyValidator validator) {
		d_model = dose;
		d_validator = validator;
	}	
	
	public void initComponents() {
		d_quantityMin = new JFormattedTextField(new DefaultFormatter());
		d_quantityMax = new JFormattedTextField(new DefaultFormatter());
		PropertyConnector.connectAndUpdate(d_model.getModel(FlexibleDose.PROPERTY_MIN_DOSE), d_quantityMin, "value");
		PropertyConnector.connectAndUpdate(d_model.getModel(FlexibleDose.PROPERTY_MAX_DOSE), d_quantityMax, "value");
		d_quantityMin.setColumns(8);
		d_quantityMax.setColumns(8);
		
		SelectionInList<SIUnit> unitSelectionInList = new SelectionInList<SIUnit>(
				SIUnit.values(),
				d_model.getModel(AbstractDose.PROPERTY_UNIT));
		d_unit = BasicComponentFactory.createComboBox(unitSelectionInList);
		ComboBoxPopupOnFocusListener.add(d_unit);
		
		if (d_validator != null) {
			d_validator.add(d_quantityMin);
			d_validator.add(d_unit);			
		}		
	}

	public JComponent buildPanel() {
		initComponents();
		JPanel panel = new JPanel();
		panel.add(d_quantityMin);
		panel.add(new JLabel(" up to "));
		panel.add(d_quantityMax);
		panel.add(d_unit);
		return panel;
	}
}
