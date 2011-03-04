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

package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLPropertiesFormat;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;
import org.drugis.addis.util.comparator.OutcomeComparator;
import org.drugis.common.DateUtil;
import org.drugis.common.EqualsUtil;

import scala.actors.threadpool.Arrays;

public class Study extends AbstractEntity implements Comparable<Study>, Entity {

	public static class MeasurementKey extends AbstractEntity implements Entity {

		private Variable d_variable;
		private Arm d_arm;

		public MeasurementKey(Variable v, Arm g)  {
			if (v == null) {
				throw new NullPointerException("Variable may not be null");
			}
			if (v instanceof OutcomeMeasure && g == null) {
				throw new NullPointerException("Arm may not be null for Endpoints/ADEs");
			}
			d_variable = v;
			d_arm = g;
		}

		public Variable getVariable() {
			return d_variable;
		}
		
		public Arm getArm() {
			return d_arm;
		}
		
		@Override
		public String toString() {
			return "<" + d_variable + ", " + d_arm + ">";
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof MeasurementKey) { 
				MeasurementKey other = (MeasurementKey)o;
				return d_variable.equals(other.d_variable) && EqualsUtil.equal(d_arm, other.d_arm);
			}
			return false;
		}

		@Override
		public int hashCode() {
			int code = 1;
			code = code * 31 + d_variable.hashCode();
			code = code * 31 + (d_arm == null ? 0 : d_arm.hashCode());
			return code;
		}

