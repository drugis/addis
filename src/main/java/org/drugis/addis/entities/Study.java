/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.drugis.common.EqualsUtil;

public class Study extends AbstractEntity implements Comparable<Study>, Entity {

	public static class MeasurementKey extends AbstractEntity implements Entity {

		private Entity d_outcomeM;
		private Arm d_arm;

		public MeasurementKey() {
		}

		public MeasurementKey(Entity e, Arm g)  {
			if (e == null) {
				throw new NullPointerException("Variable/Outcome = " + e + " may not be null");
			}
			if (!(e instanceof Variable) && g == null) {
				throw new NullPointerException("Arm = " + g + " may not be null for Endpoints/ADEs");
			}
			d_outcomeM = e;
			d_arm = g;
		}

		public Entity getOutcomeM() {
			return d_outcomeM;
		}
		public void setOutcomeM(Entity outcomeM) {
			d_outcomeM = outcomeM;
		}
		public Arm getArm() {
			return d_arm;
		}
		public void setArm(Arm arm) {
			d_arm = arm;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof MeasurementKey) { 
				MeasurementKey other = (MeasurementKey)o;
				return d_outcomeM.equals(other.d_outcomeM) && EqualsUtil.equal(d_arm, other.d_arm);
			}
			return false;
		}

		@Override
		public int hashCode() {
			int code = 1;
			code = code * 31 + d_outcomeM.hashCode();
			code = code * 31 + (d_arm == null ? 0 : d_arm.hashCode());
			return code;
		}

