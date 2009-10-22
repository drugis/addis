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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class DomainImpl implements Domain, Serializable {
	private static final long serialVersionUID = 3222342605059458693L;
	private SortedSet<Endpoint> d_endpoints;
	private SortedSet<Study> d_studies;
	private SortedSet<MetaStudy> d_metaStudies;	
	private SortedSet<Drug> d_drugs;
	private SortedSet<Indication> d_indications;
	private transient List<DomainListener> d_listeners;
	
	private void readObject(ObjectInputStream in)
	throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		d_listeners = new ArrayList<DomainListener>();
		d_studyListener = new StudyChangeListener();
		
		for (Study s : d_studies) {
			s.addPropertyChangeListener(d_studyListener);
		}
		for (MetaStudy s : d_metaStudies) {
			s.addPropertyChangeListener(d_studyListener);
		}		
	}
	
	private class StudyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireStudiesChanged();
		}
	}
	
	private transient PropertyChangeListener d_studyListener = new StudyChangeListener();
	
	public DomainImpl() {
		d_endpoints = new TreeSet<Endpoint>();
		d_studies = new TreeSet<Study>();
		d_metaStudies = new TreeSet<MetaStudy>();		
		d_drugs = new TreeSet<Drug>();
		d_indications = new TreeSet<Indication>();
		d_listeners = new ArrayList<DomainListener>();
	}

	public void addEndpoint(Endpoint e) {
		if (e == null) {
			throw new NullPointerException("Endpoint may not be null");
		}
		
		d_endpoints.add(e);
		
		fireEndpointsChanged();
	}

	private void fireEndpointsChanged() {
		for (DomainListener l : d_listeners) {
			l.endpointsChanged();
		}
	}

	public SortedSet<Endpoint> getEndpoints() {
		return Collections.unmodifiableSortedSet(d_endpoints);
	}

	public void addListener(DomainListener listener) {
		if (!d_listeners.contains(listener)) {
			d_listeners.add(listener);
		}
	}

	public void removeListener(DomainListener listener) {
		d_listeners.remove(listener);
	}
	
	public List<DomainListener> getListeners() {
		return Collections.unmodifiableList(d_listeners);
	}

	public void addStudy(Study s) throws NullPointerException {
		if (s == null) {
			throw new NullPointerException("Study may not be null");
		}
		s.addPropertyChangeListener(d_studyListener);
		
		if (s instanceof MetaStudy) {
			d_metaStudies.add((MetaStudy)s);
		} else {
			d_studies.add(s);
		}
		
		fireStudiesChanged();
	}

	private void fireStudiesChanged() {
		for (DomainListener l : d_listeners) {
			l.studiesChanged();
		}
	}

	public SortedSet<Study> getStudies() {
		return Collections.unmodifiableSortedSet(d_studies);
	}

	public void addDrug(Drug d) throws NullPointerException {
		if (d == null) {
			throw new NullPointerException("Drug may not be null");
		}
		d_drugs.add(d);
		
		fireDrugsChanged();
	}

	private void fireDrugsChanged() {
		for (DomainListener l : d_listeners) {
			l.drugsChanged();
		}
	}

	public SortedSet<Drug> getDrugs() {
		return Collections.unmodifiableSortedSet(d_drugs);
	}

	public SortedSet<Study> getStudies(Endpoint e) {
		SortedSet<Study> list = new TreeSet<Study>();
		for (Study s : d_studies) {
			if (s.getEndpoints().contains(e)) {
				list.add(s);
			}
		}
		return list;
	}
	
	public SortedSet<Study> getStudies(Drug d) {
		SortedSet<Study> list = new TreeSet<Study>();
		for (Study s : d_studies) {
			if (s.getDrugs().contains(d)) {
				list.add(s);
			}
		}
		return list;
	}

	public boolean equals(Object o) {
		if (o instanceof Domain) {
			Domain other = (Domain)o;
			return getEndpoints().equals(other.getEndpoints()) &&
				getDrugs().equals(other.getDrugs()) &&
				getStudies().equals(other.getStudies());
		}
		
		return false;
	}
	
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getEndpoints().hashCode();
		hash = hash * 31 + getDrugs().hashCode();
		hash = hash * 31 + getStudies().hashCode();
		return hash;
	}
	
	public Set<Entity> getDependents(Entity e) {
		Set<Entity> deps = new HashSet<Entity>();
		for (Study s : d_studies) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}
		for (MetaStudy s : d_metaStudies) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}		
		return deps;
	}

	public void deleteStudy(Study s) throws DependentEntitiesException {
		checkDependents(s);
		if (s instanceof MetaStudy) {
			d_metaStudies.remove(s);
		} else {
			d_studies.remove(s);
		}
		fireStudiesChanged();
	}

	public void deleteDrug(Drug d) throws DependentEntitiesException {
		checkDependents(d);
		d_drugs.remove(d);
		fireDrugsChanged();		
	}

	private void checkDependents(Entity d) throws DependentEntitiesException {
		Set<Entity> deps = getDependents(d);
		if (!deps.isEmpty()) {
			throw new DependentEntitiesException(deps);
		}
	}

	public void deleteEndpoint(Endpoint e) throws DependentEntitiesException {
		checkDependents(e);
		d_endpoints.remove(e);
		fireEndpointsChanged();				
	}

	public void addIndication(Indication i) throws NullPointerException {
		if (i == null) {
			throw new NullPointerException();
		}
		d_indications.add(i);
		fireIndicationsChanged();
	}

	private void fireIndicationsChanged() {
		for (DomainListener l : d_listeners) {
			l.indicationsChanged();
		}
	}

	public SortedSet<Indication> getIndications() {
		return d_indications;
	}

	public SortedSet<MetaStudy> getMetaStudies() {
		return Collections.unmodifiableSortedSet(d_metaStudies); 
	}
}
