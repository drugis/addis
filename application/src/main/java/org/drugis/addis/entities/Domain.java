/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.common.beans.SortedSetModel;

import com.jgoodies.binding.list.ObservableList;

public abstract class Domain {
	public static final Unit GRAM = new Unit("gram", "g");
	public static final Unit LITER = new Unit("liter", "l");

	/**
	 * Get the list of top-level entity categories.
	 */
	public abstract List<EntityCategory> getCategories();
	
	/**
	 * Get the entities that belong to a category.
	 */
	public abstract ObservableList<? extends Entity> getCategoryContents(EntityCategory node);
	
	/**
	 * Get the category an entity belongs to.
	 * @param entity The entity to categorize.
	 * @return The top-level category for the entity, or null if the entity is not a top-level entity.
	 */
	public abstract EntityCategory getCategory(Entity entity);
	
	/**
	 * Get the category a type of entity belongs to.
	 * @param entityClass The class of entity to categorize.
	 * @return The top-level category for the entity class, or null if the entity class is not top-level.
	 */
	public abstract EntityCategory getCategory(Class<? extends Entity> entityClass);
	
	public abstract SortedSetModel<Drug> getDrugs();

	public abstract ObservableList<TreatmentCategorization> getTreatmentCategorizations();
	
	public abstract SortedSetModel<Indication> getIndications();

	public abstract SortedSetModel<Endpoint> getEndpoints();

	public abstract SortedSetModel<AdverseEvent> getAdverseEvents();

	public abstract SortedSetModel<PopulationCharacteristic> getPopulationCharacteristics();

	public abstract SortedSetModel<Study> getStudies();
	
	public abstract SortedSetModel<MetaAnalysis> getMetaAnalyses();

	public abstract SortedSetModel<BenefitRiskAnalysis<?>> getBenefitRiskAnalyses();
	
	public abstract SortedSetModel<Unit> getUnits();

	/**
	 * Delete a top-level entity from the domain.
	 * @param entity The entity to remove.
	 * @throws DependentEntitiesException if the entity is used by other top-level entities.
	 */
	public abstract void deleteEntity(Entity entity) throws DependentEntitiesException;
	
	/**
	 * Return whether any entities depend on this entity.
	 * @param entity
	 * @return true if this entity is being used by others.
	 */
	public abstract boolean hasDependents(Entity entity);
	
	public abstract ObservableList<MetaAnalysis> getPairWiseMetaAnalyses();
	
	public abstract ObservableList<MetaAnalysis> getNetworkMetaAnalyses();
	
	/**
	 * Get studies by Variable (Endpoint, AdverseEvent or PopulationCharacteristic).
	 * @return An unmodifiable sorted set of studies. Never null.
	 */
	public abstract ObservableList<Study> getStudies(Variable e);
	
	/**
	 * Get studies by Drug.
	 * @return An unmodifiable sorted set of studies. Never null.
	 */
	public abstract ObservableList<Study> getTreatmentDefinition(Drug d);
	
	/**
	 * Get studies by Indication.
	 * @return An unmodifiable sorted set of studies. Never null.
	 */
	public abstract ObservableList<Study> getStudies(Indication i);

}
