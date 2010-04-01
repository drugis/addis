package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.util.XMLSet;

public class DomainData implements Serializable {
	private static final long serialVersionUID = 8470783311348971598L;

	private SortedSet<Endpoint> d_endpoints;
	private SortedSet<Drug> d_drugs;
	private SortedSet<Indication> d_indications;
	private SortedSet<AdverseEvent> d_ades;
	private SortedSet<PopulationCharacteristic> d_variables;
	private SortedSet<Study> d_studies;
	private SortedSet<MetaAnalysis> d_metaAnalyses;	

	public DomainData() {
		d_endpoints = new TreeSet<Endpoint>();
		d_studies = new TreeSet<Study>();
		d_metaAnalyses = new TreeSet<MetaAnalysis>();		
		d_drugs = new TreeSet<Drug>();
		d_indications = new TreeSet<Indication>();	
		d_variables = new TreeSet<PopulationCharacteristic>();
		d_ades = new TreeSet<AdverseEvent>();
	}
	
	public void setEndpoints(SortedSet<Endpoint> endpoints) {
		d_endpoints = endpoints;
	}

	public void setDrugs(SortedSet<Drug> drugs) {
		d_drugs = drugs;
	}

	public void setIndications(SortedSet<Indication> indications) {
		d_indications = indications;
	}

	public void setAdes(SortedSet<AdverseEvent> ades) {
		d_ades = ades;
	}

	public void setVariables(SortedSet<PopulationCharacteristic> variables) {
		d_variables = variables;
	}

	public void setStudies(SortedSet<Study> studies) {
		d_studies = studies;
	}

	public void setMetaAnalyses(SortedSet<MetaAnalysis> metaAnalyses) {
		d_metaAnalyses = metaAnalyses;
	}

	public SortedSet<Endpoint> getEndpoints() {
		return d_endpoints;
	}

	public SortedSet<Study> getStudies() {
		return d_studies;
	}

	public SortedSet<MetaAnalysis> getMetaAnalyses() {
		return d_metaAnalyses;
	}

	public SortedSet<Drug> getDrugs() {
		return d_drugs;
	}

	public SortedSet<Indication> getIndications() {
		return d_indications;
	}
	
	public SortedSet<PopulationCharacteristic> getVariables() {
		return d_variables;
	}
	
	public SortedSet<AdverseEvent> getAdverseEvents() {
		return d_ades;
	}
	
	public void addEnpoint(Endpoint e) {
		d_endpoints.add(e);
	}
	
	public void addStudy(Study s) {
		d_studies.add(s);
	}
	
	public void addMetaAnalysis(MetaAnalysis ma) {
		d_metaAnalyses.add(ma);
	}
	
	public void addDrug(Drug d) {
		d_drugs.add(d);
	}
		
	public void addIndication(Indication i) {
		d_indications.add(i);
	}
	
	public void addVariable(PopulationCharacteristic cv) {
		d_variables.add(cv);
	}
	
	public void addAdverseEvent(AdverseEvent ade) {
		d_ades.add(ade);
	}
	
	public void removeEndpoint(OutcomeMeasure e) {
		d_endpoints.remove(e);
	}
	
	public void removeStudy(Study s) {
		d_studies.remove(s);
	}
	
	public void removeMetaAnalysis(MetaAnalysis ma) {
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
	
	public void removeAdverseEvent(AdverseEvent ade) {
		d_ades.remove(ade);
	}
	
/*
	
	private SortedSet<Study> d_studies;
	private SortedSet<MetaAnalysis> d_metaAnalyses;	
*/
	
	
	protected static final XMLFormat<DomainData> XML = new XMLFormat<DomainData>(DomainData.class) {
		public DomainData newInstance(Class<DomainData> cls, InputElement ie) throws XMLStreamException {
			// In newInstance, only use getAttribute, not get. Thats why no indication can be instantiated at this point
			return new DomainData();
		}
		
		public boolean isReferenceable() {
			return false;
		}
		
		@SuppressWarnings("unchecked")
		public void read(InputElement ie, DomainData d) throws XMLStreamException {
			XMLSet indication = ie.get("indications",XMLSet.class);
			d.setIndications((SortedSet) ((XMLSet<Indication>) indication).getSet());
			
			
			XMLSet endpoint = ie.get("endpoints",XMLSet.class);
			d.setEndpoints((SortedSet) ((XMLSet<Endpoint>) endpoint).getSet());
			
			
			XMLSet ade = ie.get("adverse events",XMLSet.class);
			if (ade != null)
				d.setAdes((SortedSet) ((XMLSet<AdverseEvent>) ade).getSet());
			
			
			XMLSet drug = ie.get("drugs",XMLSet.class);
			d.setDrugs((SortedSet) ((XMLSet<Drug>) drug).getSet());
			
			XMLSet popchars = ie.get("populationcharacteristics", XMLSet.class);
			if (popchars != null)
				d.setVariables((SortedSet) ((XMLSet<PopulationCharacteristic>) popchars).getSet());
			
			XMLSet catChar = ie.get("categoricalcharacteristic", XMLSet.class);
			if (catChar != null)
				d.setVariables((SortedSet) ((XMLSet<CategoricalPopulationCharacteristic>) catChar).getSet());
			
			
			XMLSet contChar = ie.get("continuouscharacteristic", XMLSet.class);
			if (contChar != null)
				d.setVariables((SortedSet) ((XMLSet<ContinuousPopulationCharacteristic>) contChar).getSet());
		}
		
		@Override
		public void write(DomainData d, OutputElement oe) throws XMLStreamException {
			System.out.println("DomainData::XMLFormat::write " + d.getIndications());
			oe.add(new XMLSet<Indication>(d.getIndications(),"indication"),"indications",XMLSet.class);
			oe.add(new XMLSet<Endpoint>(d.getEndpoints(),"endpoint"),"endpoints",XMLSet.class);
			if (d.getAdverseEvents().size() != 0)
				oe.add(new XMLSet<AdverseEvent>(d.getAdverseEvents(),"adverse event"),"adverse events",XMLSet.class);
			oe.add(new XMLSet<Drug>(d.getDrugs(), "drug"), "drugs", XMLSet.class);
			oe.add(new XMLSet<PopulationCharacteristic>(d.getVariables(), "populationcharacteristic"), "populationcharacteristics", XMLSet.class);
			oe.add(new XMLSet<Study>(d.getStudies(),"study"),"studies", XMLSet.class);
		}
	};
}
