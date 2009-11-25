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

import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;

public class DomainImpl implements Domain, Serializable {
	private static final long serialVersionUID = 3222342605059458693L;
	private SortedSet<Endpoint> d_endpoints;
	private SortedSet<Study> d_studies;
	private SortedSet<RandomEffectsMetaAnalysis> d_metaAnalyses;	
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
		d_metaAnalyses = new TreeSet<RandomEffectsMetaAnalysis>();		
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

	public void addStudy(BasicStudy s) throws NullPointerException {
		if (s == null) {
			throw new NullPointerException("Study may not be null");
		}
		s.addPropertyChangeListener(d_studyListener);
		
		if (!getIndications().contains(s.getCharacteristics().get(StudyCharacteristic.INDICATION))) {
			throw new IllegalArgumentException("indication of this study not in the domain");
		}
		d_studies.add(s);
		
		fireStudiesChanged();
	}
	
	public void addMetaAnalysis(RandomEffectsMetaAnalysis ma) 
	throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		if (ma == null) {
			throw new NullPointerException("Meta-Study may not be null");
		}
		
		if (!getStudies().containsAll(ma.getStudies()))
			throw new IllegalArgumentException("Not All studies in this Meta-Study are in the domain");
		
		Indication firstInd = (Indication) ma.getStudies().get(0).getCharacteristic(StudyCharacteristic.INDICATION);
		if (!getIndications().contains(firstInd)) {
			throw new IllegalArgumentException("Indication not in domain");
		}
		
		for (RandomEffectsMetaAnalysis m : d_metaAnalyses) {
			if (m.getName().equals(ma.getName())) {
				throw new EntityIdExistsException("There already exists a meta-analysis with the given name");
			}
		}
		
		d_metaAnalyses.add(ma);
		
		fireAnalysesChanged();
	}
	
	private void fireAnalysesChanged() {
		for (DomainListener l : d_listeners) {
			l.analysesChanged();
		}
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

	public SortedSet<Study> getStudies(Endpoint e) 
	throws NullPointerException {
		if (e == null) {
			throw new NullPointerException("Endpoint must not be null");
		}
		SortedSet<Study> list = new TreeSet<Study>();
		for (Study s : d_studies) {
			if (s.getEndpoints().contains(e)) {
				list.add(s);
			}
		}
		return list;
	}
	
	public SortedSet<Study> getStudies(Drug d)
	throws NullPointerException {
			if (d == null) {
				throw new NullPointerException("Drug must not be null");
			}
		SortedSet<Study> list = new TreeSet<Study>();
		for (Study s : d_studies) {
			if (s.getDrugs().contains(d)) {
				list.add(s);
			}
		}
		return list;
	}
	
	public SortedSet<Study> getStudies(Indication i)
	throws NullPointerException {
		if (i == null) {
			throw new NullPointerException("Indication must not be null");
		}
		SortedSet<Study> set = new TreeSet<Study>();
		for (Study s : d_studies) {
			if (s.getDependencies().contains(i)) {
				set.add(s);
			}
		}
		return set;
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
		for (RandomEffectsMetaAnalysis s : d_metaAnalyses) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}		
		return deps;
	}

	public void deleteStudy(Study s) throws DependentEntitiesException {
		checkDependents(s);
		d_studies.remove(s);
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

	public SortedSet<RandomEffectsMetaAnalysis> getMetaAnalyses() {
		return Collections.unmodifiableSortedSet(d_metaAnalyses); 
	}

	public void deleteMetaAnalysis(RandomEffectsMetaAnalysis ma)
			throws DependentEntitiesException {
		checkDependents(ma);
		d_metaAnalyses.remove(ma);
		fireAnalysesChanged();
	}
}
