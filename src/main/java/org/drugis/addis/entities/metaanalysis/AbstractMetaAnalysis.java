package org.drugis.addis.entities.metaanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

public abstract class AbstractMetaAnalysis extends AbstractEntity implements MetaAnalysis {
	private static final long serialVersionUID = 6504073155207712299L;
	
	private static class ArmMap extends HashMap<Study, Map<Drug, Arm>> {
		private static final long serialVersionUID = -8579169115557701584L;

		public ArmMap() {
			super();
		}
		
		public ArmMap(Map<Study, Map<Drug, Arm>> other) {
			super(other);
		}
	}
	
	protected OutcomeMeasure d_outcome;
	protected Indication d_indication;
	protected List<? extends Study> d_studies;
	protected List<Drug> d_drugs;
	protected String d_name;
	protected int d_totalSampleSize;
	protected ArmMap d_armMap;
	
	protected AbstractMetaAnalysis() {
		d_armMap = new ArmMap();
	}
	
	public AbstractMetaAnalysis(String name, 
			Indication indication, OutcomeMeasure om,
			List<? extends Study> studies, List<Drug> drugs, Map<Study, Map<Drug, Arm>> armMap) 
	throws IllegalArgumentException {
		if (studies.isEmpty()) {
			throw new IllegalArgumentException("studylist empty");
		}
		checkSameIndication(studies, indication);
		
		d_drugs = drugs;
		d_studies = studies;
		d_indication = indication;
		d_outcome = om;
		d_name = name;
		d_armMap = new ArmMap(armMap);
		
		setSampleSize();
	}

	private void setSampleSize() {
		for (Study s : d_studies) {
			d_totalSampleSize += s.getSampleSize();
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public void setName(String name) {
		String oldName = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldName, d_name);
	}

	protected void checkSameIndication(List<? extends Study> studies, Indication indication)
	throws IllegalArgumentException {
		for (int i = 1; i < studies.size(); i++) {
			Indication ind2 = studies.get(i).getIndication();
			if (!ind2.equals(indication)) {
				throw new IllegalArgumentException("different indications in studies");
			}
		}
	}

	public String getName() {
		return d_name;
	}

	public int getSampleSize() {
		return d_totalSampleSize;
	}

	public List<Study> getIncludedStudies() {
		return Collections.unmodifiableList(d_studies);
	}

	public OutcomeMeasure getOutcomeMeasure() {
		return d_outcome;
	}

	public int getStudiesIncluded() {
		return d_studies.size();
	}

	@Override
	public Set<Entity> getDependencies() {
		HashSet<Entity> deps = new HashSet<Entity>();
		deps.addAll(getIncludedDrugs());
		deps.add(getIndication());
		deps.add(getOutcomeMeasure());
		deps.addAll(getIncludedStudies());
		return deps;
	}

	public Indication getIndication() {
		return d_indication;
	}
	
	public boolean equals(Object o) { 
		if (o instanceof AbstractMetaAnalysis) {
			AbstractMetaAnalysis other = (AbstractMetaAnalysis)o;
			return (other.getClass() == getClass()) && other.getName().equals(getName());
		}
		return false;
	}
	
	public int hashCode() {
		return getName().hashCode();
	}

	public int compareTo(MetaAnalysis o) {
		return getName().compareTo(o.getName());
	}
	
	public List<Drug> getIncludedDrugs() {
		return Collections.unmodifiableList(d_drugs);
	}
	
	public Arm getArm(Study s, Drug d) {
		return d_armMap.get(s).get(d);
	}
	
	public List<Arm> getArmList(){
		List <Arm>armList = new ArrayList<Arm>();
		for(Study s : d_armMap.keySet()){
			for(Drug d : d_armMap.get(s).keySet()){
				armList.add(d_armMap.get(s).get(d));
			}
		}
		return armList;
	}
	
	protected static final XMLFormat<AbstractMetaAnalysis> XML = new XMLFormat<AbstractMetaAnalysis>(AbstractMetaAnalysis.class) {		
		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie,
				AbstractMetaAnalysis meta) throws XMLStreamException {
			meta.d_name = ie.getAttribute("name").toString();
			meta.d_indication = ie.get("indication", Indication.class);
			meta.d_outcome = ie.get("outcomeMeasure");
			
			meta.d_armMap = ie.get("armEntries", ArmMap.class);
						
			meta.calculateDerived();
		}

		@Override
		public void write(AbstractMetaAnalysis meta,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			oe.setAttribute("name", meta.getName());
			oe.add(meta.getIndication(), "indication", Indication.class);
			oe.add(meta.getOutcomeMeasure(), "outcomeMeasure");

			oe.add(meta.d_armMap, "armEntries", ArmMap.class);
		}
	};
	
	private static class ArmEntry {
		public ArmEntry() {
			
		}
		public ArmEntry(Study s, Drug d, Arm a) {
			study = s;
			drug = d;
			arm = a;
		}
		public Study study;
		public Drug drug;
		public Arm arm;
	}
	
	@SuppressWarnings("unused")
	private static final XMLFormat<ArmMap> armMapXML = new XMLFormat<ArmMap>(ArmMap.class) {
		public ArmMap newInstance(Class<ArmMap> cls, XMLFormat.InputElement xml) {
			return new ArmMap();
		}
		
		@Override
		public boolean isReferenceable() {
			return false;
		}
		
		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie,
				ArmMap ae) throws XMLStreamException {
			while (ie.hasNext()) {
				ArmEntry entry = ie.get("armEntry", ArmEntry.class);
				if (!ae.containsKey(entry.study)) { 
					ae.put(entry.study, new HashMap<Drug, Arm>());
				}
				ae.get(entry.study).put(entry.drug, entry.arm);
			}
		}

		@Override
		public void write(ArmMap am,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			for (Study s : am.keySet()) {
				for (Drug d : am.get(s).keySet()) {
					oe.add(new ArmEntry(s, d, am.get(s).get(d)), "armEntry", ArmEntry.class);
				}
			}
		}
	};
	
	@SuppressWarnings("unused")
	private static final XMLFormat<ArmEntry> armEntryXML = new XMLFormat<ArmEntry>(ArmEntry.class) {
		public ArmEntry newInstance(Class<ArmEntry> cls, XMLFormat.InputElement xml) {
			return new ArmEntry();
		}
		
		@Override
		public boolean isReferenceable() {
			return false;
		}
		
		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie,
				ArmEntry ae) throws XMLStreamException {
			ae.study = ie.get("study", Study.class);
			ae.drug = ie.get("drug", Drug.class);
			ae.arm = ie.get("arm", Arm.class);
		}

		@Override
		public void write(ArmEntry ae,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			oe.add(ae.study);
			oe.add(ae.drug);
			oe.add(ae.arm);
		}
		
	};

	private void calculateDerived() {
		d_studies = new ArrayList<Study>(d_armMap.keySet());
		Collections.sort(d_studies);
		Set<Drug> drugs = new HashSet<Drug>();
		for (Study s : d_studies) {
			drugs.addAll(d_armMap.get(s).keySet());
		}
		d_drugs = new ArrayList<Drug>(drugs);
		Collections.sort(d_drugs);
		setSampleSize();
	}
}