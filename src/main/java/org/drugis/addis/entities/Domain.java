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

import java.util.SortedSet;

import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.presentation.ListHolder;

public interface Domain {
	/**
	 * Adds an indication to the data model.
	 * 
	 * @param i the Indication to add
	 * @throws NullPointerException if i is null
	 */
	public void addIndication(Indication i) throws NullPointerException;
	
	/**
	 * Get the indications stored in the data model.
	 * @return An unmodifiable sorted set of indications. Never a null.
	 */
	public SortedSet<Indication> getIndications();
	
	/**
	 * Adds an endpoint to the data model.
	 * 
	 * @param e the endpoint to add
	 * @throws NullPointerException if e is null
	 */
	public void addEndpoint(Endpoint e) throws NullPointerException;

	/**
	 * Get the endpoints stored in the data model.
	 * @return An unmodifiable sorted set of endpoints. Never a null.
	 */
	public SortedSet<Endpoint> getEndpoints();
		
	/**
	 * Adds a study to the data model.
	 * 
	 * @param e the study to add
	 * @throws NullPointerException if e is null
	 * @throws IllegalArgumentException if indication of the study is not included in the domain yet
	 */
	public void addStudy(BasicStudy s) throws NullPointerException, IllegalArgumentException;

	/**
	 * Adds a MetaStudy to the data model.
	 * 
	 * @param ms the meta-study to add
	 * @throws NullPointerException if ma is null
	 * @throws IllegalArgumentException if some study within the meta-analysis is not included in the domain yet
	 * OR the indication isn't in the domain yet.
	 * @throws EntityIdExistsException if there is already an entity registered within the domain with the same ID. 
	 */
	public void addMetaAnalysis(RandomEffectsMetaAnalysis ma) 
		throws NullPointerException, IllegalArgumentException, EntityIdExistsException;
	
	/**
	 * Get the studies stored in the data model, EXCLUDING the meta-studies.
	 * @return An unmodifiable sorted set of studies. Never a null.
	 */
	public SortedSet<Study> getStudies();
	
	/**
	 * Get the meta-studies stored in the data model.
	 * @return An unmodifiable sorted set of meta-studies. Never a null.
	 */
	public SortedSet<RandomEffectsMetaAnalysis> getMetaAnalyses();	
	
	
	/**
	 * Get studies by Endpoint.
	 * @return An unmodifiable sorted set of studies. Never null.
	 */
	public ListHolder<Study> getStudies(Endpoint e);
	
	/**
	 * Get studies by Drug.
	 * @return An unmodifiable sorted set of studies. Never null.
	 */
	public ListHolder<Study> getStudies(Drug d);
	
	/**
	 * Get studies by Indication.
	 * @return An unmodifiable sorted set of studies. Never null.
	 */
	public ListHolder<Study> getStudies(Indication i);
	
	/** 
	 * Get all studies
	 * @return A ListHolder of studies.
	 */
	public ListHolder<Study> getStudiesHolder();
	
	/**
	 * Adds an drug to the data model.
	 * 
	 * @param e the drug to add
	 * @throws NullPointerException if e is null
	 */
	public void addDrug(Drug d) throws NullPointerException;

	/**
	 * Get the drugs stored in the data model.
	 * @return An unmodifiable sorted set of drugs. Never a null.
	 */
	public SortedSet<Drug> getDrugs();
	
	/**
	 * Add a listener to the domain object.
	 */
	public void addListener(DomainListener listener);
	
	/**
	 * Remove a listener from the domain object.
	 */
	public void removeListener(DomainListener listener);

	/**
	 * Deletes a study from the domain.
	 * 
	 * @param s the study to delete
	 * @throws DependentEntitiesException if some entities depend on the study
	 */
	public void deleteStudy(Study s) throws DependentEntitiesException;
	
	/**
	 * Deletes a meta-analysis from the domain.
	 * 
	 * @param ma the meta-analysis to delete 
	 * @throws DependentEntitiesException if some entities depend on the meta-analysis
	 */
	public void deleteMetaAnalysis(RandomEffectsMetaAnalysis ma) throws DependentEntitiesException;
	
	/**
	 * Deletes a drug from the domain.
	 * 
	 * @param d the drug to delete 
	 * @throws DependeptEntititesException if some entities depend on this drug
	 */
	public void deleteDrug(Drug d) throws DependentEntitiesException;
	
	/**
	 * Deletes an endpoint from the domain.
	 * 
	 * @param e the endpoint to delete
	 * @throws DependentEntitiesException if some entities depend on this endpoint
	 */
	public void deleteEndpoint(Endpoint e) throws DependentEntitiesException;
	
	/**
	 * Deletes an Indication from the domain.
	 * 
	 * @param i the Indication to delete
	 * @throws DependentEntitiesException if some entities depend on this endpoint
	 */
	public void deleteIndication(Indication i) throws DependentEntitiesException;
	
	public SortedSet<CategoricalVariable> getCategoricalVariables();
	
	public void addCategoricalVariable(CategoricalVariable c);
}
