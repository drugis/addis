package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.metaanalysis.MetaAnalysis;

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
	
	public void removeVariable(Variable var) {
		d_variables.remove(var);
	}
	
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
			d.setIndications(ie.get("indications",TreeSet.class));
			d.setEndpoints(ie.get("endpoints",TreeSet.class));
			
			TreeSet ade = ie.get("adverseEvents",TreeSet.class);
			if (ade != null)
				d.setAdes(ade);
			
			d.setDrugs(ie.get("drugs",TreeSet.class));
			
			TreeSet popchars = ie.get("populationCharacteristics", TreeSet.class);
			if (popchars != null)
				d.setVariables(popchars);
			
			TreeSet catChar = ie.get("categoricalCharacteristic", TreeSet.class);
			if (catChar != null)
				d.setVariables(catChar);
			
			TreeSet contChar = ie.get("continuousCharacteristic", TreeSet.class);
			if (contChar != null)
				d.setVariables(contChar);
			
			TreeSet study = ie.get("studies", TreeSet.class);
			if (study != null)
				d.setStudies(study);
			
			TreeSet analysis = ie.get("metaAnalyses", TreeSet.class);
			if (analysis != null)
				d.setMetaAnalyses(analysis);
		}
		
		@Override
		public void write(DomainData d, OutputElement oe) throws XMLStreamException {
			System.out.println("DomainData::XMLFormat::write " + d.getIndications());
			oe.add(new TreeSet<Indication>(d.getIndications()),"indications",TreeSet.class);
			oe.add(new TreeSet<Endpoint>(d.getEndpoints()),"endpoints",TreeSet.class);
			if (d.getAdverseEvents().size() != 0)
				oe.add(new TreeSet<AdverseEvent>(d.getAdverseEvents()),"adverseEvents",TreeSet.class);
			oe.add(new TreeSet<Drug>(d.getDrugs()), "drugs", TreeSet.class);
			oe.add(new TreeSet<PopulationCharacteristic>(d.getVariables()), "populationCharacteristics", TreeSet.class);
			oe.add(new TreeSet<Study>(d.getStudies()),"studies", TreeSet.class);
			oe.add(new TreeSet<MetaAnalysis>(d.getMetaAnalyses()), "metaAnalyses", TreeSet.class);
		}
	};
}
