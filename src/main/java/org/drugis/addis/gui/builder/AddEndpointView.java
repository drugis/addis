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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.gui.components.AutoSelectFocusListener;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;


import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddEndpointView implements ViewBuilder {
	private JTextField d_name;
//	private JTextField d_description;
	private JTextField d_unitOfMeasurement;
	private PresentationModel<Endpoint> d_model;
	private JComboBox d_type;
	private JComboBox d_direction;
	private NotEmptyValidator d_validator;
	
	public AddEndpointView(PresentationModel<Endpoint> model, JButton okButton) {
		d_model = model;
		d_validator = new NotEmptyValidator(okButton);
	}
	
	private void initComponents() {
		d_name = BasicComponentFactory.createTextField(d_model.getModel(Endpoint.PROPERTY_NAME), false);
		AutoSelectFocusListener.add(d_name);
//TODO: Deprecated?
		/*d_description = BasicComponentFactory.createTextField(
				d_model.getModel(Endpoint.PROPERTY_DESCRIPTION), false);
		
		AutoSelectFocusListener.add(d_description);
		d_description.setColumns(30);
		d_validator.add(d_description);
				*/
		
		d_unitOfMeasurement = BasicComponentFactory.createTextField(
				d_model.getModel(Endpoint.PROPERTY_UNIT_OF_MEASUREMENT), false);
		
		AutoSelectFocusListener.add(d_unitOfMeasurement);
		d_unitOfMeasurement.setColumns(30);
				
		d_name.setColumns(30);
	
		d_validator.add(d_name);
	
		d_type = AuxComponentFactory.createBoundComboBox(
				Endpoint.Type.values(), d_model.getModel(Endpoint.PROPERTY_TYPE));
		d_direction = AuxComponentFactory.createBoundComboBox(
				Endpoint.Direction.values(), d_model.getModel(Endpoint.PROPERTY_DIRECTION));
		ComboBoxPopupOnFocusListener.add(d_type);
		d_validator.add(d_type);
	}

	/**
	 * @see org.drugis.common.gui.ViewBuilder#buildPanel()
	 */
	public JComponent buildPanel() {
		initComponents();

		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Endpoint", cc.xyw(1, 1, 3));
		builder.addLabel("Name:", cc.xy(1, 3));
		builder.add(d_name, cc.xy(3,3));
		
		//TODO: Deprecated?
	/*	builder.addLabel("Description:", cc.xy(1, 5));
		builder.add(d_description, cc.xy(3, 5));*/
		
		builder.addLabel("Unit of Measurement:", cc.xy(1, 5));
		builder.add(d_unitOfMeasurement, cc.xy(3, 5));
		
		builder.addLabel("Type:", cc.xy(1, 7));
		builder.add(d_type, cc.xy(3, 7));
		builder.addLabel("Direction:", cc.xy(1, 9));
		builder.add(d_direction, cc.xy(3, 9));
		
		return builder.getPanel();
	}
}