		@Override
		public Set<? extends Entity> getDependencies() {
			return null;
		}
	}

	public final static String PROPERTY_ID = "studyId";
	public final static String PROPERTY_ENDPOINTS = "endpoints";
	public final static String PROPERTY_ADVERSE_EVENTS = "adverseEvents";
	public final static String PROPERTY_POPULATION_CHARACTERISTICS = "populationCharacteristics";
	public final static String PROPERTY_ARMS = "arms";
	public final static String PROPERTY_CHARACTERISTIC = "Characteristics";
	public final static String PROPERTY_NOTE = "Note";
	public final static String PROPERTY_INDICATION = "indication";

	private List<Arm> d_arms = new ArrayList<Arm>();
	private String d_studyId;
	private Map<MeasurementKey, Measurement> d_measurements = new HashMap<MeasurementKey, Measurement>();
	private List<Endpoint> d_endpointList = new ArrayList<Endpoint>();
	private List<AdverseEvent> d_adverseEvents = new ArrayList<AdverseEvent>();
	private List<PopulationCharacteristic> d_populationChars = new ArrayList<PopulationCharacteristic>();
	private CharacteristicsMap d_chars = new CharacteristicsMap();
	private Indication d_indication;
	private Map<Object, Note> d_notes = new HashMap<Object, Note>();

	public Study (){
	}

	public Study(String id, Indication i) {
		d_studyId = id;
		d_indication = i;
		setArms(new ArrayList<Arm>());
	}

	private void readObject(ObjectInputStream in) 
	throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

	public List<Arm> getArms() {
		return d_arms;
	}

	public void setArms(List<Arm> arms) {
		List<Arm> oldVal = d_arms;
		d_arms = arms;
		updateMeasurements();

		firePropertyChange(PROPERTY_ARMS, oldVal, d_arms);
	}

	public void addArm(Arm group) {
		List<Arm> newVal = new ArrayList<Arm>(d_arms);
		newVal.add(group);
		setArms(newVal);
	}

	public Set<Drug> getDrugs() {
		Set<Drug> drugs = new HashSet<Drug>();
		for (Arm g : getArms()) {
			drugs.add(g.getDrug());
		}
		return drugs;
	}

	public Indication getIndication() {
		return d_indication;
	}

	public void setIndication(Indication indication) {
		Indication oldInd = d_indication;
		d_indication = indication;
		firePropertyChange(PROPERTY_INDICATION, oldInd, indication);
	}

	@Override
	public Set<Entity> getDependencies() {
		HashSet<Entity> dep = new HashSet<Entity>(getDrugs());
		dep.addAll(getOutcomeMeasures());
		dep.addAll(getPopulationCharacteristics());
		dep.add(d_indication);
		return dep;
	}

	public void setCharacteristic(BasicStudyCharacteristic c, Object val) {
		d_chars.put(c, val);
		/* Beware: Every characteristicHolder attached to this study will receive this event, even though only one characteristic has changed*/
		firePropertyChange(PROPERTY_CHARACTERISTIC, c, c);
	}

	public CharacteristicsMap setCharacteristics() {
		throw new IllegalAccessError("Can't set characteristics map directly.");
	}

	public void setCharacteristics(CharacteristicsMap m) {
		d_chars = m;
	}

	public CharacteristicsMap getCharacteristics() {
		return d_chars;
	}

	public String getStudyId() {
		return d_studyId;
	}

	public void setStudyId(String id) {
		String oldVal = d_studyId;
		d_studyId = id;
		firePropertyChange(PROPERTY_ID, oldVal, d_studyId);
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
		if (!d_populationChars.contains(v)) {
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
		List<OutcomeMeasure> l = new ArrayList<OutcomeMeasure>();
		l.addAll(d_endpointList);
		l.addAll(d_adverseEvents);
		return l;
	}

	public List<Endpoint> getEndpoints() {
		return Collections.unmodifiableList(d_endpointList);
	}

	public List<AdverseEvent> getAdverseEvents() {
		return Collections.unmodifiableList(d_adverseEvents);
	}

	public List<PopulationCharacteristic> getPopulationCharacteristics() {
		return Collections.unmodifiableList(d_populationChars);
	}

	public List<? extends Variable> getVariables(Class<? extends Variable> type) {
		if (type == Endpoint.class) {
			return Collections.unmodifiableList(d_endpointList);
		} else if (type == AdverseEvent.class){
			return Collections.unmodifiableList(d_adverseEvents);
		} else if (type == OutcomeMeasure.class) {
			List<Variable> varList = new ArrayList<Variable>();
			varList.addAll(d_endpointList);
			varList.addAll(d_adverseEvents);
			return Collections.unmodifiableList(varList);
		}		
		return Collections.unmodifiableList(d_populationChars); 
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		List<Endpoint> oldVal = getEndpoints();
		d_endpointList = new ArrayList<Endpoint>(endpoints);
		updateMeasurements();
		firePropertyChange(PROPERTY_ENDPOINTS, oldVal, getEndpoints());
	}

	public void setAdverseEvents(List<AdverseEvent> ade) {
		List<AdverseEvent> oldVal = getAdverseEvents();
		d_adverseEvents = new ArrayList<AdverseEvent>(ade);
		updateMeasurements();
		firePropertyChange(PROPERTY_ADVERSE_EVENTS, oldVal, getAdverseEvents());
	}

	public void setPopulationCharacteristics(List<PopulationCharacteristic> chars) {
		List<? extends Variable> oldVal = getVariables(PopulationCharacteristic.class);
		d_populationChars = new ArrayList<PopulationCharacteristic>(chars);
		updateMeasurements();
		firePropertyChange(PROPERTY_POPULATION_CHARACTERISTICS, oldVal, getVariables(PopulationCharacteristic.class));
	}

	public void addAdverseEvent(AdverseEvent ade) {
		if (ade == null) 
			throw new NullPointerException("Cannot add a NULL outcome measure");

		List<AdverseEvent> newList = new ArrayList<AdverseEvent>(d_adverseEvents);
		newList.add(ade);
		setAdverseEvents(newList);
	}

	public void addOutcomeMeasure(OutcomeMeasure om) {
		if (om instanceof Endpoint)
			addEndpoint((Endpoint) om);
		else if (om instanceof AdverseEvent) {
			addAdverseEvent((AdverseEvent) om);
		} else {
			throw new IllegalStateException("Illegal OutcomeMeasure type " + om.getClass());
		}
	}

	public void addEndpoint(Endpoint om) {
		if (om == null) 
			throw new NullPointerException("Cannot add a NULL outcome measure");

		List<Endpoint> newVal = new ArrayList<Endpoint>(d_endpointList);
		newVal.add(om);
		setEndpoints(newVal);
	}

	public void deleteOutcomeMeasure(Endpoint om) {
		deleteEndpoint(om);
	}

	public void deleteEndpoint(Endpoint om) {
		if (d_endpointList.contains(om)) {
			List<Endpoint> newVal = new ArrayList<Endpoint>(d_endpointList);
			newVal.remove(om);
			setEndpoints(newVal);
		}
	}

	private void updateMeasurements() {
		// Add default measurements for all outcomes
		for (OutcomeMeasure om : getOutcomeMeasures()) {
			for (Arm g : getArms()) {
				MeasurementKey key = new MeasurementKey(om, g);
				if (d_measurements.get(key) == null) {
					d_measurements.put(key, om.buildMeasurement(g.getSize()));
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
		// OutcomeMeasure measurements
		if (k.d_outcomeM instanceof OutcomeMeasure) {
			if (!getOutcomeMeasures().contains(k.d_outcomeM)) {
				return true;
			}
			if (!d_arms.contains(k.d_arm)){
				return true;
			}
			return false;
		}
		// PopulationChar measurements
		if (k.d_outcomeM instanceof Variable) {
			if (!getVariables(Variable.class).contains(k.d_outcomeM)) {
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
		return d_chars.get(c);
	}

	public int getSampleSize() {
		int s = 0;
		for (Arm pg : d_arms)
			s += pg.getSize();
		return s;
	}

	public void putNote(Object key, Note note){
		d_notes.put(key, note);
		firePropertyChange(PROPERTY_NOTE, key, key);
	}

	public Note getNote(Object key){
		return d_notes.get(key);
	}

	public Map<Object,Note> getNotes() {
		return d_notes;
	}
	public void setNotes(Map<Object,Note> notes) {
		d_notes = notes;
	}

	public void removeNote (Object key){
		d_notes.remove(key);
	}

	public void removeEndpoint(int i) {
		List<Endpoint> newVal = new ArrayList<Endpoint>(d_endpointList);
		newVal.remove(i);
		setEndpoints(newVal);
	}

	public Map<MeasurementKey, Measurement> getMeasurements() {
		return d_measurements;
	}

	public void setMeasurements(Map<MeasurementKey, Measurement> m) {
		d_measurements = m;
	}

	@Override
	public String[] getXmlExclusions() {
		return new String[] {"sampleSize", "outcomeMeasures", "drugs"};
	}
}
