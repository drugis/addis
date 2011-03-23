/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.presentation.wizard.WizardWithSelectableIndicationPresentation;
import org.pietschy.wizard.PanelWizardStep;

@SuppressWarnings("serial")
public class SelectIndicationWizardStep extends PanelWizardStep {
	public SelectIndicationWizardStep(WizardWithSelectableIndicationPresentation pm) {
		super("Select Indication","Select an Indication that you want to use for this meta analysis.");
		JComboBox indBox = AuxComponentFactory.createBoundComboBox(pm.getIndicationListModel(), pm.getIndicationModel());
		add(new JLabel("Indication : "));
		add(indBox);
		pm.getIndicationModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setComplete(evt.getNewValue() != null);
			}
		});
	}
}