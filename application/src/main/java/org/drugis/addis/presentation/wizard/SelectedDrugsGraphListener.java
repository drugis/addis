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

package org.drugis.addis.presentation.wizard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;

import com.jgoodies.binding.list.ObservableList;

public class SelectedDrugsGraphListener extends MouseAdapter {
	
	private ObservableList<TreatmentDefinition> d_drugList;
	private JGraph d_jgraph;
	private SelectableStudyGraph d_studyGraph;

	public SelectedDrugsGraphListener(SelectableStudyGraph selectableStudyGraph, JGraph graph, ObservableList<TreatmentDefinition> selectedDrugs) {
		d_drugList = selectedDrugs;
		d_studyGraph = selectableStudyGraph;
		d_jgraph = graph;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		Object cell = d_jgraph.getFirstCellForLocation(e.getX(), e.getY());		
		
		if (cell instanceof DefaultGraphCell) {
			DefaultGraphCell realcell = (DefaultGraphCell) cell;
			Object obj = realcell.getUserObject();
			if (obj instanceof Vertex) {
				selectUnselectDrug(((Vertex) obj).getTreatmentDefinition());
			}
		}
	}	

	private void selectUnselectDrug(TreatmentDefinition drug) {
		List<TreatmentDefinition> drugs = new ArrayList<TreatmentDefinition>(d_drugList);
		if (drugs.contains(drug)) {
			drugs.remove(drug);
		} else {
			drugs.add(drug);
		}
		d_drugList.clear();
		d_drugList.addAll(drugs);
		d_studyGraph.resetVertexAttributes();
	}

}
