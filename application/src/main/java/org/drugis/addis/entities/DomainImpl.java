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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;
import org.drugis.common.beans.SortedSetModel;

import com.jgoodies.binding.beans.BeanUtils;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class DomainImpl extends Domain {

	private static final EntityCategory CATEGORY_UNITS =
		new EntityCategory("units", Unit.class);
	private static final EntityCategory CATEGORY_INDICATIONS =
		new EntityCategory("indications", Indication.class);
	private static final EntityCategory CATEGORY_DRUGS =
		new EntityCategory("drugs", Drug.class);
	private static final EntityCategory CATEGORY_TREATMENTCATEGORIZATIONS =
			new EntityCategory("treatmentCategorizations", TreatmentCategorization.class);
	private static final EntityCategory CATEGORY_ENDPOINTS =
		new EntityCategory("endpoints", Endpoint.class);
	private static final EntityCategory CATEGORY_ADVERSE_EVENTS =
		new EntityCategory("adverseEvents", AdverseEvent.class);
	private static final EntityCategory CATEGORY_POPULATION_CHARACTERISTICS =
		new EntityCategory("populationCharacteristics", PopulationCharacteristic.class);
	private static final EntityCategory CATEGORY_STUDIES =
		new EntityCategory("studies", Study.class);
	private static final EntityCategory CATEGORY_PAIR_WISE_META_ANALYSES =
		new EntityCategory("pairWiseMetaAnalyses", PairWiseMetaAnalysis.class);
	private static final EntityCategory CATEGORY_NETWORK_META_ANALYSES =
		new EntityCategory("networkMetaAnalyses", NetworkMetaAnalysis.class);
	private static final EntityCategory CATEGORY_BENEFIT_RISK_ANALYSES =
		new EntityCategory("benefitRiskAnalyses",
				BenefitRiskAnalysis.class);
	
	private static final List<EntityCategory> CATEGORIES = 
		Arrays.asList(new EntityCategory[] {
			CATEGORY_UNITS,
			CATEGORY_INDICATIONS,
			CATEGORY_DRUGS,
			CATEGORY_TREATMENTCATEGORIZATIONS,
			CATEGORY_ENDPOINTS,
			CATEGORY_ADVERSE_EVENTS,
			CATEGORY_POPULATION_CHARACTERISTICS,
			CATEGORY_STUDIES,
			CATEGORY_PAIR_WISE_META_ANALYSES,
			CATEGORY_NETWORK_META_ANALYSES,
			CATEGORY_BENEFIT_RISK_ANALYSES
		});
	
	private class DomainSortedSetModel<E extends Entity> extends SortedSetModel<E> {
		@Override
		public void add(int index, E element) {
			if (element == null) {
				throw new NullPointerException("Entity added to the Domain may not be null");
			}
			if (contains(element)) {
				throw new EntityIdExistsException(element.getLabel());
			}
			checkDependencies(element);
		
			super.add(index, element);
		};
		
		@Override
		public E remove(int index) {
			checkDependents(get(index));
			return super.remove(index);
		}
		
		@Override
		public boolean remove(Object o) {
			if (o instanceof Entity) {
				checkDependents((Entity)o);
			}
			return super.remove(o);
		}
	}
	
	private SortedSetModel<Endpoint> d_endpoints = new DomainSortedSetModel<Endpoint>();
	private SortedSetModel<Study> d_studies = new DomainSortedSetModel<Study>();
	private SortedSetModel<MetaAnalysis> d_metaAnalyses = new DomainSortedSetModel<MetaAnalysis>();		
	private SortedSetModel<Drug> d_drugs = new DomainSortedSetModel<Drug>();
	private ObservableList<TreatmentCategorization> d_treatments = new ArrayListModel<TreatmentCategorization>();

	private SortedSetModel<Indication> d_indications = new DomainSortedSetModel<Indication>();	
	private SortedSetModel<Unit> d_units = new DomainSortedSetModel<Unit>();
	private SortedSetModel<PopulationCharacteristic> d_populationCharacteristics = new DomainSortedSetModel<PopulationCharacteristic>();
	private SortedSetModel<AdverseEvent> d_adverseEvents = new DomainSortedSetModel<AdverseEvent>();
	private SortedSetModel<BenefitRiskAnalysis<?>> d_benefitRiskAnalyses = new DomainSortedSetModel<BenefitRiskAnalysis<?>>();
	private FilteredObservableList<MetaAnalysis> d_networkMetaAnalyses;
	private FilteredObservableList<MetaAnalysis> d_pairWiseMetaAnalyses;
	
	public DomainImpl() {
		d_pairWiseMetaAnalyses = new FilteredObservableList<MetaAnalysis>(getMetaAnalyses(), new FilteredObservableList.Filter<MetaAnalysis>() {
			public boolean accept(MetaAnalysis obj) {
				return obj instanceof PairWiseMetaAnalysis;
			}
		});
		d_networkMetaAnalyses = new FilteredObservableList<MetaAnalysis>(getMetaAnalyses(), new FilteredObservableList.Filter<MetaAnalysis>() {
			public boolean accept(MetaAnalysis obj) {
				return obj instanceof NetworkMetaAnalysis;
			}
		});
		d_units.add(GRAM);
		d_units.add(LITER);
	}
	
	public void addOutcomeMeasure(OutcomeMeasure om) {
		if (om instanceof Endpoint)
			getEndpoints().add(((Endpoint) om));
		else if (om instanceof AdverseEvent) {
			getAdverseEvents().add(((AdverseEvent) om));
		} else {
			throw new IllegalStateException("Illegal OutcomeMeasure type " + om.getClass());
		}
	}

	public ObservableList<Study> getStudies(Variable e) 
	throws NullPointerException {
		if (e == null) {
			throw new NullPointerException("Variable must not be null");
		}
		if (e instanceof Endpoint) {
			return new FilteredObservableList<Study>(getStudies(), new EndpointFilter((Endpoint)e));
		}
		if (e instanceof AdverseEvent) {
			return new FilteredObservableList<Study>(getStudies(), new AdverseEventFilter((AdverseEvent)e));
		}
		if (e instanceof PopulationCharacteristic) {
			return new FilteredObservableList<Study>(getStudies(), new PopulationCharacteristicFilter((PopulationCharacteristic)e));
		}
		throw new RuntimeException(e.getClass() + " not supported");
	}
	
	public ObservableList<Study> getStudies(Drug d) {
		return new FilteredObservableList<Study>(getStudies(), new DrugFilter(TreatmentDefinition.createTrivial(d)));
	}
	
	public ObservableList<Study> getStudies(Indication i) {
		return new FilteredObservableList<Study>(getStudies(), new IndicationFilter(i));
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Domain) {
			Domain other = (Domain)o;
			return (
				getEndpoints().equals(other.getEndpoints()) &&
				getDrugs().equals(other.getDrugs()) &&
				getTreatmentCategorizations().equals(other.getTreatmentCategorizations()) &&
				getIndications().equals(other.getIndications()) &&
				getAdverseEvents().equals(other.getAdverseEvents()) &&
				getPopulationCharacteristics().equals(other.getPopulationCharacteristics()) &&
				getStudies().equals(other.getStudies()) &&
				getMetaAnalyses().equals(other.getMetaAnalyses()) &&
				getBenefitRiskAnalyses().equals(other.getBenefitRiskAnalyses())
			);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getEndpoints().hashCode();
		hash = hash * 31 + getDrugs().hashCode();
		hash = hash * 31 + getStudies().hashCode();
		return hash;
	}
	
	public Set<Entity> getDependents(Entity e) {
		Set<Entity> deps = new HashSet<Entity>();
		for (Study s : getStudies().getSet()) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}
		for (MetaAnalysis s : getMetaAnalyses().getSet()) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}		
		for (BenefitRiskAnalysis<?> s : getBenefitRiskAnalyses().getSet()) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}		
		return deps;
	}
	
	public void deleteEntity(Entity entity) throws DependentEntitiesException {
		if (entity instanceof Drug) {
			getDrugs().remove(((Drug) entity));
		} else if (entity instanceof TreatmentCategorization) {
			getTreatmentCategorizations().remove(((TreatmentCategorization) entity));
		} else if (entity instanceof Endpoint) {
			getEndpoints().remove(((Endpoint) entity));
		} else if (entity instanceof AdverseEvent) {
			getAdverseEvents().remove(((OutcomeMeasure) entity));
		} else if (entity instanceof PopulationCharacteristic) {
			getPopulationCharacteristics().remove(((PopulationCharacteristic) entity));
		} else if (entity instanceof Study) {
			getStudies().remove(((Study) entity));
		} else if (entity instanceof MetaAnalysis) {
			getMetaAnalyses().remove(((MetaAnalysis) entity));
		} else if (entity instanceof MetaBenefitRiskAnalysis) {
			getBenefitRiskAnalyses().remove(((MetaBenefitRiskAnalysis) entity));
		} else if (entity instanceof StudyBenefitRiskAnalysis) {
			getBenefitRiskAnalyses().remove(((StudyBenefitRiskAnalysis) entity));
		} else if (entity instanceof Indication) {
			getIndications().remove(((Indication) entity));
		} else if (entity instanceof Unit) {
			getUnits().remove(((Unit) entity));
		} else {
			throw new RuntimeException("Unhandled entity type " + entity.getClass().getSimpleName());
		}
	}
	
	/**
	 * Checks whether an entity is being depended upon by other entities currently in the Domain.
	 * @param d The entity to check for.
	 * @throws DependentEntitiesException if the entity is being depended upon.
	 */
	private void checkDependents(Entity d) throws DependentEntitiesException {
		Set<Entity> deps = getDependents(d);
		if (!deps.isEmpty()) {
			throw new DependentEntitiesException(deps);
		}
	}

	/**
	 * Checks whether an entity has unsatisfied dependencies.
	 * @param d The entity to check for.
	 * @throws EntityMissingDependenciesException if the entity has missing dependencies.
	 */
	private void checkDependencies(Entity d) throws EntityMissingDependenciesException {
		List<Entity> unsatisfied = new ArrayList<Entity>();
		for (Entity e : d.getDependencies()) {
			EntityCategory c = getCategory(e);
			if (!getCategoryContents(c).contains(e)) {
					unsatisfied.add(e);
			}
		}
		if (!unsatisfied.isEmpty()) {
			throw new EntityMissingDependenciesException(unsatisfied);
		}
	}
	
	public boolean hasDependents(Entity entity) {
		return !getDependents(entity).isEmpty();
	}

	public List<EntityCategory> getCategories() {
		return CATEGORIES;
	}

	public EntityCategory getCategory(Entity entity) {
		return getCategory(entity.getClass());
	}

	public EntityCategory getCategory(Class<? extends Entity> entityClass) {
		for (EntityCategory cat : getCategories()) {
			if (cat.getEntityClass().isAssignableFrom(entityClass)) {
				return cat;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ObservableList<? extends Entity> getCategoryContents(EntityCategory node) {
		if (node == null) {
			return null;
		}
		try {
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(
					Domain.class, node.getPropertyName());
			return (ObservableList<? extends Entity>)BeanUtils.getValue(this, propertyDescriptor);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public SortedSetModel<Drug> getDrugs() {
		return d_drugs;
	}
	
	@Override
	public ObservableList<TreatmentCategorization> getTreatmentCategorizations() {
		return d_treatments;
	}

	@Override
	public SortedSetModel<Indication> getIndications() {
		return d_indications;
	}

	@Override
	public SortedSetModel<AdverseEvent> getAdverseEvents() {
		return d_adverseEvents;
	}

	@Override
	public SortedSetModel<Endpoint> getEndpoints() {
		return d_endpoints;
	}

	@Override
	public SortedSetModel<PopulationCharacteristic> getPopulationCharacteristics() {
		return d_populationCharacteristics;
	}

	@Override
	public SortedSetModel<Study> getStudies() {
		return d_studies;
	}

	@Override
	public SortedSetModel<MetaAnalysis> getMetaAnalyses() {
		return d_metaAnalyses;
	}

	@Override
	public ObservableList<MetaAnalysis> getPairWiseMetaAnalyses() {
		return d_pairWiseMetaAnalyses;
	}
	
	@Override
	public ObservableList<MetaAnalysis> getNetworkMetaAnalyses() {
		return d_networkMetaAnalyses;
	}

	@Override
	public SortedSetModel<BenefitRiskAnalysis<?>> getBenefitRiskAnalyses() {
		return d_benefitRiskAnalyses;
	}
	
	@Override
	public SortedSetModel<Unit> getUnits() {
		return d_units;
	}

	public static class EndpointFilter implements Filter<Study> {
		private Endpoint d_endpoint;

		public EndpointFilter(Endpoint e) {
			d_endpoint = e;
		}

		public boolean accept(Study s) {
			return Study.extractVariables(s.getEndpoints()).contains(d_endpoint);
		}
	}
	public static class AdverseEventFilter implements Filter<Study> {
		private AdverseEvent d_adverseEvent;

		public AdverseEventFilter(AdverseEvent ade) {
			d_adverseEvent = ade;
		}

		public boolean accept(Study s) {
			return Study.extractVariables(s.getAdverseEvents()).contains(d_adverseEvent);
		}
	}
	public static class PopulationCharacteristicFilter implements Filter<Study> {
		private PopulationCharacteristic d_popChar;

		public PopulationCharacteristicFilter(PopulationCharacteristic e) {
			d_popChar = e;
		}

		public boolean accept(Study s) {
			return Study.extractVariables(s.getPopulationChars()).contains(d_popChar);
		}
	}
	public static class IndicationFilter implements Filter<Study> {
		private final Indication d_indication;

		public IndicationFilter(Indication indication) {
			d_indication = indication;
		}

		public boolean accept(Study s) {
			return s.getIndication().equals(d_indication);
		}
	}
	public class DrugFilter implements Filter<Study> {
		private final TreatmentDefinition d_TreatmentDefinition;
		
		public DrugFilter(TreatmentDefinition ds) {
			d_TreatmentDefinition = ds;
		}
		
		public boolean accept(Study s) {
			return s.getDrugs().contains(d_TreatmentDefinition);
		}
	}
}