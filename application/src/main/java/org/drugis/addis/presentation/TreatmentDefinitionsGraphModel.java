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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.collections15.CollectionUtils;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.jgrapht.graph.ListenableUndirectedGraph;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class TreatmentDefinitionsGraphModel extends ListenableUndirectedGraph<TreatmentDefinitionsGraphModel.Vertex, TreatmentDefinitionsGraphModel.Edge> {
	public static class Vertex {
		private TreatmentDefinition d_definition;
		private int d_sampleSize;
		
		public Vertex(TreatmentDefinition def, int size) {
			d_definition = def;
			d_sampleSize = size;
		}
		
		public TreatmentDefinition getTreatmentDefinition() {
			return d_definition;
		}
		
		public int getSampleSize() {
			return d_sampleSize;
		}
				
		@Override
		public String toString() {
			return d_definition.getLabel();
		}
	}
	
	public static class Edge {
		private int d_studies;
		
		public Edge(int studies) {
			d_studies = studies;
		}
		
		public int getStudyCount() {
			return d_studies;
		}
		
		public void setStudyCount(int studies) {
			 d_studies = studies;
		}
		
		@Override
		public String toString() {
			return Integer.toString(d_studies);
		}
	}
	
	protected ObservableList<TreatmentDefinition> d_definitions;
	private ObservableList<Study> d_studies;
	protected boolean d_rebuildNeeded;
	private final ValueHolder<OutcomeMeasure> d_om;
	private Map<TreatmentDefinition, Set<Study>> d_studiesMeasuringDefinition;

	
	public TreatmentDefinitionsGraphModel(ObservableList<Study> studies, ObservableList<TreatmentDefinition> definitions, ValueHolder<OutcomeMeasure> om) {
		super(Edge.class);
		
		d_definitions = definitions;
		d_studies = studies;
		d_om = om;
		
		ListDataListener rebuildNeededListener = new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				d_rebuildNeeded = true;
			}
			public void intervalAdded(ListDataEvent e) {
				d_rebuildNeeded = true;
			}
			public void contentsChanged(ListDataEvent e) {
				d_rebuildNeeded = true;
			}
		};
		
		d_rebuildNeeded = true;
		d_studies.addListDataListener(rebuildNeededListener);
		d_definitions.addListDataListener(rebuildNeededListener);
		
		rebuildGraph();
	}
	
	public void rebuildGraph() {
		if (!d_rebuildNeeded) {
			System.out.println("Rebuild not needed");
			return;
		}

		initStudiesMeasuringDefinition();
		
		// Add vertices
		ArrayList<Vertex> verts = new ArrayList<Vertex>(vertexSet());
		removeAllVertices(verts);		
		for (TreatmentDefinition d : d_definitions) {
			addVertex(new Vertex(d, calculateSampleSize(d)));
		}
		
		// Add edges
		ArrayList<Edge> edges = new ArrayList<Edge>(edgeSet());
		removeAllEdges(edges);
		for (int i = 0; i < (d_definitions.size() - 1); ++i) {
			for (int j = i + 1; j < d_definitions.size(); ++j) {
				Collection<Study> studies = getStudies(d_definitions.get(i), d_definitions.get(j));
				if (studies.size() > 0) {
					addEdge(findVertex(d_definitions.get(i)), findVertex(d_definitions.get(j)), new Edge(studies.size()));
				}
			}
		}
		
		d_rebuildNeeded = false;
	}

	private void initStudiesMeasuringDefinition() {
		d_studiesMeasuringDefinition = new HashMap<TreatmentDefinition, Set<Study>>(); 
		for(TreatmentDefinition d : d_definitions) { 
			d_studiesMeasuringDefinition.put(d, new HashSet<Study>());
			for(Study s : d_studies) { 
				for(Arm arm : s.getArms()) { 
					if(d.match(s, arm) && s.getOutcomeMeasures().contains(d_om.getValue())) {
						d_studiesMeasuringDefinition.get(d).add(s);
					}
				}
			}
		}
	}

	public Vertex findVertex(TreatmentDefinition definition) {
		for (Vertex v : vertexSet()) {
			if (v.getTreatmentDefinition().equals(definition)) {
				return v;
			}
		}
		return null;
	}

	private int calculateSampleSize(TreatmentDefinition d) {
		int n = 0;
		for (Study s : getStudies(d)) {
			n += s.getSampleSize();
		}
		return n;
	}

	/**
	 * Return the list of drugs that are included in at least one of the studies having the correct indication
	 * and outcome.
	 */
	public List<TreatmentDefinition> getTreatmentDefinitions() {
		return d_definitions;
	}
	
	/**
	 * Return the studies with the correct indication and outcome that compare the given drugs.
	 */
	public Collection<Study> getStudies(TreatmentDefinition a, TreatmentDefinition b) {
		return CollectionUtils.intersection(getStudies(a), getStudies(b));
	}
	
	/**
	 * Return the studies with the correct indication and outcome that include the given drug.
	 */
	public Collection<Study> getStudies(TreatmentDefinition d) {
		return d_studiesMeasuringDefinition.get(d);
	}
}
