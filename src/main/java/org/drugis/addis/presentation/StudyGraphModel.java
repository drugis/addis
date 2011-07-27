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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.util.ListHolderWrapperPlsDel;
import org.jgrapht.graph.ListenableUndirectedGraph;

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
	
	protected ListHolder<DrugSet> d_drugs;
	private ListModel d_studies;
	private final ValueHolder<OutcomeMeasure> d_om;
	
	public StudyGraphModel(ListHolder<Study> studies, ListHolder<DrugSet> drugs, ValueHolder<OutcomeMeasure> om){
		this(new ListHolderWrapperPlsDel<Study>(studies), drugs, om);
	}
	
	public StudyGraphModel(ListModel studies, ListHolder<DrugSet> drugs, ValueHolder<OutcomeMeasure> om){ // FIXME: change to ObservableList once available.
		super(Edge.class);
		
		d_drugs = drugs;
		d_studies = studies;
		d_om = om;	
		
		resetGraph();
		
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				resetGraph();
			}
		};
		ListDataListener listListener = new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				resetGraph();
			}

			public void intervalAdded(ListDataEvent e) {
				resetGraph();
			}
			

			public void contentsChanged(ListDataEvent e) {
				resetGraph();
			}
		};
		d_drugs.addValueChangeListener(listener);
		d_studies.addListDataListener(listListener);
	}
	
	public void resetGraph() {
		ArrayList<Edge> edges = new ArrayList<Edge>(edgeSet());
		ArrayList<Vertex> verts = new ArrayList<Vertex>(vertexSet());

		removeAllEdges(edges);
		removeAllVertices(verts);

		List<DrugSet> drugs = d_drugs.getValue();

		for (DrugSet d : drugs) {
			addVertex(new Vertex(d, calculateSampleSize(d)));
		}

		for (int i = 0; i < (drugs.size() - 1); ++i) {
			for (int j = i + 1; j < drugs.size(); ++j) {
				List<Study> studies = getStudies(drugs.get(i), drugs.get(j));
				if (studies.size() > 0) {
					addEdge(findVertex(drugs.get(i)), findVertex(drugs.get(j)), new Edge(studies.size()));
				}
			}
		}
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
		return d_drugs.getValue();
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
	
	private List<Study> filter(DrugSet d, ListModel allStudies) {
		List<Study> studies = new ArrayList<Study>();
		for (int i = 0; i < allStudies.getSize(); ++i) {
			Study s = (Study) allStudies.getElementAt(i);
			if (s.getMeasuredDrugs(d_om.getValue()).contains(d)) {
				studies.add(s);
			}
		}
		return studies;
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
