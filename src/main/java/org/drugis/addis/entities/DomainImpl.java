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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.ListHolder;

public class DomainImpl implements Domain {
	private DomainData d_domainData;
	
	private List<DomainListener> d_listeners;
	private PropertyChangeListener d_studyListener;
	
	/**
	 * Replace the DomainData by a new instance loaded from a stream.
	 * @param is Stream to read objects from.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadDomainData(InputStream is)
	throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(is);
		d_domainData = (DomainData)ois.readObject();
		domainDataReinit();
	}
	
	/**
	 * Save the Domain to a stream.
	 * @param os Stream to write objects to.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void saveDomainData(OutputStream os)
	throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(d_domainData);
	}
	
	private void domainDataReinit() {
		for (Study s : d_domainData.getStudies()) {
			s.addPropertyChangeListener(d_studyListener);
		}
		for (DomainEvent.Type t : DomainEvent.Type.values()) {
			fireDomainChanged(t);
		}
	}
		
	public DomainImpl() {
		d_domainData = new DomainData();
		d_listeners = new ArrayList<DomainListener>();
		d_studyListener = new StudyChangeListener();
	}
	
	private class StudyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			
			if (evt.getPropertyName().equals(Study.PROPERTY_CHARACTERISTIC))
				return; // FIXME: Why is this necessary?
			
			fireDomainChanged(DomainEvent.Type.STUDIES);
		}		
	}

	public void addEndpoint(Endpoint e) {
		if (e == null) {
			throw new NullPointerException("Endpoint may not be null");
		}
		
		d_domainData.addEnpoint(e);

		fireDomainChanged(DomainEvent.Type.ENDPOINTS);
	}

	public SortedSet<Endpoint> getEndpoints() {
		return Collections.unmodifiableSortedSet(d_domainData.getEndpoints());
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
		
		if (!getIndications().contains(s.getIndication())) {
			throw new IllegalArgumentException("indication of this study not in the domain");
		}
		d_domainData.addStudy(s);
		s.addPropertyChangeListener(d_studyListener);		
		
		fireDomainChanged(DomainEvent.Type.STUDIES);
	}
	
	public void addMetaAnalysis(RandomEffectsMetaAnalysis ma) throws 
			NullPointerException, IllegalArgumentException, EntityIdExistsException {
		if (ma == null) {
			throw new NullPointerException("Meta-Study may not be null");
		}
		
		if (!getStudies().containsAll(ma.getStudies()))
			throw new IllegalArgumentException("Not All studies in this Meta-Study are in the domain");
		
		Indication firstInd = (Indication) ma.getStudies().get(0).getIndication();
		if (!getIndications().contains(firstInd)) {
			throw new IllegalArgumentException("Indication not in domain");
		}
		
		for (RandomEffectsMetaAnalysis m : d_domainData.getMetaAnalyses()) {
			if (m.getName().equals(ma.getName())) {
				throw new EntityIdExistsException("There already exists a meta-analysis with the given name");
			}
		}
		
		d_domainData.addMetaAnalysis(ma);
	
		fireDomainChanged(DomainEvent.Type.ANALYSES);
	}
	
	public SortedSet<Study> getStudies() {
		return Collections.unmodifiableSortedSet(d_domainData.getStudies());
	}

	public void addDrug(Drug d) throws NullPointerException {
		if (d == null) {
			throw new NullPointerException("Drug may not be null");
		}
		d_domainData.addDrug(d);
	
		fireDomainChanged(DomainEvent.Type.DRUGS);
	}

	public SortedSet<Drug> getDrugs() {
		return Collections.unmodifiableSortedSet(d_domainData.getDrugs());
	}

	public ListHolder<Study> getStudies(OutcomeMeasure e) 
	throws NullPointerException {
		if (e == null) {
			throw new NullPointerException("Endpoint must not be null");
		}
		return new StudiesForEntityListHolder(e);
	}
	
	public ListHolder<Study> getStudies(Drug d)
	throws NullPointerException {
		if (d == null) {
			throw new NullPointerException("Drug must not be null");
		}
		return new StudiesForEntityListHolder(d);
	}
	
	public ListHolder<Study> getStudies(Indication i)
	throws NullPointerException {
		if (i == null) {
			throw new NullPointerException("Indication must not be null");
		}
		return new StudiesForEntityListHolder(i);
	}
	
	public ListHolder<Study> getStudiesHolder() {
		return new StudiesForEntityListHolder(null);
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
		for (Study s : d_domainData.getStudies()) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}
		for (RandomEffectsMetaAnalysis s : d_domainData.getMetaAnalyses()) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}		
		return deps;
	}

	public void deleteStudy(Study s) throws DependentEntitiesException {
		checkDependents(s);
		d_domainData.removeStudy(s);
		fireDomainChanged(DomainEvent.Type.STUDIES);
	}

	public void deleteDrug(Drug d) throws DependentEntitiesException {
		checkDependents(d);
		d_domainData.removeDrug(d);
		fireDomainChanged(DomainEvent.Type.DRUGS);
	}

	private void checkDependents(Entity d) throws DependentEntitiesException {
		Set<Entity> deps = getDependents(d);
		if (!deps.isEmpty()) {
			throw new DependentEntitiesException(deps);
		}
	}

	public void deleteEndpoint(Endpoint e) throws DependentEntitiesException {
		checkDependents(e);
		d_domainData.removeEndpoint(e);
		fireDomainChanged(DomainEvent.Type.ENDPOINTS);		
	}

	public void deleteIndication(Indication i) throws DependentEntitiesException {
		checkDependents(i);
		d_domainData.removeIndication(i);
		fireDomainChanged(DomainEvent.Type.INDICATIONS);
	}

	
	public void addIndication(Indication i) throws NullPointerException {
		if (i == null) {
			throw new NullPointerException();
		}
		d_domainData.addIndication(i);
		fireDomainChanged(DomainEvent.Type.INDICATIONS);
	}

	private void fireDomainChanged(DomainEvent.Type type) {
		for (DomainListener l : d_listeners) {
			l.domainChanged(new DomainEvent(type));
		}
	}

	public SortedSet<Indication> getIndications() {
		return Collections.unmodifiableSortedSet(d_domainData.getIndications());
	}

	public SortedSet<RandomEffectsMetaAnalysis> getMetaAnalyses() {
		return Collections.unmodifiableSortedSet(d_domainData.getMetaAnalyses()); 
	}

	public void deleteMetaAnalysis(RandomEffectsMetaAnalysis ma)
			throws DependentEntitiesException {
		checkDependents(ma);
		d_domainData.removeMetaAnalysis(ma);
		fireDomainChanged(DomainEvent.Type.ANALYSES);
	}
	
	@SuppressWarnings("serial")
	private class StudiesForEntityListHolder extends AbstractListHolder<Study> implements DomainListener {
		
		private Entity d_holderEntity;
		private List<Study> d_holderStudies;
		
		public StudiesForEntityListHolder(Entity i) {
			d_holderEntity = i;
			updateHolderStudies(i);
			addListener(this);
		}
		
		private void updateHolderStudies(Entity i) {
			List<Study> oldStudies = d_holderStudies;
			d_holderStudies = new ArrayList<Study>();
			if (i == null) {
				d_holderStudies.addAll(d_domainData.getStudies());
			} else {
				for (Study s : d_domainData.getStudies()) {
					if (s.getDependencies().contains(i)) {
						d_holderStudies.add(s);
					}
				}
			}
			firePropertyChange("value", oldStudies, d_holderStudies);
		}
		
		@Override
		public List<Study> getValue() {
			return d_holderStudies;
		}

		public void domainChanged(DomainEvent evt) {
			// FIXME: make this more intelligent, to update only when needed						
			updateHolderStudies(d_holderEntity);			
		}
	}

	public SortedSet<Variable> getVariables() {
		return Collections.unmodifiableSortedSet(d_domainData.getVariables());
	}

	public void addVariable(Variable c) {
		if (c == null) {
			throw new NullPointerException("Categorical Variable may not be null");
		}
		d_domainData.addVariable(c);
	
		fireDomainChanged(DomainEvent.Type.VARIABLES);
	}

	public ListHolder<Variable> getVariablesHolder() {
		return new VariablesHolder();
	}
	
	@SuppressWarnings("serial")
	private class VariablesHolder extends AbstractListHolder<Variable> implements DomainListener {
		private List<Variable> d_vars;
		
		public VariablesHolder() {
			d_vars = getVars();
			addListener(this);
		}

		private ArrayList<Variable> getVars() {
			return new ArrayList<Variable>(getVariables());
		}
		
		@Override
		public List<Variable> getValue() {
			return d_vars;
		}

		public void domainChanged(DomainEvent evt) {
			if (evt.getType().equals(DomainEvent.Type.VARIABLES)) {
				d_vars = getVars();
				fireValueChange(null, d_vars);
			}
		}
		
	}

	public void addAde(AdverseDrugEvent ade) {
		if (ade == null) {
			throw new NullPointerException();
		}
		d_domainData.addAde(ade);
		fireDomainChanged(DomainEvent.Type.ADES);
		
	}

	public void deleteAde(AdverseDrugEvent ade)
			throws DependentEntitiesException {
		checkDependents(ade);
		d_domainData.removeAde(ade);
		fireDomainChanged(DomainEvent.Type.ADES);		
	}

	public SortedSet<AdverseDrugEvent> getAdes() {
		return Collections.unmodifiableSortedSet(d_domainData.getAdes());
	}
}
