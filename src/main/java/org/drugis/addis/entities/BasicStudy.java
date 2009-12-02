/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;



public class BasicStudy extends AbstractEntity implements MutableStudy {
	protected static class MeasurementKey implements Serializable {
		private static final long serialVersionUID = 6310789667384578005L;
		private Endpoint d_endpoint;
		private PatientGroup d_patientGroup;
		
		public MeasurementKey(Endpoint e, PatientGroup g) {
			d_endpoint = e;
			d_patientGroup = g;
		}
		
		public boolean equals(Object o) {
			if (o instanceof MeasurementKey) { 
				MeasurementKey other = (MeasurementKey)o;
				return d_endpoint.equals(other.d_endpoint) && d_patientGroup.equals(other.d_patientGroup);
			}
			return false;
		}
		
		public int hashCode() {
			int code = 1;
			code = code * 31 + d_endpoint.hashCode();
			code = code * 31 + d_patientGroup.hashCode();
			return code;
		}
		
		public Endpoint getEndpoint() {
			return d_endpoint;
		}
		
		public PatientGroup getPatientGroup() {
			return d_patientGroup;
		}
	}

	private static final long serialVersionUID = 532314508658928979L;
	
	private List<BasicPatientGroup> d_patientGroups = new ArrayList<BasicPatientGroup>();

	private String d_id;

	protected Map<BasicStudy.MeasurementKey, Measurement> d_measurements = new HashMap<BasicStudy.MeasurementKey, Measurement>();

	protected Set<Endpoint> d_endpoints = new HashSet<Endpoint>();

	protected StudyCharacteristicsMap d_chars = new StudyCharacteristicsMap();

	public BasicStudy(String id, Indication i) {
		d_id = id;
		d_chars.put(StudyCharacteristic.INDICATION, i);
		setEndpoints(new HashSet<Endpoint>());
		setPatientGroups(new ArrayList<BasicPatientGroup>());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
	}
	
	public List<BasicPatientGroup> getPatientGroups() {
		return d_patientGroups;
	}

	public void setPatientGroups(List<BasicPatientGroup> patientGroups) {
		List<BasicPatientGroup> oldVal = d_patientGroups;
		d_patientGroups = patientGroups;
		updateMeasurements();
		
		updateCharateristics();
		
		firePropertyChange(PROPERTY_PATIENTGROUPS, oldVal, d_patientGroups);
	}

	private void updateCharateristics() {
		if (getSampleSize() != 0)
			d_chars.put(StudyCharacteristic.STUDYSIZE, getSampleSize());
		SortedSet<Drug> drugs = new TreeSet<Drug>();		
		for (PatientGroup pg : d_patientGroups) {
			if (pg.getDrug() != null)
				drugs.add(pg.getDrug());
		}
		d_chars.put(StudyCharacteristic.DRUGS, drugs);
	}
	
	public void addPatientGroup(BasicPatientGroup group) {
		List<BasicPatientGroup> newVal = new ArrayList<BasicPatientGroup>(d_patientGroups);
		newVal.add(group);
		setPatientGroups(newVal);
	}
	
	public Set<Drug> getDrugs() {
		Set<Drug> drugs = new HashSet<Drug>();
		for (BasicPatientGroup g : getPatientGroups()) {
			drugs.add(g.getDrug());
		}
		return drugs;
	}

	public Set<Entity> getDependencies() {
		HashSet<Entity> dep = new HashSet<Entity>(getDrugs());
		dep.addAll(getEndpoints());
		dep.add((Entity) getCharacteristic(StudyCharacteristic.INDICATION));
		return dep;
	}
	
	public void setCharacteristic(StudyCharacteristic c, Object val) {
		d_chars.put(c, val);
	}
	
	public StudyCharacteristicsMap getCharacteristics() {
		return d_chars;
	}

	public String getId() {
		return d_id;
	}

	public void setId(String id) {
		String oldVal = d_id;
		d_id = id;
		firePropertyChange(PROPERTY_ID, oldVal, d_id);
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Study) {
			Study other = (Study)o;
			if (other.getId() == null) {
				return getId() == null;
			}
			return other.getId().equals(getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	public int compareTo(Study other) {
		return getId().compareTo(other.getId());
	}

	public Measurement getMeasurement(Endpoint e, PatientGroup g) {
			forceLegalArguments(e, g);
			Measurement measurement = d_measurements.get(new BasicStudy.MeasurementKey(e, g));
	//		if (measurement == null) {
				//throw new IllegalStateException("measurement null - shouldn't be!");
	//		}
			return measurement;
		}

	protected void forceLegalArguments(Endpoint e, PatientGroup g) {
		if (!getPatientGroups().contains(g)) {
			throw new IllegalArgumentException("PatientGroup " + g + " not part of this study.");
		}
		if (!getEndpoints().contains(e)) {
			throw new IllegalArgumentException("Endpoint " + e + " not measured by this study.");
		}
	}

	public void setMeasurement(Endpoint e, PatientGroup g, Measurement m) {
		forceLegalArguments(e, g);
		if (!m.isOfType(e.getType())) {
			throw new IllegalArgumentException("Measurement does not conform with Endpoint");
		}
		d_measurements.put(new BasicStudy.MeasurementKey(e, g), m);
	}

	public Set<Endpoint> getEndpoints() {
		return d_endpoints;
	}

	public void setEndpoints(Set<Endpoint> endpoints) {
		Set<Endpoint> oldVal = d_endpoints;
		d_endpoints = endpoints;
		updateMeasurements();
		firePropertyChange(PROPERTY_ENDPOINTS, oldVal, d_endpoints);
	}

	public void addEndpoint(Endpoint endpoint) {
		Set<Endpoint> newVal = new HashSet<Endpoint>(d_endpoints);
		newVal.add(endpoint);
		setEndpoints(newVal);
	}

	public void deleteEndpoint(Endpoint e) {
		if (d_endpoints.contains(e)) {
			Set<Endpoint> newVal = new HashSet<Endpoint>(d_endpoints);
			newVal.remove(e);
			setEndpoints(newVal);
		}
	}

	protected void updateMeasurements() {
		for (Endpoint e : d_endpoints) {
			for (PatientGroup g : getPatientGroups()) {
				BasicStudy.MeasurementKey key = new BasicStudy.MeasurementKey(e, g);
				if (d_measurements.get(key) == null) {
					d_measurements.put(key, e.buildMeasurement(g));
				}
			}
		}
	}

	public Object getCharacteristic(StudyCharacteristic c) {
		return d_chars.get(c);
	}

	public int getSampleSize() {
		int s = 0;
		for (PatientGroup pg : d_patientGroups)
			s += pg.getSize();
		return s;
	}
}