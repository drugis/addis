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
