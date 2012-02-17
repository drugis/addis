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

package org.drugis.addis.presentation;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.UndirectedSubgraph;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class SelectableStudyGraphModel extends StudyGraphModel {
	
	private ObservableList<DrugSet> d_selectedDrugs = new ArrayListModel<DrugSet>(d_drugs);
	private ValueHolder<Boolean> d_complete = new ModifiableHolder<Boolean>(false);

	public SelectableStudyGraphModel(ObservableList<Study> studies, ObservableList<DrugSet> drugs, ValueHolder<OutcomeMeasure> outcome) {
		super(studies, drugs, outcome);
		d_selectedDrugs.addListDataListener(new ListDataListener() {
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
		super.rebuildGraph();
		
		if (d_selectedDrugs != null) {
			d_selectedDrugs.clear();
			d_selectedDrugs.addAll(d_drugs);
		}
	}
	
	private void updateComplete() {
		d_complete.setValue(getSelectedDrugsModel().size() > 1 && isSelectionConnected());
	}
	
	public ValueHolder<Boolean> getSelectionCompleteModel() {
		return d_complete;
	}

	public ObservableList<DrugSet> getSelectedDrugsModel() {
		return d_selectedDrugs;
	}
	
	public boolean isSelectionConnected() {
		UndirectedGraph<Vertex, Edge> g = getSelectedDrugsGraph();
		
		ConnectivityInspector<Vertex, Edge> inspectorGadget = new ConnectivityInspector<Vertex, Edge>(g);
		Set<Vertex> connectedDrugs = inspectorGadget.connectedSetOf(this.findVertex(d_selectedDrugs.get(0)));
		for (DrugSet d : d_selectedDrugs) {
			if (!connectedDrugs.contains(this.findVertex(d))) {
				return false;
			}
		}
		return true;
	}	

	public UndirectedGraph<Vertex, Edge> getSelectedDrugsGraph() {
		UndirectedGraph<Vertex, Edge> newGraph = new UndirectedSubgraph<Vertex, Edge>(this,
				new HashSet<Vertex>(this.vertexSet()), new HashSet<Edge>(this.edgeSet()));
		Set<Vertex> vertices = new HashSet<Vertex>(newGraph.vertexSet());
		for (Vertex v : vertices) {
			if (!d_selectedDrugs.contains(v.getDrug())) {
				newGraph.removeVertex(v);
			}
		}
		return newGraph;
	}

}
