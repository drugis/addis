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

import java.util.List;
import java.util.SortedSet;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.presentation.ListHolder;

public interface Domain {
	/**
	 * Get the list of top-level entity categories.
	 */
	public List<EntityCategory> getCategories();
	
	/**
	 * Get the entities that belong to a category.
	 */
	public SortedSet<? extends Entity> getCategoryContents(EntityCategory node);
	
	/** 
	 * Get a ValueModel to the collection of entities that belong to a category.
	 */
	public ListHolder<? extends Entity> getCategoryContentsModel(EntityCategory node);
	
	/**
	 * Get the category an entity belongs to.
	 * @param entity The entity to categorize.
	 * @return The top-level category for the entity, or null if the entity is not a top-level entity.
	 */
	public EntityCategory getCategory(Entity entity);
	
	/**
	 * Get the category a type of entity belongs to.
	 * @param entityClass The class of entity to categorize.
	 * @return The top-level category for the entity class, or null if the entity class is not top-level.
	 */
	public EntityCategory getCategory(Class<? extends Entity> entityClass);
	 
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
	 * Adds an adverse event to the data model.
	 * 
	 * @param ade the adverse event to add
	 * @throws NullPointerException if ade is null
	 */
	public void addAdverseEvent(AdverseEvent ade);
	
	/**
	 * Get the adverse events stored in the data model.
	 * @return An unmodifiable sorted set of adverse events. Never a null.
	 */
	public SortedSet<AdverseEvent> getAdverseEvents();
	
	public void addPopulationCharacteristic(PopulationCharacteristic c);
	
	public SortedSet<PopulationCharacteristic> getPopulationCharacteristics();
		
	/**
	 * Adds a study to the data model.
	 * 
	 * @param e the study to add
	 * @throws NullPointerException if e is null
	 * @throws IllegalArgumentException if indication of the study is not included in the domain yet
	 */
	public void addStudy(Study s) throws NullPointerException, IllegalArgumentException;
	
	/**
	 * Get the studies stored in the data model, EXCLUDING the meta-studies.
	 * @return An unmodifiable sorted set of studies. Never a null.
	 */
	public SortedSet<Study> getStudies();

	/**
	 * Adds a meta analysis to the data model.
	 * 
	 * @param ms the meta-study to add
	 * @throws NullPointerException if ma is null
	 * @throws IllegalArgumentException if some study within the meta-analysis is not included in the domain yet
	 * OR the indication isn't in the domain yet.
	 * @throws EntityIdExistsException if there is already an entity registered within the domain with the same ID. 
	 */
	public void addMetaAnalysis(MetaAnalysis ma) 
		throws NullPointerException, IllegalArgumentException, EntityIdExistsException;
	
	/**
	 * Get the meta-studies stored in the data model.
	 * @return An unmodifiable sorted set of meta-studies. Never a null.
	 */
	public SortedSet<MetaAnalysis> getMetaAnalyses();	
	
	/**
	 * Get the meta-studies stored in the data model.
	 * @return An unmodifiable sorted set of meta-studies. Never a null.
	 */
	public SortedSet<NetworkMetaAnalysis> getNetworkMetaAnalyses();	
	
	/**
	 * Get the meta-studies stored in the data model.
	 * @return An unmodifiable sorted set of meta-studies. Never a null.
	 */
	public SortedSet<PairWiseMetaAnalysis> getPairWiseMetaAnalyses();	
	
	/**
	 * Adds a BenefitRiskAnalysis to the data model.
	 * 
	 * @param br the BenefitRiskAnalysis to add
	 */
	public void addBenefitRiskAnalysis(BenefitRiskAnalysis<?> br);

	public SortedSet<BenefitRiskAnalysis<?>> getBenefitRiskAnalyses();
	
	/**
	 * Delete a top-level entity from the domain.
	 * @param entity The entity to remove.
	 * @throws DependentEntitiesException if the entity is used by other top-level entities.
	 */
	public void deleteEntity(Entity entity) throws DependentEntitiesException;
	
	/**
	 * Get studies by Variable (Endpoint, AdverseEvent or PopulationCharacteristic).
	 * @return An unmodifiable sorted set of studies. Never null.
	 */
	public ListHolder<Study> getStudies(Variable e);
	
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
	 * Get all population characteristics
	 * @return A ListHolder of studies.
	 */
	public ListHolder<PopulationCharacteristic> getPopulationCharacteristicsHolder();
	
	/**
	 * Add a listener to the domain object.
	 */
	public void addListener(DomainListener listener);
	
	/**
	 * Remove a listener from the domain object.
	 */
	public void removeListener(DomainListener listener);

	public void clearDomain();

	/**
	 * Return whether any entities depend on this entity.
	 * @param entity
	 * @return true if this entity is being used by others.
	 */
	public boolean hasDependents(Entity entity);
}
