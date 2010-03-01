package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.jgrapht.graph.ListenableUndirectedGraph;

@SuppressWarnings("serial")
public class StudyGraphModel
extends ListenableUndirectedGraph<StudyGraphModel.Vertex, StudyGraphModel.Edge> {
	public static class Vertex {
		private Drug d_drug;
		private int d_sampleSize;
		
		public Vertex(Drug drug, int size) {
			d_drug = drug;
			d_sampleSize = size;
		}
		
		public Drug getDrug() {
			return d_drug;
		}
		
		public int getSampleSize() {
			return d_sampleSize;
		}
				
		public String toString() {
			return d_drug.getName();
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
		
		public String toString() {
			return Integer.toString(d_studies);
		}
	}
	
	protected ListHolder<Drug> d_drugs;
	private ListHolder<Study> d_studies;

	public StudyGraphModel(ListHolder<Study> studies, ListHolder<Drug> drugs){
		super(Edge.class);
		d_drugs = drugs;
		d_studies = studies;
		updateGraph();
		
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				updateGraph();
			}
		};
		d_drugs.addValueChangeListener(listener);
		d_studies.addValueChangeListener(listener);
	}
	
	public StudyGraphModel(ValueHolder<Indication> indication, ValueHolder<OutcomeMeasure> outcome, 
			ListHolder<Drug> drugs, Domain domain) {
		this(new DomainStudyListHolder(domain, indication, outcome), drugs);
	}
	
	private void updateGraph() {
		ArrayList<Edge> edges = new ArrayList<Edge>(edgeSet());
		ArrayList<Vertex> verts = new ArrayList<Vertex>(vertexSet());
		
		removeAllEdges(edges);
		removeAllVertices(verts);
		
		List<Drug> drugs = d_drugs.getValue();
		
		for (Drug d : drugs) {
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

	public Vertex findVertex(Drug drug) {
		for (Vertex v : vertexSet()) {
			if (v.getDrug().equals(drug)) {
				return v;
			}
		}
		throw new RuntimeException("No vertex for drug " + drug);
	}

	private int calculateSampleSize(Drug d) {
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
	public List<Drug> getDrugs() {
		return d_drugs.getValue();
	}
	
	/**
	 * Return the studies with the correct indication and outcome that compare the given drugs.
	 */
	public List<Study> getStudies(Drug a, Drug b) {
		return filter(b, getStudies(a));
	}
	
	/**
	 * Return the studies with the correct indication and outcome that include the given drug.
	 */
	public List<Study> getStudies(Drug a) {
		return filter(a, d_studies.getValue());
	}

	private List<Study> filter(Drug a, List<Study> allStudies) {
		List<Study> studies = new ArrayList<Study>();
		for (Study s : allStudies) {
			if (s.getDrugs().contains(a)) {
				studies.add(s);
			}
		}
		return studies;
	}
}
