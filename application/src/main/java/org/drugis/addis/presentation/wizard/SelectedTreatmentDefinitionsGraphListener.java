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

package org.drugis.addis.presentation.wizard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.gui.SelectableTreatmentDefinitionsGraph;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;

import com.jgoodies.binding.list.ObservableList;

public class SelectedTreatmentDefinitionsGraphListener extends MouseAdapter {
	
	private ObservableList<TreatmentDefinition> d_definitionList;
	private JGraph d_jgraph;
	private SelectableTreatmentDefinitionsGraph d_studyGraph;

	public SelectedTreatmentDefinitionsGraphListener(SelectableTreatmentDefinitionsGraph selectableTreatmentDefinitionsGraph, JGraph graph, ObservableList<TreatmentDefinition> selectedDefinitions) {
		d_definitionList = selectedDefinitions;
		d_studyGraph = selectableTreatmentDefinitionsGraph;
		d_jgraph = graph;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		Object cell = d_jgraph.getFirstCellForLocation(e.getX(), e.getY());		
		
		if (cell instanceof DefaultGraphCell) {
			DefaultGraphCell realcell = (DefaultGraphCell) cell;
			Object obj = realcell.getUserObject();
			if (obj instanceof Vertex) {
				selectUnselectDefinition(((Vertex) obj).getTreatmentDefinition());
			}
		}
	}	

	private void selectUnselectDefinition(TreatmentDefinition definition) {
		List<TreatmentDefinition> definitions = new ArrayList<TreatmentDefinition>(d_definitionList);
		if (definitions.contains(definition)) {
			definitions.remove(definition);
		} else {
			definitions.add(definition);
		}
		d_definitionList.clear();
		d_definitionList.addAll(definitions);
		d_studyGraph.resetVertexAttributes();
	}

}
