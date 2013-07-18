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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.DoseView;
import org.drugis.addis.gui.renderer.CategoryComboboxRenderer;
import org.drugis.addis.gui.util.ComboBoxSelectionModel;
import org.drugis.addis.gui.util.NonEmptyValueModel;
import org.drugis.addis.presentation.wizard.TreatmentCategorizationWizardPresentation;
import org.drugis.common.event.IndifferentListDataListener;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.ListItemsUniqueModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddTreatmentCategorizationWizardStep extends AbstractTreatmentCategorizationWizardStep {
	private static final long serialVersionUID = 7730051460456443680L;

	private final BooleanAndModel d_validator = new BooleanAndModel();

	public AddTreatmentCategorizationWizardStep(final TreatmentCategorizationWizardPresentation pm, JDialog dialog) {
		super(pm, "Add characteristics", "Add the name, drug and categories for this treatment", dialog);
		d_validators.add(d_validator);
		d_validators.add(new ListItemsUniqueModel<Category>(d_pm.getCategories(), Category.class, Category.PROPERTY_NAME));
		d_validators.add(pm.getNameAvailableModel());

		d_pm.getCategories().addListDataListener(new IndifferentListDataListener() {
			protected void update() {
				rebuildPanel();
			}
		});
	}

	@Override
	protected JPanel buildPanel() {
		final FormLayout layout = new FormLayout(
				"left:pref, 3dlu, pref, 3dlu, pref, fill:pref:grow, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);

		final PanelBuilder builder = new PanelBuilder(layout);

		final CellConstraints cc = new CellConstraints();
		int row = 1;
		final int colSpan = layout.getColumnCount();

		final JTextField name = BasicComponentFactory.createTextField(d_pm.getModel(TreatmentCategorization.PROPERTY_NAME), false);
		name.setColumns(15);

		builder.addLabel("Drug:", cc.xy(1, row));
		final JComboBox drugSelect = AuxComponentFactory.createBoundComboBox(d_domain.getDrugs(), d_pm.getDrug(), true);
		builder.add(drugSelect, cc.xy(3, row));
		builder.add(createNewDrugButton(d_pm.getDrug()), cc.xy(5, row));
		d_validator.add(new NonEmptyValueModel(new ComboBoxSelectionModel(drugSelect)));

		builder.addLabel("Name:", cc.xy(7, row));
		builder.add(name, cc.xy(9, row));

		d_pm.getNameAvailableModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setNameValidBorder(name);
			}
		});

		d_validator.add(new NonEmptyValueModel(d_pm.getModel(TreatmentCategorization.PROPERTY_NAME)));

		row += 2;
		builder.addSeparator("Category labels", cc.xyw(1, row, colSpan));

		row += 2;
		final JComponent categoriesPanel = createCategoriesPanel();
		final JScrollPane catPane = new JScrollPane(categoriesPanel);
		catPane.setPreferredSize(new Dimension(PANEL_WIDTH, 200));
		builder.add(catPane, cc.xyw(1, row, colSpan));

		row += 2;
		builder.addSeparator("Dose criteria", cc.xyw(1, row, colSpan));

		row += 2;
		builder.addLabel("Unit:", cc.xy(1, row));
		builder.add(DoseView.createDoseUnitRow(d_pm.getDoseUnitPresentation(), d_domain.getUnits()), cc.xyw(3, row, colSpan - 2));

		row += 2;
		builder.addLabel("Unknown dose:", cc.xy(1, row));
		final JComboBox unknownDoseCombo = BasicComponentFactory.createComboBox(
				new SelectionInList<DecisionTreeNode>((ListModel)d_pm.getOptionsForUnknownDose(), d_pm.getModelForUnknownDose()),
				new CategoryComboboxRenderer(false));
		builder.add(unknownDoseCombo, cc.xyw(3, row, colSpan - 2));

		row += 2;
		builder.addLabel("Known dose:", cc.xy(1, row));
		final JComboBox knownDoseCombo = BasicComponentFactory.createComboBox(
				new SelectionInList<DecisionTreeNode>((ListModel)d_pm.getOptionsForKnownDose(), d_pm.getModelForKnownDose()),
				new CategoryComboboxRenderer(false));
		builder.add(knownDoseCombo, cc.xyw(3, row, colSpan - 2));
		return builder.getPanel();
	}


	private void setNameValidBorder(final JTextField name) {
		name.setOpaque(true);
		if (!(Boolean)d_pm.getNameAvailableModel().getValue()) {
			name.setBackground(AuxComponentFactory.COLOR_ERROR);
			name.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			name.setToolTipText("Treatment with this drug and name already exists, please change it.");
		} else {
			name.setBackground(new JTextField().getBackground());
			name.setBorder(new JTextField().getBorder());
			name.setToolTipText(null);
		}

	}

	private JButton createNewDrugButton(final ValueModel drugModel) {
		final JButton btn = GUIFactory.createPlusButton("Create drug");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class), drugModel);
			}
		});
		return btn;
	}

	private JComponent createCategoriesPanel() {
		final FormLayout layout = new FormLayout(
				"left:pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu, pref",
				"p");
		final PanelBuilder builder = new PanelBuilder(layout);
		final CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		int row = 1;

		ObservableList<Category> categories = d_pm.getCategories();
		for (int i = 0; i < categories.size(); i++) {
			final int idx = i;
			Category category = d_pm.getCategories().get(idx);

			builder.add(new JLabel("Category: "), cc.xy(1, row));
			builder.add(BasicComponentFactory.createLabel(getNameModel(idx)), cc.xy(3, row));
			JButton rename = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_EDIT));
			rename.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showRenameDialog(idx);
				}
			});
			builder.add(rename, cc.xy(5, row));

			final JButton remove = GUIFactory.createIconButton(org.drugis.mtc.gui.FileNames.ICON_DELETE, "delete");
			remove.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					d_pm.getCategories().remove(idx);
				}
			});
			builder.add(remove, cc.xy(7, row));

			Bindings.bind(remove, "enabled", d_pm.getCategoryUsed(category));
			Bindings.bind(rename, "enabled", d_pm.getCategoryUsed(category));
			row = LayoutUtil.addRow(layout, row);
		}

		builder.add(createAddCategoryButton(), cc.xy(1, row));
		return builder.getPanel();
	}

	private JButton createAddCategoryButton() {
		final JButton btn = GUIFactory.createLabeledIconButton("Add category", FileNames.ICON_PLUS);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				Category category = new Category(d_pm.getBean());
				category.setName("Category " + ((int)d_pm.getCategories().size() + 1));
				d_pm.getCategories().add(category);
				showRenameDialog(d_pm.getCategories().indexOf(category));
			}
		});
		return btn;
	}


	private ValueModel getNameModel(final int idx) {
		return new PropertyAdapter<TypeWithName>(d_pm.getCategories().get(idx), TypeWithName.PROPERTY_NAME, true);
	}

	private void showRenameDialog(final int idx) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JDialog renameDialog = new RenameCategoryDialog(idx);
				renameDialog.setVisible(true);
			}
		});
	}

	@SuppressWarnings("serial")
	private class RenameCategoryDialog extends RenameDialog {
		private Category d_category;

		public RenameCategoryDialog(int idx) {
			super(d_dialog, "Rename " + getNameModel(idx).getValue(), true, d_pm.getCategories(), idx);
			d_category = d_pm.getCategories().get(idx);
		}

		protected void rename(String newName) {
			d_category.setName(newName);
		}

	}

}