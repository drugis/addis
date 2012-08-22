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

package org.drugis.addis.presentation;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.UndirectedSubgraph;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class SelectableTreatmentDefinitionsGraphModel extends TreatmentDefinitionsGraphModel {
	
	private ObservableList<TreatmentDefinition> d_selectedDefinitions = new ArrayListModel<TreatmentDefinition>(d_definitions);
	private ValueHolder<Boolean> d_complete = new ModifiableHolder<Boolean>(false);
	private int d_minSelection;
	private int d_maxSelection;

	/**
	 * Creates a selectable graph of TreatmentDefinitions compared in studies
	 * @param studies the list of studies (the comparisons made in the studies are the edges) 
	 * @param definitions the vertices 
	 * @param outcome the outcome measure to be used for comparison
	 * @param minSelection minimum number of definitions that should be selected 
	 * @param maxSelection maximum number of definitions that should be selected 
	 */
	public SelectableTreatmentDefinitionsGraphModel(ObservableList<Study> studies, 
			ObservableList<TreatmentDefinition> definitions, 
			ValueHolder<OutcomeMeasure> outcome, 
			int minSelection, 
			int maxSelection) {
		super(studies, definitions, outcome);
		
		d_minSelection = minSelection;
		d_maxSelection = maxSelection;
		d_selectedDefinitions.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateComplete();
			}
			public void intervalAdded(ListDataEvent e) {
				updateComplete();
			}
			public void contentsChanged(ListDataEvent e) {
				updateComplete();
			}
		});
		updateComplete();
	}
	
	@Override
	public void rebuildGraph() {
		if (!d_rebuildNeeded) {
			return;
		}
		super.rebuildGraph();
		
		if (d_selectedDefinitions != null) {
			d_selectedDefinitions.clear();
			d_selectedDefinitions.addAll(d_definitions);
		}
	}
	
	private void updateComplete() {
		d_complete.setValue(checkBounds() && isSelectionConnected());
	}

	private boolean checkBounds() {
		int size = getSelectedDefinitions().size();
		return (d_minSelection != -1 ? size >= d_minSelection : true) && (d_maxSelection != -1 ? size <= d_maxSelection : true);
	}
	
	public ValueHolder<Boolean> getSelectionCompleteModel() {
		return d_complete;
	}

	public ObservableList<TreatmentDefinition> getSelectedDefinitions() {
		return d_selectedDefinitions;
	}
	
	public boolean isSelectionConnected() {
		UndirectedGraph<Vertex, Edge> g = getSelectedDefinitionsGraph();
		
		ConnectivityInspector<Vertex, Edge> inspector = new ConnectivityInspector<Vertex, Edge>(g);
		Set<Vertex> connectedDrugs = inspector.connectedSetOf(this.findVertex(d_selectedDefinitions.get(0)));
		for (TreatmentDefinition d : d_selectedDefinitions) {
			if (!connectedDrugs.contains(this.findVertex(d))) {
				return false;
			}
		}
		return true;
	}	

	public UndirectedGraph<Vertex, Edge> getSelectedDefinitionsGraph() {
		UndirectedGraph<Vertex, Edge> newGraph = new UndirectedSubgraph<Vertex, Edge>(this,
				new HashSet<Vertex>(this.vertexSet()), new HashSet<Edge>(this.edgeSet()));
		Set<Vertex> vertices = new HashSet<Vertex>(newGraph.vertexSet());
		for (Vertex v : vertices) {
			if (!d_selectedDefinitions.contains(v.getTreatmentDefinition())) {
				newGraph.removeVertex(v);
			}
		}
		return newGraph;
	}

}
