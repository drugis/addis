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

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListModel;

import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.gui.renderer.CategoryComboboxRenderer;
import org.drugis.addis.presentation.wizard.TreatmentCategorizationWizardPresentation;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SpecifyDoseTypeWizardStep extends AbstractTreatmentCategorizationWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;

	public SpecifyDoseTypeWizardStep(final TreatmentCategorizationWizardPresentation pm, JDialog dialog) {
		super(pm, "Specify criteria","Select for the category or criteria for the fixed and flexible dose types.", dialog);
	}

	@Override
	protected void initialize() {
		rebuildPanel();
	}

	@Override
	protected JPanel buildPanel() {
		final FormLayout layout = new FormLayout(
				"left:pref, 3dlu, fill:pref:grow",
				"p"
				);

		final PanelBuilder builder = new PanelBuilder(layout);
		final CellConstraints cc = new CellConstraints();

		final int colSpan = layout.getColumnCount();
		int row = 1 ;
		builder.addSeparator("Dose type", cc.xyw(1, row, colSpan));

		row = LayoutUtil.addRow(layout, row);

		builder.addLabel("Fixed dose", cc.xy(1, row));
		final JComboBox fixedCategoryComboBox = BasicComponentFactory.createComboBox(
				new SelectionInList<DecisionTreeNode>((ListModel)d_pm.getOptionsForFixedDose(), d_pm.getModelForFixedDose()), new CategoryComboboxRenderer(false));
		builder.add(fixedCategoryComboBox, cc.xy(3, row));

		row = LayoutUtil.addRow(layout, row);

		builder.addLabel("Flexible dose", cc.xy(1, row));
		final JComboBox flexibleCategoryComboBox = BasicComponentFactory.createComboBox(
				new SelectionInList<DecisionTreeNode>((ListModel)d_pm.getOptionsForFlexibleDose(), d_pm.getModelForFlexibleDose()), new CategoryComboboxRenderer(false));
		builder.add(flexibleCategoryComboBox, cc.xy(3, row));

		return builder.getPanel();
	}
}
