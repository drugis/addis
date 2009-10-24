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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.common.gui.ViewBuilder;


import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddDrugView implements ViewBuilder {
	private JTextField d_name;
	private JTextField d_atcCode;
	private PresentationModel<Drug> d_model;
	private NotEmptyValidator d_validator; 

	public AddDrugView(PresentationModel<Drug> presentationModel, JButton okButton) {
		d_validator = new NotEmptyValidator(okButton);
		d_model = presentationModel;
	}
	
	public void initComponents() {
		d_name = BasicComponentFactory.createTextField(d_model.getModel(Drug.PROPERTY_NAME), false);
		d_name.setColumns(15);
		d_validator.add(d_name);
		d_atcCode = BasicComponentFactory.createTextField(d_model.getModel(Drug.PROPERTY_ATCCODE), false);
		d_validator.add(d_atcCode);		
	}

	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Drug", cc.xyw(1, 1, 3));
		builder.addLabel("Name:", cc.xy(1, 3));
		builder.add(d_name, cc.xy(3,3));
		builder.addLabel("ATC Code:", cc.xy(1, 5));
		builder.add(d_atcCode, cc.xy(3, 5));
		
		return builder.getPanel();	
	}
}