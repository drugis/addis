/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.drugis.addis.entities.Unit;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddUnitView implements ViewBuilder{

	private final PresentationModel<Unit> d_model;
	private JTextField d_name;
	private NotEmptyValidator d_validator;
	private JTextField d_symbol;
	private JPanel d_panel;

	public AddUnitView(PresentationModel<Unit> presentationModel, JButton okButton) {
		d_validator = new NotEmptyValidator();
		Bindings.bind(okButton, "enabled", d_validator);
		d_model = presentationModel;
	}
	
	public void initComponents() {
		d_name = BasicComponentFactory.createTextField(d_model.getModel(Unit.PROPERTY_NAME), false);
		d_name.setColumns(15);
		d_symbol = BasicComponentFactory.createTextField(d_model.getModel(Unit.PROPERTY_SYMBOL), false);
		d_validator.add(d_name);
		d_validator.add(d_symbol);
	}

	@Override
	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Unit", cc.xyw(1, 1, 5));
		builder.addLabel("Name:", cc.xy(1, 3));
		builder.add(d_name, cc.xy(3, 3));
		builder.addLabel("Symbol:", cc.xy(1, 5));
		builder.add(d_symbol, cc.xyw(3, 5, 3));
		
		d_panel = builder.getPanel();
		return d_panel;	
	}

}
