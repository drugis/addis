package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.common.CollectionUtil;
import org.jgrapht.graph.ListenableUndirectedGraph;

@SuppressWarnings("serial")
public class StudyGraphModel extends ListenableUndirectedGraph<StudyGraphModel.Vertex, StudyGraphModel.Edge> {
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
		
		public void setStudyCount(int studies) {
			 d_studies = studies;
		}
		
		public String toString() {
			return Integer.toString(d_studies);
		}
	}
	
	protected ListHolder<Drug> d_drugs;
	private ListHolder<Study> d_studies;
	
	private List<Drug> d_previousUpdateDrugs = new ArrayList<Drug>();
	private List<Study> d_previousUpdateStudies = new ArrayList<Study>();
	
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
	
	public void updateGraph() {	
		if (!needUpdate()) 
			return;

		removeOldEdges();
		removeOldVertices();
			
		addNewVertices();
		addNewEdges();
				
		d_previousUpdateDrugs = cloneList(d_drugs.getValue());
		d_previousUpdateStudies = cloneList(d_studies.getValue());
	}

	private <T> List<T> cloneList (List<T> orig) {
		List<T> newList = new ArrayList<T>();
		newList.addAll(orig);
		return newList;
	}
	
	private boolean needUpdate() {
		return !( CollectionUtil.containsAllAndOnly(d_studies.getValue(), d_previousUpdateStudies) 
				&& CollectionUtil.containsAllAndOnly(d_drugs.getValue(), d_previousUpdateDrugs));
	}

	private void addNewEdges() {
		// Calculate the edges we need to add.
		List<Study> edgesToAdd = new ArrayList<Study>();
		edgesToAdd.addAll(d_studies.getValue());
		edgesToAdd.removeAll(d_previousUpdateStudies); // contains only newly added studies
		// calculate the existing edges
		List<Study> oldEdges = new ArrayList<Study>();
		oldEdges.addAll(d_studies.getValue());
		oldEdges.removeAll(edgesToAdd); // the existing vertices
		
		System.out.println("edges to add " + edgesToAdd);
		
		//		add edges
		for (Study s : edgesToAdd) {
			for(Drug d1 : s.getDrugs()){
				for (Drug d2 : s.getDrugs()){		
					if (d1 == d2)
						continue;
	
					if ((findVertex(d1) != null) && (findVertex(d2) != null)) {
						if (getEdge(findVertex(d1), findVertex(d2)) == null) {
							List<Study> studies = getStudies(d1, d2);
							addEdge(findVertex(d1), findVertex(d2), new Edge(studies.size()));
						}
					}
				}
			}
		}
	}

	private void addNewVertices() {
		// Calculate the vertices we need to add.
		List<Drug> verticesToAdd = new ArrayList<Drug>();
		verticesToAdd.addAll(d_drugs.getValue());
		verticesToAdd.removeAll(d_previousUpdateDrugs); // contains only newly added drugs
		
		System.out.println("vertices to add " + verticesToAdd);
		
		// calculate the vertices that we still want
		List<Drug> oldVertices = new ArrayList<Drug>();
		oldVertices.addAll(d_drugs.getValue());
		oldVertices.removeAll(verticesToAdd); // the existing vertices
		
		// Add the needed vertices
		for (Drug d : verticesToAdd) {
			addVertex(new Vertex(d, calculateSampleSize(d)));
		}
	
		// Add the edges between the new vertices and the existing ones
		for(Drug newDrug : verticesToAdd) {
			for(Drug existingDrug : oldVertices){
				if(newDrug == existingDrug)
					continue;
				
				List<Study> studies = getStudies(newDrug, existingDrug);
				if (studies.size() > 0) {
					addEdge(findVertex(newDrug), findVertex(existingDrug), new Edge(studies.size()));
				}
			}
		}
		
		// Add the edges between the new vertices and the other new vertices
		for (int i = 0; i < (verticesToAdd.size() - 1); ++i) {
			for (int j = i + 1; j < verticesToAdd.size(); ++j) {
				List<Study> studies = getStudies(verticesToAdd.get(i), verticesToAdd.get(j));
				if (studies.size() > 0) {
					addEdge(findVertex(verticesToAdd.get(i)), findVertex(verticesToAdd.get(j)), new Edge(studies.size()));
				}
			}
		}
	}

	private void removeOldVertices() {
		List<Drug> drugs = d_drugs.getValue();
		
		// remove the vertices that should be removed.
		List<Drug> verticesToDelete = new ArrayList<Drug>();
		verticesToDelete.addAll(d_previousUpdateDrugs);
		verticesToDelete.removeAll(d_drugs.getValue()); // contains only deleted drugs.
		
		System.out.println("vertices to delete " + verticesToDelete);
		
		for (Drug drugToDelete : verticesToDelete) {
			for (Drug anyDrug : drugs) {
				if ((findVertex(drugToDelete) == null) || (findVertex(anyDrug) == null))
					continue;
				removeAllEdges(findVertex(drugToDelete), findVertex(anyDrug));
				removeAllEdges(findVertex(anyDrug), findVertex(drugToDelete));
			}
			removeVertex(findVertex(drugToDelete));
		}
	}

	private void removeOldEdges() {
		//remove edges that should be removed
		List<Study> edgesToDelete = new ArrayList<Study>();
		edgesToDelete.addAll(d_previousUpdateStudies);
		edgesToDelete.removeAll(d_studies.getValue()); // contains only deleted studies.
		
		System.out.println("Edges to delete " + edgesToDelete);
		
		for (Study s: edgesToDelete){
			List<Drug> drugs = new ArrayList<Drug>(s.getDrugs());
			for(int i = 0; i < drugs.size() - 1; ++i) {
				Drug d1 = drugs.get(i);
				for (int j = i + 1; j < drugs.size(); ++j){
					Drug d2 = drugs.get(j);
					Vertex vert1 = findVertex(d1);
					Vertex vert2 = findVertex(d2);
					if ((vert1 != null) && (vert2 != null)) {
						Edge toDelete = getEdge(vert1, vert2);
						if (toDelete != null) {
							int origStudyCount = toDelete.getStudyCount();
							removeEdge(toDelete);
							if (origStudyCount > 1)
							{
								System.out.println("Reducing count between " + vert1 + " and " + vert2 + " by one to " + (origStudyCount-1));
								addEdge(vert1, vert2, new Edge(origStudyCount -1));
					
								//toDelete.setStudyCount(origStudyCount-1);
							}
							//else
								//removeEdge(toDelete);
						}
					//removeAllEdges(findVertex(d1), findVertex(d2));
					}
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
		//throw new RuntimeException("No vertex for drug " + drug);
		return null;
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
