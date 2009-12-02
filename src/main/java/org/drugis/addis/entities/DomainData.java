package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;

public class DomainData implements Serializable {
	private static final long serialVersionUID = 8470783311348971598L;

	private SortedSet<Endpoint> d_endpoints;
	private SortedSet<Study> d_studies;
	private SortedSet<RandomEffectsMetaAnalysis> d_metaAnalyses;	
	private SortedSet<Drug> d_drugs;
	private SortedSet<Indication> d_indications;
	
	public DomainData() {
		d_endpoints = new TreeSet<Endpoint>();
		d_studies = new TreeSet<Study>();
		d_metaAnalyses = new TreeSet<RandomEffectsMetaAnalysis>();		
		d_drugs = new TreeSet<Drug>();
		d_indications = new TreeSet<Indication>();	
	}
	
	public SortedSet<Endpoint> getEndpoints() {
		return d_endpoints;
	}

	public SortedSet<Study> getStudies() {
		return d_studies;
	}

	public SortedSet<RandomEffectsMetaAnalysis> getMetaAnalyses() {
		return d_metaAnalyses;
	}

	public SortedSet<Drug> getDrugs() {
		return d_drugs;
	}

	public SortedSet<Indication> getIndications() {
		return d_indications;
	}
	
	public void addEnpoint(Endpoint e) {
		d_endpoints.add(e);
	}
	
	public void addStudy(Study s) {
		d_studies.add(s);
	}
	
	public void addMetaAnalysis(RandomEffectsMetaAnalysis ma) {
		d_metaAnalyses.add(ma);
	}
	
	public void addDrug(Drug d) {
		d_drugs.add(d);
	}
		
	public void addIndication(Indication i) {
		d_indications.add(i);
	}
	
	public void removeEndpoint(Endpoint e) {
		d_endpoints.remove(e);
	}
	
	public void removeStudy(Study s) {
		d_studies.remove(s);
	}
	
	public void removeMetaAnalysis(RandomEffectsMetaAnalysis ma) {
		d_metaAnalyses.remove(ma);
	}
	
	public void removeDrug(Drug d) {
		d_drugs.remove(d);
	}
		
	public void removeIndication(Indication i) {
		d_indications.remove(i);
	}
}
