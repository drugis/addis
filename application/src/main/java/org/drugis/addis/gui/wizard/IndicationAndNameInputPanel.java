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

package org.drugis.addis.gui.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.presentation.wizard.AnalysisWizardPresentation;
import org.drugis.common.beans.ValueEqualsModel;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.BooleanNotModel;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class IndicationAndNameInputPanel {

	private static void updateBorder(final JTextField nameInput, final Border border, AnalysisWizardPresentation pm) {
		if (!pm.getNameValidModel().getValue()){
			nameInput.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			if (pm.getNameModel().getValue() == null || pm.getNameModel().getValue().isEmpty()) {
				nameInput.setToolTipText("An analysis must have a name, please provide one.");
			} else {
				nameInput.setToolTipText("An analysis with that name already exists, please change it.");
			}
			nameInput.dispatchEvent(new KeyEvent(nameInput, KeyEvent.KEY_PRESSED, 0, KeyEvent.CTRL_MASK, KeyEvent.VK_F1, KeyEvent.CHAR_UNDEFINED));
		} else {
			nameInput.setToolTipText("");
			nameInput.setBorder(border);
		}
	}

	public static Component create(final PanelWizardStep panelWizardStep, final AnalysisWizardPresentation pm) {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref:grow",
				"p"
			);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		addToBuilder(builder, 1, pm);

		ValueModel complete = new BooleanAndModel(pm.getNameValidModel(),
				new BooleanNotModel(new ValueEqualsModel(pm.getIndicationModel(), null)));
		Bindings.bind(panelWizardStep, "complete", complete);
		return builder.getPanel();
	}

	/**
	 * Add the name and indication components to an existing builder. The builder's layout is assumed to have
	 * at least three columns, and at least one available row. Rows are added as needed.
	 * @param complete
	 * @return The index of the last used row.
	 */
	public static int addToBuilder(PanelBuilder builder, int row, final AnalysisWizardPresentation pm) {
		CellConstraints cc = new CellConstraints();

		final JTextField nameInput = BasicComponentFactory.createTextField(pm.getNameModel(), false);
		nameInput.setColumns(20);
		final Border border = nameInput.getBorder();
		builder.add(new JLabel("Name : "), cc.xy(1, row));
		builder.add(nameInput, cc.xy(3, row));
		pm.getNameValidModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateBorder(nameInput, border, pm);
			}
		});

		row = LayoutUtil.addRow(builder.getLayout(), row);
		JComboBox indBox = AuxComponentFactory.createBoundComboBox(pm.getIndicationsModel(), pm.getIndicationModel(), true);
		builder.add(new JLabel("Indication : "), cc.xy(1, row));
		builder.add(indBox, cc.xy(3, row));

		return row;
	}

}
