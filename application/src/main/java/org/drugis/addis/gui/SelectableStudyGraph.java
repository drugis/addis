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

package org.drugis.addis.gui;

import org.drugis.addis.entities.treatment.TreatmentCategorySet;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.wizard.SelectedDrugsGraphListener;
import org.jgraph.JGraph;
import org.jgraph.graph.GraphLayoutCache;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class SelectableStudyGraph extends StudyGraph {

	public SelectableStudyGraph(SelectableStudyGraphModel pm) {
		super(pm);
	}
	
	@Override
	protected JGraph createGraph(GraphLayoutCache cache) {
		final JGraph graph = super.createGraph(cache);
		ObservableList<TreatmentCategorySet> selectedDrugs = ((SelectableStudyGraphModel)d_pm).getSelectedDrugsModel();
		SelectedDrugsGraphListener listener =
			new SelectedDrugsGraphListener(this, graph, selectedDrugs);
		graph.addMouseListener(listener);
		return graph;
	}
	
	@Override
	protected MyDefaultCellViewFactory getCellFactory() {
		return new SelectableCellViewFactory(d_model, ((SelectableStudyGraphModel)d_pm).getSelectedDrugsModel());
	}	

}
