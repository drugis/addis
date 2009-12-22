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
	private SortedSet<AdverseDrugEvent> d_ades;
	
	private SortedSet<Variable> d_variables;
	
	public DomainData() {
		d_endpoints = new TreeSet<Endpoint>();
		d_studies = new TreeSet<Study>();
		d_metaAnalyses = new TreeSet<RandomEffectsMetaAnalysis>();		
		d_drugs = new TreeSet<Drug>();
		d_indications = new TreeSet<Indication>();	
		d_variables = new TreeSet<Variable>();
		d_ades = new TreeSet<AdverseDrugEvent>();
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
	
	public SortedSet<Variable> getVariables() {
		return d_variables;
	}
	
	public SortedSet<AdverseDrugEvent> getAdes() {
		return d_ades;
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
	
	public void addVariable(Variable cv) {
		d_variables.add(cv);
	}
	
	public void addAde(AdverseDrugEvent ade) {
		d_ades.add(ade);
	}
	
	public void removeEndpoint(OutcomeMeasure e) {
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
	
	public void removeCategoricalVariable(Variable cv) {
		d_variables.remove(cv);
	}
	
	public void removeAde(AdverseDrugEvent ade) {
		d_ades.remove(ade);
	}
}
