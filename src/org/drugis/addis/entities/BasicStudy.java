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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BasicStudy extends AbstractStudy implements MutableStudy {
	private static final long serialVersionUID = 532314508658928979L;
	
	private List<BasicPatientGroup> d_patientGroups = new ArrayList<BasicPatientGroup>();
	private transient PatientGroupListener d_pgListener;

	public BasicStudy(String id, Indication i) {
		super(id, i);	
		setEndpoints(new HashSet<Endpoint>());
		setPatientGroups(new ArrayList<BasicPatientGroup>());
		initPatientGroupListener();
	}

	private void initPatientGroupListener() {
		d_pgListener = new PatientGroupListener();
		for (PatientGroup g : d_patientGroups) {
			g.addPropertyChangeListener(d_pgListener);
		}
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		initPatientGroupListener();
	}

	public List<BasicPatientGroup> getPatientGroups() {
		return d_patientGroups;
	}

	public void setPatientGroups(List<BasicPatientGroup> patientGroups) {
		List<BasicPatientGroup> oldVal = d_patientGroups;
		for (PatientGroup g : oldVal) {
			g.removePropertyChangeListener(d_pgListener);
		}		
		d_patientGroups = patientGroups;
		updateMeasurements();		
		for (PatientGroup g : d_patientGroups) {
			g.addPropertyChangeListener(d_pgListener);
		}		
		firePropertyChange(PROPERTY_PATIENTGROUPS, oldVal, d_patientGroups);	
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
		return dep;
	}
	
	private class PatientGroupListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(PatientGroup.PROPERTY_SIZE)) {
				changeMeasurements((PatientGroup) evt.getSource(), (Integer) evt.getNewValue());
			}
		}		
	}
}