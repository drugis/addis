/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.components.AutoSelectFocusListener;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.VariablePresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddVariableView implements ViewBuilder {
	private JLabel d_dynamicLabel;
	private JTextField d_name;
	private JTextField d_description;
	private JTextField d_unitOfMeasurement;
	private VariablePresentation d_model;
	private JComboBox d_type;
	private JComboBox d_direction;
	private JList d_categories;
	private NotEmptyValidator d_validator;
	private JScrollPane d_scrollPane;
	private JButton d_AddcatBtn;
	private SelectionInList<String> d_categoriesListModel;
	private JButton d_dOkButton;
	
	public AddVariableView(PresentationModel<Variable> model, JButton okButton) {
		d_model = (VariablePresentation) model;
		d_dOkButton = okButton;
		d_validator = new NotEmptyValidator(d_dOkButton);
	}
	
	private void initComponents() {
		ArrayList<Type> values = new ArrayList<Type>(Arrays.asList(Variable.Type.values()));
		if (!(d_model.getBean() instanceof PopulationCharacteristic))
			values.remove(Variable.Type.CATEGORICAL);
		
		d_type = AuxComponentFactory.createBoundComboBox( values.toArray(), d_model.getTypeModel());
		d_type.addItemListener(new ItemListener() {	
			public void itemStateChanged(ItemEvent e) {
				boolean catVisible = d_type.getSelectedItem().equals(Type.CATEGORICAL);
				d_unitOfMeasurement.setVisible(!catVisible);
				d_scrollPane.setVisible(catVisible);
				d_AddcatBtn.setVisible(catVisible);
				if (catVisible){
					d_categoriesListModel = d_model.getCategoriesListModel();
					Bindings.bind(d_categories, d_categoriesListModel);
					d_validator.add(d_categories);
					d_dynamicLabel.setText("Categories: ");
				} else {
					d_dynamicLabel.setText("Unit of Measurement: ");
				}
			}
		});
		
		d_name = BasicComponentFactory.createTextField(d_model.getModel(OutcomeMeasure.PROPERTY_NAME), false);
		AutoSelectFocusListener.add(d_name);
		d_name.setColumns(30);
		d_validator.add(d_name);
		
		d_description = BasicComponentFactory.createTextField(
				d_model.getModel(OutcomeMeasure.PROPERTY_DESCRIPTION), false);
		
		AutoSelectFocusListener.add(d_description);
		d_description.setColumns(30);
		d_validator.add(d_description);
		
		d_unitOfMeasurement = BasicComponentFactory.createTextField(
				d_model.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT), false);
		
		AutoSelectFocusListener.add(d_unitOfMeasurement);
		d_unitOfMeasurement.setColumns(30);
		
		if (d_model.getBean() instanceof OutcomeMeasure) {
			d_direction = AuxComponentFactory.createBoundComboBox(
					OutcomeMeasure.Direction.values(), d_model.getModel(OutcomeMeasure.PROPERTY_DIRECTION));
		}
		
		d_categories = new JList();
		
		d_validator.add(d_type);
	}

	
	
	/**
	 * @see org.drugis.common.gui.ViewBuilder#buildPanel()
	 */
	public JComponent buildPanel() {
		initComponents();

		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		String categoryName = VariablePresentation.getEntityName(d_model.getBean());
		builder.addSeparator(categoryName , cc.xyw(1, 1, 3));
		
		builder.addLabel("Type:", cc.xy(1, 3));
		builder.add(d_type, cc.xy(3, 3));
	
		builder.addLabel("Name:", cc.xy(1, 5));
		builder.add(d_name, cc.xy(3,5));
		
		builder.addLabel("Description:", cc.xy(1, 7));
		builder.add(d_description, cc.xy(3, 7));
		
		d_dynamicLabel = builder.addLabel("Unit of Measurement:", cc.xy(1, 9));
		builder.add(d_unitOfMeasurement, cc.xy(3, 9));
		
		d_scrollPane = new JScrollPane(d_categories);
		d_scrollPane.setVisible(false);
		d_scrollPane.setPreferredSize(new Dimension(60, 40));
		builder.add(d_scrollPane, cc.xy(3, 9));
		
		d_AddcatBtn = GUIFactory.createPlusButton("add new category");
		d_AddcatBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String newCat = JOptionPane.showInputDialog("enter name of new category");
				d_model.addNewCategory(newCat);
			}
		});
		
		d_AddcatBtn.setVisible(false);
		builder.add(d_AddcatBtn,cc.xy(5,9));
		
		if (d_direction != null && !(d_model.getBean() instanceof AdverseEvent)) {
			builder.addLabel("Direction:", cc.xy(1, 11));
			builder.add(d_direction, cc.xy(3, 11));
		}
		
		return builder.getPanel();
	}
}