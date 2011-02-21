/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.entities.analysis;

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
import org.drugis.addis.util.XMLPropertiesFormat;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;

import scala.actors.threadpool.Arrays;

public abstract class AbstractMetaAnalysis extends AbstractEntity implements MetaAnalysis {
	
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
	protected String d_name = "";
	protected int d_totalSampleSize;
	protected ArmMap d_armMap;
	
	protected AbstractMetaAnalysis() {
		d_armMap = new ArmMap();
	}
	
	public AbstractMetaAnalysis(String name, 
			Indication indication, OutcomeMeasure om,
			List<? extends Study> studies, List<Drug> drugs, Map<Study, Map<Drug, Arm>> armMap) 
	throws IllegalArgumentException {
		checkDataConsistency(studies, indication, om);
		
		d_drugs = drugs;
		d_studies = studies;
		d_indication = indication;
		d_outcome = om;
		d_name = name;
		d_armMap = new ArmMap(armMap);
		
		setSampleSize();
	}
	
	public AbstractMetaAnalysis(String name, Indication indication, OutcomeMeasure om, Map<Study, Map<Drug, Arm>> armMap) { 
		this(name, indication, om, calculateStudies(armMap), calculateDrugs(armMap), armMap);
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

	protected void checkDataConsistency(List<? extends Study> studies, Indication indication, OutcomeMeasure om)
	throws IllegalArgumentException {
		if (studies.isEmpty())
			throw new IllegalArgumentException("studylist empty");

		for (int i = 0; i < studies.size(); i++) {
			if (!studies.get(i).getOutcomeMeasures().contains(om))
				throw new IllegalArgumentException("Not all studies are comparing OutcomeMeasure " + om);
			if (!studies.get(i).getIndication().equals(indication))
				throw new IllegalArgumentException("Not all studies measure indication " + indication);
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
	
	@Override
	public boolean equals(Object o) { 
		if (o instanceof AbstractMetaAnalysis) {
			AbstractMetaAnalysis other = (AbstractMetaAnalysis)o;
			return (other.getClass() == getClass()) && other.getName().equals(getName());
		}
		return false;
	}
	
	@Override
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
	
	@SuppressWarnings("unchecked")
	private List<PropertyDefinition> d_propDefs = Arrays.asList(new PropertyDefinition<?>[]{
		new PropertyDefinition<Indication>(PROPERTY_INDICATION, Indication.class) {
			public Indication getValue() { return getIndication(); }
			public void setValue(Object val) { d_indication = (Indication) val; }
		},
		new PropertyDefinition<OutcomeMeasure>(PROPERTY_OUTCOME_MEASURE, null) {
			public OutcomeMeasure getValue() { return getOutcomeMeasure(); }
			public void setValue(Object val) { d_outcome = (OutcomeMeasure) val; }
		},
		new PropertyDefinition<ArmMap>("armEntries", ArmMap.class) {
			public ArmMap getValue() { return d_armMap; }
			public void setValue(Object val) { d_armMap = (ArmMap) val; }
		}
	});
		
	protected static final XMLFormat<AbstractMetaAnalysis> ABSTRACT_META_ANALYSIS_XML = new XMLFormat<AbstractMetaAnalysis>(AbstractMetaAnalysis.class) {		
		
		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie, AbstractMetaAnalysis meta) throws XMLStreamException {
			meta.d_name = ie.getAttribute("name").toString();
			XMLPropertiesFormat.readProperties(ie, meta.d_propDefs);
			meta.calculateDerived();
		}

		@Override
		public void write(AbstractMetaAnalysis meta,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			oe.setAttribute("name", meta.getName());
			XMLPropertiesFormat.writeProperties(meta.d_propDefs, oe);
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
		@Override
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
		@Override
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
		ArmMap armMap = d_armMap;
		d_studies = calculateStudies(armMap);
		d_drugs = calculateDrugs(armMap);
		setSampleSize();
	}

	private static List<Drug> calculateDrugs(Map<Study, Map<Drug, Arm>> armMap) {
		Set<Drug> drugs = new HashSet<Drug>();
		for (Map<Drug, Arm> entry : armMap.values()) {
			drugs.addAll(entry.keySet());
		}
		List<Drug> list = new ArrayList<Drug>(drugs);
		Collections.sort(list);
		return list;
	}

	private static List<Study> calculateStudies(Map<Study, Map<Drug, Arm>> armMap) {
		ArrayList<Study> studies = new ArrayList<Study>(armMap.keySet());
		Collections.sort(studies);
		return studies;
	}
}