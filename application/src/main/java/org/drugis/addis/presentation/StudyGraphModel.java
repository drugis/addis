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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.jgrapht.graph.ListenableUndirectedGraph;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class StudyGraphModel extends ListenableUndirectedGraph<StudyGraphModel.Vertex, StudyGraphModel.Edge> {
	public static class Vertex {
		private DrugSet d_drug;
		private int d_sampleSize;
		
		public Vertex(DrugSet drug, int size) {
			d_drug = drug;
			d_sampleSize = size;
		}
		
		public DrugSet getDrug() {
			return d_drug;
		}
		
		public int getSampleSize() {
			return d_sampleSize;
		}
				
		@Override
		public String toString() {
			return d_drug.getLabel();
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
	
	protected ObservableList<DrugSet> d_drugs;
	private ObservableList<Study> d_studies;
	private boolean d_rebuildNeeded;
	private final ValueHolder<OutcomeMeasure> d_om;

	
	public StudyGraphModel(ObservableList<Study> studies, ObservableList<DrugSet> drugs, ValueHolder<OutcomeMeasure> om) {
		super(Edge.class);
		
		d_drugs = drugs;
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
		d_drugs.addListDataListener(rebuildNeededListener);
		
		rebuildGraph();
	}
	
	public void rebuildGraph() {
		if (!d_rebuildNeeded) {
			return;
		}
		ArrayList<Vertex> verts = new ArrayList<Vertex>(vertexSet());
		removeAllVertices(verts);
		for (DrugSet d : d_drugs) {
			addVertex(new Vertex(d, calculateSampleSize(d)));
		}
		
		ArrayList<Edge> edges = new ArrayList<Edge>(edgeSet());
		removeAllEdges(edges);
		for (int i = 0; i < (d_drugs.size() - 1); ++i) {
			for (int j = i + 1; j < d_drugs.size(); ++j) {
				List<Study> studies = getStudies(d_drugs.get(i), d_drugs.get(j));
				if (studies.size() > 0) {
					addEdge(findVertex(d_drugs.get(i)), findVertex(d_drugs.get(j)), new Edge(studies.size()));
				}
			}
		}
		
		d_rebuildNeeded = false;
	}

	public Vertex findVertex(DrugSet drug) {
		for (Vertex v : vertexSet()) {
			if (v.getDrug().equals(drug)) {
				return v;
			}
		}
		//throw new RuntimeException("No vertex for drug " + drug);
		return null;
	}

	private int calculateSampleSize(DrugSet d) {
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
	public List<DrugSet> getDrugs() {
		return d_drugs;
	}
	
	/**
	 * Return the studies with the correct indication and outcome that compare the given drugs.
	 */
	public List<Study> getStudies(DrugSet a, DrugSet b) {
		return filter(b, getStudies(a));
	}
	
	/**
	 * Return the studies with the correct indication and outcome that include the given drug.
	 */
	public List<Study> getStudies(DrugSet d) {
		return filter(d, d_studies);
	}

	private List<Study> filter(DrugSet d, List<Study> allStudies) {
		List<Study> studies = new ArrayList<Study>();
		for (Study s : allStudies) {
			if (s.getMeasuredDrugs(d_om.getValue()).contains(d)) {
				studies.add(s);
			}
		}
		return studies;
	}
}
