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
import javax.swing.ListModel;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.drugis.addis.util.ListHolderWrapperPlsDel;
import org.pietschy.wizard.PanelWizardStep;

@SuppressWarnings("serial")
public class SelectEndpointWizardStep extends PanelWizardStep {
	public SelectEndpointWizardStep(AbstractMetaAnalysisWizardPM<?> pm) {
		super("Select Outcome","Select an outcome measure (endpoint or adverse event) that you want to use for this meta analysis.");
		add(new JLabel("Outcome measure : "));

		ListModel outcomeListModel = new ListHolderWrapperPlsDel<OutcomeMeasure>(pm.getOutcomeMeasureListModel());
		
		JComboBox endPointBox = AuxComponentFactory.createBoundComboBox(outcomeListModel, pm.getOutcomeMeasureModel(), true);
		add(endPointBox);
		pm.getOutcomeMeasureModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setComplete(evt.getNewValue() != null);
			}
		});
	}
}