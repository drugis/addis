package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.jgrapht.graph.ListenableUndirectedGraph;

@SuppressWarnings("serial")
public class StudyGraphPresentation
extends ListenableUndirectedGraph<StudyGraphPresentation.Vertex, StudyGraphPresentation.Edge> {
	public class Vertex {
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
	
	public class Edge {
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
	
	private ValueHolder<Indication> d_indication;
	private ValueHolder<Endpoint> d_endpoint;
	private Domain d_domain;
	private ListHolder<Drug> d_drugs;

	public StudyGraphPresentation(ValueHolder<Indication> indication, ValueHolder<Endpoint> endpoint, 
			ListHolder<Drug> drugs, Domain domain) {
		super(Edge.class);
		d_indication = indication;
		d_endpoint = endpoint;
		d_drugs = drugs;
		d_domain = domain;
		updateGraph();
		
		d_drugs.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				updateGraph();
			}
		});
	}
	
	private void updateGraph() {
		removeAllEdges(edgeSet());
		removeAllVertices(vertexSet());
		
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
	 * and endpoint.
	 */
	public List<Drug> getDrugs() {
		return d_drugs.getValue();
	}
	
	/**
	 * Return the studies with the correct indication and endpoint that compare the given drugs.
	 */
	public List<Study> getStudies(Drug a, Drug b) {
		List<Study> studies = getStudies(a);
		studies.retainAll(d_domain.getStudies(b).getValue());
		return studies;
	}
	
	/**
	 * Return the studies with the correct indication and endpoint that include the given drug.
	 */
	public List<Study> getStudies(Drug a) {
		List<Study> studies = d_domain.getStudies(a).getValue();
		studies.retainAll(d_domain.getStudies(getIndication()).getValue());
		studies.retainAll(d_domain.getStudies(getEndpoint()).getValue());
		return studies;
	}

	private OutcomeMeasure getEndpoint() {
		return d_endpoint.getValue();
	}

	private Indication getIndication() {
		return d_indication.getValue();
	}
}
