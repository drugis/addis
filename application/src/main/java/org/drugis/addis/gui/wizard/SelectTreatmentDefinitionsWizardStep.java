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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.SelectableTreatmentDefinitionsGraph;
import org.drugis.addis.presentation.SelectableTreatmentDefinitionsGraphModel;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectTreatmentDefinitionsWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = 2928649302800999758L;
	private Runnable d_rebuild;
	private SelectableTreatmentDefinitionsGraph d_studyGraph;

	public SelectTreatmentDefinitionsWizardStep(
			SelectableTreatmentDefinitionsGraphModel graph,
			Runnable rebuild,
			String title,
			String description) {
		super(title, description);
		d_rebuild = rebuild;

		setLayout(new BorderLayout());

		FormLayout layout = new FormLayout(
				"center:pref:grow",
				"p"
				);

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		d_studyGraph = buildStudiesGraph(graph);
		builder.add(d_studyGraph, cc.xy(1, 1));

		JScrollPane sp = new JScrollPane(builder.getPanel());
		add(sp);
		sp.getVerticalScrollBar().setUnitIncrement(16);

		Bindings.bind(this, "complete", graph.getSelectionCompleteModel());
	}

	private SelectableTreatmentDefinitionsGraph buildStudiesGraph(SelectableTreatmentDefinitionsGraphModel graph) {
		SelectableTreatmentDefinitionsGraph studyGraph = new SelectableTreatmentDefinitionsGraph(graph);
		studyGraph.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return studyGraph;
	}

	@Override
	public void prepare() {
		d_rebuild.run();
		d_studyGraph.layoutGraph();
	}
}