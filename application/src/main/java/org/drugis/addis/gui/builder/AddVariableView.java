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

package org.drugis.addis.gui.builder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.CategoricalVariableType;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.components.AutoSelectFocusListener;
import org.drugis.addis.gui.util.ComboBoxSelectionModel;
import org.drugis.addis.gui.util.NonEmptyValueModel;
import org.drugis.addis.presentation.VariablePresentation;
import org.drugis.common.event.IndifferentListDataListener;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.validation.BooleanAndModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddVariableView implements ViewBuilder {
	private JLabel d_dynamicLabel;
	private JTextField d_name;
	private JTextField d_description;
	private JTextField d_unitOfMeasurement;
	private VariablePresentation d_model;
	private JComboBox d_typeCombo;
	private JComboBox d_direction;
	private JList d_categories;
	private JScrollPane d_scrollPane;
	private JButton d_AddcatBtn;
	private final JDialog d_dialog;
	private final TwoCategoriesModel d_enoughCategories;
	private BooleanAndModel d_validator = new BooleanAndModel();
	
	public AddVariableView(JDialog dialog, PresentationModel<Variable> model, JButton okButton) {
		d_dialog = dialog;
		d_model = (VariablePresentation) model;
		
		d_categories = new JList(d_model.getCategoricalModel().getBean().getCategories());
		
		d_enoughCategories = new TwoCategoriesModel(d_categories.getModel());
		Bindings.bind(okButton, "enabled", d_validator);
	}
	
	private void initComponents() {
		d_typeCombo = AuxComponentFactory.createBoundComboBox(d_model.getVariableTypes(), d_model.getTypeModel());
		d_typeCombo.addItemListener(new ItemListener() {	
			public void itemStateChanged(ItemEvent e) {
				boolean categoricalSelected = d_typeCombo.getSelectedItem() instanceof CategoricalVariableType;
				d_scrollPane.setVisible(categoricalSelected);
				d_AddcatBtn.setVisible(categoricalSelected);
				if (categoricalSelected){
					d_dynamicLabel.setText("Categories: ");
					d_validator.add(d_enoughCategories);
				} else {
					d_dynamicLabel.setText("Unit of Measurement: ");
					d_validator.remove(d_enoughCategories);
				}
				updateUOMVisible();
				d_dialog.pack();
			}
		});
		
		d_name = BasicComponentFactory.createTextField(d_model.getModel(OutcomeMeasure.PROPERTY_NAME), false);
		AutoSelectFocusListener.add(d_name);
		d_name.setColumns(30);
		d_validator.add(new NonEmptyValueModel(d_model.getModel(OutcomeMeasure.PROPERTY_NAME)));
		
		d_description = BasicComponentFactory.createTextField(
				d_model.getModel(OutcomeMeasure.PROPERTY_DESCRIPTION), false);
		
		AutoSelectFocusListener.add(d_description);
		d_description.setColumns(30);
		d_validator.add(new NonEmptyValueModel(d_model.getModel(OutcomeMeasure.PROPERTY_DESCRIPTION)));
		
		d_unitOfMeasurement = BasicComponentFactory.createTextField(
				d_model.getContinuousModel().getModel(ContinuousVariableType.PROPERTY_UNIT_OF_MEASUREMENT));
		AutoSelectFocusListener.add(d_unitOfMeasurement);
		d_unitOfMeasurement.setColumns(30);
		d_dynamicLabel = new JLabel("Unit of Measurement:");
		updateUOMVisible();
		
		if (d_model.getBean() instanceof OutcomeMeasure) {
			d_direction = AuxComponentFactory.createBoundComboBox(
					OutcomeMeasure.Direction.values(), d_model.getModel(OutcomeMeasure.PROPERTY_DIRECTION));
		}
		
		d_validator.add(new NonEmptyValueModel(new ComboBoxSelectionModel(d_typeCombo)));
	}


	private void updateUOMVisible() {
		boolean continuousSelected = d_typeCombo.getSelectedItem() instanceof ContinuousVariableType;
		d_unitOfMeasurement.setVisible(continuousSelected);
		d_dynamicLabel.setVisible(continuousSelected);
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
		builder.add(d_typeCombo, cc.xy(3, 3));
	
		builder.addLabel("Name:", cc.xy(1, 5));
		builder.add(d_name, cc.xy(3,5));
		
		builder.addLabel("Description:", cc.xy(1, 7));
		builder.add(d_description, cc.xy(3, 7));
		
		builder.add(d_dynamicLabel, cc.xy(1, 9));
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
	
	
	private static class TwoCategoriesModel extends AbstractValueModel {
		private static final long serialVersionUID = 7039312266818135795L;
		private ListModel d_list;

		public TwoCategoriesModel(ListModel list) {
			d_list = list;
			list.addListDataListener(new IndifferentListDataListener() {
				protected void update() {
					fireValueChange(null, getValue());
				}
			});
		}

		@Override
		public Boolean getValue() {
			return d_list.getSize() >= 2;
		}

		@Override
		public void setValue(Object newValue) {
			throw new RuntimeException("Unexpected modification");
		}
	}
}
