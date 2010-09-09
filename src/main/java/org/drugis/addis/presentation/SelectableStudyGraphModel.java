/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.UndirectedSubgraph;

@SuppressWarnings("serial")
public class SelectableStudyGraphModel extends StudyGraphModel {
	
	private ListHolder<Drug> d_selectedDrugs;

	public SelectableStudyGraphModel(ValueHolder<Indication> indication,
			ValueHolder<OutcomeMeasure> outcome, ListHolder<Drug> drugs,
			Domain domain) {
		super(indication, outcome, drugs, domain);
		
		d_selectedDrugs = new DefaultListHolder<Drug>(new ArrayList<Drug>(d_drugs.getValue()));
		d_drugs.addValueChangeListener(new DrugsChangedListener());		
	}
	
	public ListHolder<Drug> getSelectedDrugsModel() {
		return d_selectedDrugs;
	}
	
	public boolean isSelectionConnected() {
		UndirectedGraph<Vertex, Edge> g = getSelectedDrugsGraph();
		
		ConnectivityInspector<Vertex, Edge> inspectorGadget = new ConnectivityInspector<Vertex, Edge>(g);
		Set<Vertex> connectedDrugs = inspectorGadget.connectedSetOf(this.findVertex(d_selectedDrugs.getValue().get(0)));
		for (Drug d : d_selectedDrugs.getValue()) {
			if (!connectedDrugs.contains(this.findVertex(d))) {
				return false;
			}
		}
		return true;
	}	

	private UndirectedGraph<Vertex, Edge> getSelectedDrugsGraph() {
		UndirectedGraph<Vertex, Edge> newGraph = new UndirectedSubgraph<Vertex, Edge>(this,
				new HashSet<Vertex>(this.vertexSet()), new HashSet<Edge>(this.edgeSet()));
		Set<Vertex> vertices = new HashSet<Vertex>(newGraph.vertexSet());
		for (Vertex v : vertices) {
			if (!d_selectedDrugs.getValue().contains(v.getDrug())) {
				newGraph.removeVertex(v);
			}
		}
		return newGraph;
	}

	private class DrugsChangedListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			d_selectedDrugs.setValue(d_drugs.getValue());
		}		
	}
}
