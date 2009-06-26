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

package nl.rug.escher.addis.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BasicStudy extends AbstractStudy implements MutableStudy {
	private static final long serialVersionUID = -2400136708833976982L;
	
	private List<Endpoint> d_endpoints;
	private List<BasicPatientGroup> d_patientGroups;
	
	public BasicStudy(String id) {
		super(id);
		d_measurements = new HashMap<MeasurementKey, Measurement>();		
		setEndpoints(new ArrayList<Endpoint>());
		setPatientGroups(new ArrayList<BasicPatientGroup>());
	}

	public List<Endpoint> getEndpoints() {
		return d_endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		List<Endpoint> oldVal = d_endpoints;
		d_endpoints = endpoints;
		updateMeasurements();		
		firePropertyChange(PROPERTY_ENDPOINTS, oldVal, d_endpoints);
	}

	private void updateMeasurements() {
		for (Endpoint e : d_endpoints) {
			for (PatientGroup g : d_patientGroups) {
				MeasurementKey key = new MeasurementKey(e, g);
				if (d_measurements.get(key) == null) {
					d_measurements.put(key, e.buildMeasurement());
				}
			}
		}
	}

	public List<BasicPatientGroup> getPatientGroups() {
		return d_patientGroups;
	}

	public void setPatientGroups(List<BasicPatientGroup> patientGroups) {
		List<BasicPatientGroup> oldVal = d_patientGroups;
		d_patientGroups = patientGroups;
		updateMeasurements();		
		firePropertyChange(PROPERTY_PATIENTGROUPS, oldVal, d_patientGroups);		
	}
	
	public void addPatientGroup(BasicPatientGroup group) {
		List<BasicPatientGroup> newVal = new ArrayList<BasicPatientGroup>(d_patientGroups);
		newVal.add(group);
		setPatientGroups(newVal);
	}
	
	public void addEndpoint(Endpoint endpoint) {
		List<Endpoint> newVal = new ArrayList<Endpoint>(d_endpoints);
		newVal.add(endpoint);
		setEndpoints(newVal);
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
		dep.addAll(d_endpoints);
		return dep;
	}
	
	public void setMeasurement(Endpoint e, PatientGroup g, Measurement m) {
		forceLegalArguments(e, g);
		if (!m.isOfType(e.getType())) {
			throw new IllegalArgumentException("Measurement does not conform with Endpoint");
		}
		d_measurements.put(new MeasurementKey(e, g), m);
		((BasicMeasurement)m).setSampleSize(g.getSize());
	}	
}