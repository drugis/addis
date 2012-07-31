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


import javax.swing.JDialog;
import javax.swing.JPanel;

import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.TreatmentCategorizationWizardPresentation;
import org.pietschy.wizard.WizardStep;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangeWizardStep extends AbstractTreatmentCategorizationWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;

	private final RangeInputPresentation d_rangeInputPresentation;
	private final String d_nextPropertyName;
	
	public static WizardStep createOnMultipleParentRanges (
			final JDialog dialog,
			final TreatmentCategorizationWizardPresentation pm,
			final ObservableList<DecisionTreeEdge> parentRanges,
			final String name, final String summary) {
		return new DoseRangesWizardStep(dialog, pm, parentRanges, name, summary);
	}

	public static DoseRangeWizardStep createOnBeanProperty(
			final JDialog dialog,
			final TreatmentCategorizationWizardPresentation pm,
			final ChoiceNode parent,
			final String nextPropertyName,
			final String name,
			final String summary) {
		return new DoseRangeWizardStep(dialog, pm, parent, nextPropertyName, name, summary);
	}

	public static WizardStep createOnKnownDoses(
			final JDialog dialog,
			final TreatmentCategorizationWizardPresentation pm,
			final String name, final String summary) {
		return new DoseRangeWizardStep(dialog, pm, null, null, name, summary);
	}

	private DoseRangeWizardStep(
			final JDialog dialog,
			final TreatmentCategorizationWizardPresentation presentationModel,
			ChoiceNode parent,
			final String nextPropertyName,
			final String name,
			final String summary) {
		super(presentationModel, name, summary, dialog);
		d_nextPropertyName = nextPropertyName;
		if (parent == null) {
			parent = presentationModel.getFixedRangeNode();
		}
		d_rangeInputPresentation = new RangeInputPresentation(d_pm, parent, d_nextPropertyName);
		d_rangeInputPresentation.getRanges().addListDataListener(d_rebuildListener);
	}

	@Override
	public void initialize() {
		// Handle the "ignore dose type" case
		if (!d_pm.getBean().getDecisionTree().containsVertex(d_rangeInputPresentation.getParent())) {
			d_pm.getModelForFixedDose().setValue(d_rangeInputPresentation.getParent());
		}

		// Add default ranges if necessary
		populate(d_pm, d_rangeInputPresentation.getParent());
	}

	public static void populate(final TreatmentCategorizationWizardPresentation pm, final ChoiceNode parent) {
		if (pm.getBean().getDecisionTree().getOutEdges(parent).size() == 0) {
			pm.addDefaultRangeEdge(parent);
		}
	}

	public ValueHolder<Boolean> getConsiderNextProperty() {
		return d_rangeInputPresentation.getConsiderNext();
	}

	@Override
	protected JPanel buildPanel() {
		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);

		final PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;

		final RangeInputBuilder rangeBuilder = new RangeInputBuilder(d_dialog, d_rangeInputPresentation);
		row = rangeBuilder.addFamilyToPanel(builder, row);

		return builder.getPanel();
	}
}
