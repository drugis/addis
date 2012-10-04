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

import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.drugis.common.beans.ValueEqualsModel;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.BooleanNotModel;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectIndicationAndNameWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = 7047952357374751159L;

	public SelectIndicationAndNameWizardStep(NetworkMetaAnalysisWizardPM pm, AddisWindow main) {
		super("Define Context", "Select the indication and outcome measure that you want to use for this meta-analysis " +
				"and give it a unique name.");
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref:grow",
				"p, 3dlu, p"
			);	
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();

		int row = IndicationAndNameInputPanel.addToBuilder(builder, 1, pm) + 2;
		
		builder.add(new JLabel("Outcome measure : "), cc.xy(1, row));
		JComboBox endPointBox = AuxComponentFactory.createBoundComboBox(
				pm.getAvailableOutcomeMeasures(), pm.getOutcomeMeasureModel(), true);
		builder.add(endPointBox, cc.xy(3, row));

		add(builder.getPanel());

		ValueModel complete = new BooleanAndModel(Arrays.asList(
				pm.getNameValidModel(),
				new BooleanNotModel(new ValueEqualsModel(pm.getIndicationModel(), null)),
				new BooleanNotModel(new ValueEqualsModel(pm.getOutcomeMeasureModel(), null))
		)); 
		Bindings.bind(this, "complete", complete);
	}
}