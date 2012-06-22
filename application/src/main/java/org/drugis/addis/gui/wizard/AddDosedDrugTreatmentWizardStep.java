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

package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.ExcludeNode;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.builder.DoseView;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddDosedDrugTreatmentWizardStep extends PanelWizardStep {
	public static final String PROPERTY_KNOWN_CATEGORY = "knownCategory";

	private static final long serialVersionUID = 7730051460456443680L;
	private static final int PANEL_WIDTH = 600;	
	private PresentationModel<DosedDrugTreatment> d_model;
	private JPanel d_dialogPanel = new JPanel();
	private final Domain d_domain;
	private final AddisWindow d_mainWindow;
	private DosedDrugTreatmentPresentation d_pm;
	private NotEmptyValidator d_validator;
	private ValueHolder d_knownDoseCategory = new ValueHolder();

	public static enum KnownCategorySpecifiers {
		CONSIDER("* Consider dose type"), DO_NOT_CONSIDER("* Do not consider dose type");
		
		private final String d_title;

		private KnownCategorySpecifiers(String title) {
			d_title = title; 
		}

		public String getTitle() {
			return d_title;
		}
		
		public String toString() { 
			return getTitle();
		}
	}
	
	public AddDosedDrugTreatmentWizardStep(PresentationModel<DosedDrugTreatment> presentationModel, 
			Domain domain, 
			AddisWindow mainWindow) {
		super("Add characteristics", "Add the name, drug and categories for this treatment");
		d_model = presentationModel;
		d_domain = domain;
		d_mainWindow = mainWindow;
		d_validator = new NotEmptyValidator();
		d_pm = new DosedDrugTreatmentPresentation(d_model.getBean());
		d_pm.getCategories().addListDataListener(new ListDataListener() {			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				rebuildPanel();
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				rebuildPanel();
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				rebuildPanel();
			}
		});
	}		
	
	@Override
	public void prepare() {
		 this.setVisible(false);		 
		 buildWizardStep();
		 PropertyConnector.connectAndUpdate(d_validator, this, "complete");
		 this.setVisible(true);
		 repaint();
	}
	
	public void buildWizardStep() {
		JPanel dialog = buildPanel();
		d_dialogPanel.setLayout(new BorderLayout());
		d_dialogPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 500));
		d_dialogPanel.add(dialog);
		add(d_dialogPanel, BorderLayout.CENTER);	
	}	
	
	public ValueHolder getKnownCategory() { 
		return d_knownDoseCategory;
	}
	
	public Boolean considerDoseType() { 
		String selection = d_knownDoseCategory.getValue().toString();
		if(selection.equals(KnownCategorySpecifiers.CONSIDER.getTitle())) {
			return true;
		} else if(selection.equals(KnownCategorySpecifiers.DO_NOT_CONSIDER.getTitle())) { 
			return false;
		}
		return null;
	}
	
	private void rebuildPanel() {
		d_dialogPanel.setVisible(false);
		d_dialogPanel.removeAll();
		d_dialogPanel.add(buildPanel());
		d_dialogPanel.setVisible(true);
	}
	
	private JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, pref, 3dlu, pref, fill:pref:grow, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		CellConstraints cc = new CellConstraints();
		int row = 1;
		int colSpan = layout.getColumnCount();

		JTextField name = BasicComponentFactory.createTextField(d_model.getModel(DosedDrugTreatment.PROPERTY_NAME), false);
		name.setColumns(15);
		
		final AbstractValueModel drugModel = d_model.getModel(DosedDrugTreatment.PROPERTY_DRUG);
		builder.addLabel("Drug:", cc.xy(1, row));
		JComboBox drugSelect = AuxComponentFactory.createBoundComboBox(d_domain.getDrugs(), drugModel, true);
		builder.add(drugSelect, cc.xy(3, row));
		builder.add(createNewDrugButton(drugModel), cc.xy(5, row));
		d_validator.add(drugSelect);

		builder.addLabel("Name:", cc.xy(7, row));
		builder.add(name, cc.xy(9, row));
		d_validator.add(name);
		
		row += 2;
		builder.addSeparator("Category labels", cc.xyw(1, row, colSpan));
		
		row += 2;
		JComponent categoriesPanel = createCategoriesPanel(d_pm);
		JScrollPane catPane = new JScrollPane(categoriesPanel);
		catPane.setPreferredSize(new Dimension(PANEL_WIDTH, 200));
		builder.add(catPane, cc.xyw(1, row, colSpan));
		
		row += 2;
		builder.addSeparator("Dose criteria", cc.xyw(1, row, colSpan));
		
		row += 2;
		builder.addLabel("Unit:", cc.xy(1, row));
		builder.add(DoseView.createDoseUnitRow(d_pm.getDoseUnitPresentation(), d_domain.getUnits()), cc.xyw(3, row, colSpan - 2));
		
		row += 2;
		builder.addLabel("Unknown dose:", cc.xy(1, row));
		JComboBox unkownDoseCategory = createCategoryComboBox();
		builder.add(unkownDoseCategory, cc.xyw(3, row, colSpan - 2));
		
		row += 2;
		builder.addLabel("Known dose:", cc.xy(1, row));
		JComboBox knownDoseCategory = createCategoryComboBox(KnownCategorySpecifiers.CONSIDER.getTitle(), KnownCategorySpecifiers.DO_NOT_CONSIDER.getTitle());
		d_knownDoseCategory = new ValueHolder(knownDoseCategory.getSelectedItem());

		builder.add(knownDoseCategory, cc.xyw(3, row, colSpan - 2));
		
		return builder.getPanel();
	}

	private JComboBox createCategoryComboBox(String ... extraItems) {
		ObservableList<Object> categories = new ArrayListModel<Object>();
		categories.add(0, new ExcludeNode());
		for (String item : extraItems) {
			categories.add(GUIFactory.createBoxedString(item));
		}
		categories.addAll(d_pm.getCategories());
		final JComboBox comboBox = new JComboBox(categories.toArray());
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object oldVal = e.getItem();
				d_knownDoseCategory.setValue(comboBox.getSelectedItem());
				firePropertyChange(PROPERTY_KNOWN_CATEGORY, oldVal, comboBox.getSelectedIndex());
			}
		});
		return comboBox;
	}
	
	private JButton createNewDrugButton(final AbstractValueModel drugModel) {
		JButton btn = GUIFactory.createPlusButton("Create drug");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class), drugModel);
			}
		});
		return btn;
	}
	
	private JComponent createCategoriesPanel(final DosedDrugTreatmentPresentation model) { 
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, fill:pref:grow, 3dlu, pref",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		int row = 1;

		for(final CategoryNode category : model.getCategories()) {
			builder.add(new JLabel("Category name"), cc.xy(1, row));
			JTextField name = BasicComponentFactory.createTextField(category.getNameModel(), false);
			builder.add(name, cc.xy(3, row));
			JButton remove = GUIFactory.createIconButton(FileNames.ICON_DELETE, "delete");
			remove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					model.getCategories().remove(category);
				}
			});
			builder.add(remove, cc.xy(5, row));
			row = LayoutUtil.addRow(layout, row);
		}
		
		builder.add(createAddCategoryButton(model), cc.xy(1, row));
		
		return builder.getPanel();
	}
	

	private JButton createAddCategoryButton(final DosedDrugTreatmentPresentation model) {
		JButton btn = GUIFactory.createLabeledIconButton("Add category" ,FileNames.ICON_PLUS);
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.getCategories().add(new CategoryNode());
			}
		});
		return btn;
	}
}