		@Override
		public Set<? extends Entity> getDependencies() {
			return null;
		}
	}
	
	public class StudyOutcomeMeasure<T extends Variable> extends ObjectWithNotes<T> {
		public StudyOutcomeMeasure(T obj) {
			super(obj);
		}
		
		@Override
		public StudyOutcomeMeasure<T> clone() {
			StudyOutcomeMeasure<T> clone = new StudyOutcomeMeasure<T>(getValue());
			clone.getNotes().addAll(getNotes());
			return clone;
		}
	}

	public final static String PROPERTY_ID = "studyId";
	public final static String PROPERTY_ENDPOINTS = "endpoints";
	public final static String PROPERTY_ADVERSE_EVENTS = "adverseEvents";
	public final static String PROPERTY_POPULATION_CHARACTERISTICS = "populationCharacteristics";
	public final static String PROPERTY_ARMS = "arms";
	public final static String PROPERTY_CHARACTERISTICS = "characteristics";
	public final static String PROPERTY_NOTES = "notes";
	public final static String PROPERTY_INDICATION = "indication";
	
	private List<Arm> d_arms = new ArrayList<Arm>();
	private ObjectWithNotes<String> d_studyId;
	private Map<MeasurementKey, Measurement> d_measurements = new HashMap<MeasurementKey, Measurement>();
	private List<StudyOutcomeMeasure<Endpoint>> d_endpoints = new ArrayList<StudyOutcomeMeasure<Endpoint>>();
	private List<StudyOutcomeMeasure<AdverseEvent>> d_adverseEvents = new ArrayList<StudyOutcomeMeasure<AdverseEvent>>();
	private List<StudyOutcomeMeasure<PopulationCharacteristic>> d_populationChars = new ArrayList<StudyOutcomeMeasure<PopulationCharacteristic>>();
	private CharacteristicsMap d_chars = new CharacteristicsMap();
	private ObjectWithNotes<Indication> d_indication;
	private List<Integer> d_armIds = null;
	
	public Study() {
		d_indication = new ObjectWithNotes<Indication>(null);
		d_studyId = new ObjectWithNotes<String>(null);
	}

	@Override
	public Study clone() {
		Study newStudy = new Study();
		newStudy.d_studyId = d_studyId.clone();
		newStudy.d_indication = d_indication.clone();

		newStudy.setArms(cloneArms());

		newStudy.d_endpoints = cloneStudyOutcomeMeasures(d_endpoints);
		newStudy.d_adverseEvents = cloneStudyOutcomeMeasures(d_adverseEvents);
		newStudy.d_populationChars = cloneStudyOutcomeMeasures(d_populationChars);

		// Copy measurements _AFTER_ the outcomes, since setEndpoints() etc removes orphan measurements from the study.
		newStudy.setMeasurements(cloneMeasurements(newStudy.getArms()));
		
		newStudy.setCharacteristics(cloneCharacteristics());
		return newStudy;
	}

	private <T extends Variable> List<StudyOutcomeMeasure<T>> cloneStudyOutcomeMeasures(List<StudyOutcomeMeasure<T>> soms) {
		List<StudyOutcomeMeasure<T>> list = new ArrayList<StudyOutcomeMeasure<T>>();
		for (StudyOutcomeMeasure<T> som : soms) {
			list.add(som.clone());
		}
		return list;
	}
	
	private CharacteristicsMap cloneCharacteristics() {
		CharacteristicsMap cm = new CharacteristicsMap();
		for(Characteristic c : d_chars.keySet()){
			cm.put(c, d_chars.get(c).clone());
		}
		return cm;
	}

	private Map<MeasurementKey, Measurement> cloneMeasurements(List<Arm> newArms) {
		HashMap<MeasurementKey, Measurement> hashMap = new HashMap<MeasurementKey, Measurement>();
		for(MeasurementKey key : d_measurements.keySet()) {
			hashMap.put(fixKey(key, newArms), d_measurements.get(key).clone());
		}
		return hashMap;
	}

	private MeasurementKey fixKey(MeasurementKey key, List<Arm> newArms) {
		if (key.getArm() == null) {
			return key;
		}
		int idx = getArms().indexOf(key.getArm());
		return new MeasurementKey(key.getVariable(), newArms.get(idx));
	}

	private List<Arm> cloneArms() {
		List<Arm> newList = new ArrayList<Arm>();
		for(Arm a : getArms()) {
			newList.add(a.clone());
		}
		return newList;
	}

	public Study(String id, Indication i) {
		d_studyId = new ObjectWithNotes<String>(id);
		d_indication = new ObjectWithNotes<Indication>(i);
		setArms(new ArrayList<Arm>());
		setCharacteristic(BasicStudyCharacteristic.CREATION_DATE, DateUtil.getCurrentDateWithoutTime());
		setCharacteristic(BasicStudyCharacteristic.TITLE, "");
		setCharacteristic(BasicStudyCharacteristic.PUBMED, new PubMedIdList());
	}

	public List<Arm> getArms() {
		return Collections.unmodifiableList(d_arms);
	}

	public void setArms(List<Arm> arms) {
		List<Arm> oldVal = d_arms;
		d_arms = arms;
		firePropertyChange(PROPERTY_ARMS, oldVal, d_arms);
	}

	public void addArm(Arm group) {
		List<Arm> newVal = new ArrayList<Arm>(d_arms);
		newVal.add(group);
		setArms(newVal);
	}
	
	public void setArmIds(List<Integer> ids) { // FIXME: for JAXB
		d_armIds = ids;
	}
	
	public List<Integer> getArmIds() { // FIXME: for JAXB
		if (d_armIds == null) {
			d_armIds = new ArrayList<Integer>();
			for (int i = 0; i < getArms().size(); ++i) {
				d_armIds.add(i);
			}
		}
		return d_armIds;
	}

	public Set<Drug> getDrugs() {
		Set<Drug> drugs = new HashSet<Drug>();
		for (Arm g : getArms()) {
			drugs.add(g.getDrug());
		}
		return drugs;
	}

	public Indication getIndication() {
		return d_indication.getValue();
	}

	public void setIndication(Indication indication) {
		Indication oldInd = d_indication.getValue();
		d_indication.setValue(indication);
		firePropertyChange(PROPERTY_INDICATION, oldInd, indication);
	}

	@Override
	public Set<Entity> getDependencies() {
		HashSet<Entity> dep = new HashSet<Entity>(getDrugs());
		dep.addAll(getOutcomeMeasures());
		dep.addAll(getPopulationCharacteristics());
		dep.add(d_indication.getValue());
		return dep;
	}

	public void setCharacteristic(BasicStudyCharacteristic c, Object val) {
		d_chars.put(c, new ObjectWithNotes<Object>(val));
		/* Beware: Every characteristicHolder attached to this study will receive this event, even though only one characteristic has changed*/
		firePropertyChange(PROPERTY_CHARACTERISTICS, c, c);
	}

	public void setCharacteristics(CharacteristicsMap m) {
		d_chars = m;
	}

	public CharacteristicsMap getCharacteristics() {
		return d_chars;
	}

	public String getStudyId() {
		return d_studyId.getValue();
	}

	public void setStudyId(String id) {
		String oldVal = d_studyId.getValue();
		d_studyId.setValue(id);
		firePropertyChange(PROPERTY_ID, oldVal, id);
	}

	@Override
	public String toString() {
		return getStudyId();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Study) {
			Study other = (Study)o;
			if (other.getStudyId() == null) {
				return getStudyId() == null;
			}
			return other.getStudyId().equals(getStudyId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getStudyId() == null ? 0 : getStudyId().hashCode();
	}

	public int compareTo(Study other) {
		return getStudyId().compareTo(other.getStudyId());
	}

	public Measurement getMeasurement(Variable v, Arm g) {
		return d_measurements.get(new MeasurementKey(v, g));
	}

	public Measurement getMeasurement(Variable v) {
		return getMeasurement(v, null);
	}

	private void forceLegalArguments(OutcomeMeasure e, Arm g, Measurement m) {
		if (!getArms().contains(g)) {
			throw new IllegalArgumentException("Arm " + g + " not part of this study.");
		}
		if (!getOutcomeMeasures().contains(e)) {
			throw new IllegalArgumentException("Outcome " + e + " not measured by this study.");
		}
		if (!m.isOfType(e.getType())) {
			throw new IllegalArgumentException("Measurement does not conform with outcome");
		}
	}

	public void setMeasurement(OutcomeMeasure e, Arm g, Measurement m) {
		forceLegalArguments(e, g, m);
		d_measurements.put(new MeasurementKey(e, g), m);
	}

	/**
	 * Set population characteristic measurement on arm.
	 * @param v
	 * @param g
	 * @param m
	 */
	public void setMeasurement(Variable v, Arm g, Measurement m) {
		forceLegalArguments(v, g, m);
		d_measurements.put(new MeasurementKey(v, g), m);
	}

	/**
	 * Set population characteristic measurement on study.
	 * @param v
	 * @param g
	 * @param m
	 */
	public void setMeasurement(Variable v, Measurement m) {
		setMeasurement(v, null, m);
	}

	private void forceLegalArguments(Variable v, Arm g, Measurement m) {
		if (!getPopulationCharacteristics().contains(v)) {
			throw new IllegalArgumentException("Variable " + v + " not in study");
		}
		if (g != null && !d_arms.contains(g)) {
			throw new IllegalArgumentException("Arm " + g + " not in study");
		}
		if (!m.isOfType(v.getType())) {
			throw new IllegalArgumentException("Measurement does not conform with outcome");
		}
	}

	public List<OutcomeMeasure> getOutcomeMeasures() {
		List<OutcomeMeasure> sortedList = new ArrayList<OutcomeMeasure>(getEndpoints());
		sortedList.addAll(getAdverseEvents());
		Collections.sort(sortedList, new OutcomeComparator());
		return sortedList;
	}

	public List<Endpoint> getEndpoints() {
		return extractVariables(d_endpoints);
	}

	private <T  extends Variable> List<T> extractVariables(List<StudyOutcomeMeasure<T>> soms) {
		List<T> vars = new ArrayList<T>();
		for (StudyOutcomeMeasure<T> som : soms) {
			vars.add(som.getValue());
		}
		return vars;
	}

	public List<AdverseEvent> getAdverseEvents() {
		return extractVariables(d_adverseEvents);
	}

	public List<PopulationCharacteristic> getPopulationCharacteristics() {
		return extractVariables(d_populationChars);
	}

	public List<? extends Variable> getVariables(Class<? extends Variable> type) {
		if (type == Endpoint.class) {
			return getEndpoints();
		} else if (type == AdverseEvent.class){
			return getAdverseEvents();
		} else if (type == OutcomeMeasure.class) {
			return Collections.unmodifiableList(getOutcomeMeasures());
		}
		return getPopulationCharacteristics(); 
	}

	private <T extends Variable> List<StudyOutcomeMeasure<T>> wrapVariables(List<T> vars) {
		List<StudyOutcomeMeasure<T>> soms = new ArrayList<StudyOutcomeMeasure<T>>();
		for (T v : vars) {
			soms.add(new StudyOutcomeMeasure<T>(v));
		}
		return soms;
	}
	
	public void setEndpoints(List<Endpoint> endpoints) {
		setEndpointsWithNotes(wrapVariables(endpoints));
	}

	private void setEndpointsWithNotes(List<StudyOutcomeMeasure<Endpoint>> endpoints) {
		List<Endpoint> oldVal = getEndpoints();
		d_endpoints = endpoints;
		firePropertyChange(PROPERTY_ENDPOINTS, oldVal, getEndpoints());
	}

	public void setAdverseEvents(List<AdverseEvent> adverseEvents) {
		setAdverseEventsWithNotes(wrapVariables(adverseEvents));
	}
	
	private void setAdverseEventsWithNotes(List<StudyOutcomeMeasure<AdverseEvent>> adverseEvents) {
		List<AdverseEvent> oldVal = getAdverseEvents();
		d_adverseEvents = adverseEvents;
		firePropertyChange(PROPERTY_ADVERSE_EVENTS, oldVal, getAdverseEvents());
	}

	public void setPopulationCharacteristics(List<PopulationCharacteristic> chars) {
		setPopulationCharacteristicsWithNotes(wrapVariables(chars));
	}

	private void setPopulationCharacteristicsWithNotes(List<StudyOutcomeMeasure<PopulationCharacteristic>> chars) {
		List<? extends Variable> oldVal = getVariables(PopulationCharacteristic.class);
		d_populationChars = chars;
		firePropertyChange(PROPERTY_POPULATION_CHARACTERISTICS, oldVal, getPopulationCharacteristics());
	}

	public void addAdverseEvent(AdverseEvent ade) {
		if (ade == null) 
			throw new NullPointerException("Cannot add a NULL outcome measure");

		List<StudyOutcomeMeasure<AdverseEvent>> newList = new ArrayList<StudyOutcomeMeasure<AdverseEvent>>(d_adverseEvents);
		newList.add(new StudyOutcomeMeasure<AdverseEvent>(ade));
		setAdverseEventsWithNotes(newList);
	}

	public void addEndpoint(Endpoint om) {
		if (om == null) 
			throw new NullPointerException("Cannot add a NULL outcome measure");

		List<StudyOutcomeMeasure<Endpoint>> newVal = new ArrayList<StudyOutcomeMeasure<Endpoint>>(d_endpoints);
		newVal.add(new StudyOutcomeMeasure<Endpoint>(om));
		setEndpointsWithNotes(newVal);
	}
	
	public void addPopulationCharacteristic(PopulationCharacteristic pc) {
		if (pc == null) 
			throw new NullPointerException("Cannot add a NULL outcome measure");

		List<StudyOutcomeMeasure<PopulationCharacteristic>> newVal = new ArrayList<StudyOutcomeMeasure<PopulationCharacteristic>>(d_populationChars);
		newVal.add(new StudyOutcomeMeasure<PopulationCharacteristic>(pc));
		setPopulationCharacteristicsWithNotes(newVal);
	}
	
	public void addOutcomeMeasure(Variable om) {
		if (om instanceof Endpoint)
			addEndpoint((Endpoint) om);
		else if (om instanceof AdverseEvent) {
			addAdverseEvent((AdverseEvent) om);
		} else if (om instanceof PopulationCharacteristic) {
			addPopulationCharacteristic((PopulationCharacteristic) om); // FIXME
		} else {
			throw new IllegalStateException("Illegal OutcomeMeasure type " + om.getClass());
		}
	}


	public void initializeDefaultMeasurements() {
		// Add default measurements for all outcomes
		for (OutcomeMeasure om : getOutcomeMeasures()) {
			for (Arm a : getArms()) {
				MeasurementKey key = new MeasurementKey(om, a);
				if (d_measurements.get(key) == null) {
					d_measurements.put(key, om.buildMeasurement(a.getSize()));
				}
			}
		}
		// Add measurements for all population characteristics
		for (Variable v : getVariables(Variable.class)) {
			MeasurementKey key = new MeasurementKey(v, null);
			if (d_measurements.get(key) == null) {
				d_measurements.put(key, v.buildMeasurement(getSampleSize()));
			}
			for (Arm g : getArms()) {
				key = new MeasurementKey(v, g);
				if (d_measurements.get(key) == null) {
					d_measurements.put(key, v.buildMeasurement(g.getSize()));
				}
			}
		}
		// Remove orphan measurements
		for (MeasurementKey k : new HashSet<MeasurementKey>(d_measurements.keySet())) {
			if (orphanKey(k)) {
				d_measurements.remove(k);
			}
		}
	}

	private boolean orphanKey(MeasurementKey k) {
		// OutcomeMeasure measurement
		if (k.d_variable instanceof OutcomeMeasure) {
			if (!getOutcomeMeasures().contains(k.d_variable)) {
				return true;
			}
			if (!d_arms.contains(k.d_arm)){
				return true;
			}
			return false;
		}
		// PopulationChar measurements
		if (k.d_variable instanceof Variable) {
			if (!getVariables(Variable.class).contains(k.d_variable)) {
				return true;
			}
			if (k.d_arm != null && !d_arms.contains(k.d_arm)) {
				return true;
			}
			return false;
		}

		throw new IllegalStateException(k + " is not a valid measurement key");
	}

	public Object getCharacteristic(Characteristic c) {
		return d_chars.get(c) != null ? d_chars.get(c).getValue() : null;
	}

	public int getSampleSize() {
		int s = 0;
		for (Arm pg : d_arms)
			s += pg.getSize();
		return s;
	}

	public void putNote(Object key, Note note) { // TODO: refactor here
		ObjectWithNotes<?> target = null;
		if (key.equals(PROPERTY_INDICATION)) {
			target = d_indication;
		} else if (key.equals(PROPERTY_ID)) {
			target = d_studyId;
		} else if (key instanceof BasicStudyCharacteristic) {
			if (d_chars.get(key) == null && note != null) {
				d_chars.put((Characteristic) key, new ObjectWithNotes<Object>(null));
			}
			target = d_chars.get(key);
		} else if (key instanceof Arm) {
			Arm arm = (Arm) key;
			arm.getNotes().clear();
			arm.getNotes().add(note);
		} else if(key instanceof Variable) {
			target = (StudyOutcomeMeasure<?>) findStudyOutcomeMeasure(key);
		} else {
			throw new IllegalArgumentException("Trying to add a note to " + key + " but this is not supported");
		}
		if (target != null) {
			target.getNotes().clear();
			if (note != null) {
				target.getNotes().add(note);
			}
		}
		firePropertyChange(PROPERTY_NOTES, key, key);
	}

	private StudyOutcomeMeasure<?> findStudyOutcomeMeasure(Object key) {
		if (key instanceof Endpoint) {
			return findStudyOutcomeMeasureInList(d_endpoints, (Endpoint)key);
		} else if (key instanceof AdverseEvent) {
			return findStudyOutcomeMeasureInList(d_adverseEvents, (AdverseEvent)key);
		} else {
			return findStudyOutcomeMeasureInList(d_populationChars, (PopulationCharacteristic)key);
		}
	}
	
	private <T extends Variable> StudyOutcomeMeasure<T> findStudyOutcomeMeasureInList(
			List<StudyOutcomeMeasure<T>> list, T om) {
		for (StudyOutcomeMeasure<T> som : list) {
			if(som.getValue().equals(om)) {
				return som;
			}
		}
		return null;
	}

	public Note getNote(Object key){
		List<Note> notes = getNotes(key);
		return (notes != null && notes.size() > 0) ? notes.get(0) : null;
	}
	
	private List<Note> getNotes(Object key) {
		if (key.equals(PROPERTY_INDICATION)) {
			return d_indication.getNotes();
		} else if (key.equals(PROPERTY_ID)) {
			return d_studyId.getNotes();
		} else if (key instanceof BasicStudyCharacteristic) {
			ObjectWithNotes<?> objectWithNotes = d_chars.get(key);
			return objectWithNotes == null ? Collections.<Note>emptyList() : objectWithNotes.getNotes();
		} else if (key instanceof Arm) {
			return ((Arm)key).getNotes();
		} else if (key instanceof Variable) {
			return findStudyOutcomeMeasure(key).getNotes();
		}
		return null;
	}

	public Map<Object,Note> getNotes() {
		Map<Object, Note> notes = new HashMap<Object, Note>();
		addNoteIfExists(PROPERTY_ID, notes);
		addNoteIfExists(PROPERTY_INDICATION, notes);
		for (Characteristic key : d_chars.keySet()) {
			addNoteIfExists(key, notes);
		}
		for (Arm arm : d_arms) {
			addNoteIfExists(arm, notes);
		}
		for (Variable om : getVariables(OutcomeMeasure.class)) {
			addNoteIfExists(om, notes);
		}
		for (Variable pc : getVariables(PopulationCharacteristic.class)) {
			addNoteIfExists(pc, notes);
		}
		return notes;
	}
	
	private void addNoteIfExists(Object key, Map<Object, Note> target) {
		if (getNote(key) != null) {
			target.put(key, getNote(key));
		}
	}
	
	private void setNotes(Map<Object,Note> notes) {
		for (Entry<Object, Note> entry : notes.entrySet()) {
			putNote(entry.getKey(), entry.getValue());
		}
	}

	public void removeEndpoint(int i) {
		ArrayList<StudyOutcomeMeasure<Endpoint>> newVal = new ArrayList<StudyOutcomeMeasure<Endpoint>>(d_endpoints);
		newVal.remove(i);
		setEndpointsWithNotes(newVal);
	}

	public Map<MeasurementKey, Measurement> getMeasurements() {
		return d_measurements;
	}

	private void setMeasurements(Map<MeasurementKey, Measurement> m) {
		d_measurements = m;
	}

	@SuppressWarnings("unchecked")
	private List<PropertyDefinition> d_propDefs = Arrays.asList(new PropertyDefinition[]{
		new PropertyDefinition<Indication>(PROPERTY_INDICATION, Indication.class) {
			public Indication getValue() { return getIndication(); }
			public void setValue(Object val) { setIndication((Indication) val); }
		},
		new PropertyDefinition<CharacteristicsMap>(PROPERTY_CHARACTERISTICS, CharacteristicsMap.class) {
			public CharacteristicsMap getValue() { return getCharacteristics(); }
			public void setValue(Object val) { setCharacteristics((CharacteristicsMap) val); }
		},
		new PropertyDefinition<ArrayList>(PROPERTY_ADVERSE_EVENTS, ArrayList.class) {
			public ArrayList<AdverseEvent> getValue() { return new ArrayList<AdverseEvent>(getAdverseEvents()); }
			public void setValue(Object val) { setAdverseEvents((ArrayList<AdverseEvent>) val); }
		},
		new PropertyDefinition<ArrayList>(PROPERTY_ENDPOINTS, ArrayList.class) {
			public ArrayList<Endpoint> getValue() { return new ArrayList<Endpoint>(getEndpoints()); }
			public void setValue(Object val) { setEndpoints((ArrayList<Endpoint>) val); }
		},
		new PropertyDefinition<ArrayList>(PROPERTY_POPULATION_CHARACTERISTICS, ArrayList.class) {
			public ArrayList<PopulationCharacteristic> getValue() { return new ArrayList<PopulationCharacteristic>(getPopulationCharacteristics()); }
			public void setValue(Object val) { setPopulationCharacteristics((ArrayList<PopulationCharacteristic>) val); }
		},
		new PropertyDefinition<ArrayList>(PROPERTY_ARMS, ArrayList.class) {
			public ArrayList<Arm> getValue() { return new ArrayList<Arm>(getArms()); }
			public void setValue(Object val) { setArms((ArrayList<Arm>) val); }
		},
		new PropertyDefinition<HashMap>("measurements", HashMap.class) {
			public HashMap getValue() { return (HashMap) getMeasurements(); }
			public void setValue(Object val) { setMeasurements((HashMap) val); }
		},
		new PropertyDefinition<HashMap>(PROPERTY_NOTES, HashMap.class) {
			public HashMap getValue() { return (HashMap) getNotes(); }
			public void setValue(Object val) { setNotes((HashMap) val); }
		}
	});
	
	protected static final XMLFormat<Study> STUDY_XML = new XMLFormat<Study>(Study.class) {
		public Study newInstance(Class<Study> cls, InputElement xml) throws XMLStreamException {
			return new Study();
		};

		@Override
		public void read(InputElement ie, Study s) throws XMLStreamException {
			s.setStudyId(ie.getAttribute(PROPERTY_ID, null));
			XMLPropertiesFormat.readProperties(ie, s.d_propDefs);
			if (s.getCharacteristic(BasicStudyCharacteristic.PUBMED) == null) {
				s.setCharacteristic(BasicStudyCharacteristic.PUBMED, new PubMedIdList());
			}
		}

		@Override
		public void write(Study s, OutputElement oe) throws XMLStreamException {
			oe.setAttribute(PROPERTY_ID, s.getStudyId());
			XMLPropertiesFormat.writeProperties(s.d_propDefs, oe);
		}
	};

	public void setMeasurement(MeasurementKey key, Measurement value) {
		d_measurements.put(key, value);
	}
}
