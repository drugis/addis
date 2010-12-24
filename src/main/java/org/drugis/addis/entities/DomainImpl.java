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

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.DefaultListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.util.XMLHelper;

import com.jgoodies.binding.beans.BeanUtils;

public class DomainImpl implements Domain {
	private static final EntityCategory CATEGORY_INDICATIONS =
		new EntityCategory("indications", Indication.class);
	private static final EntityCategory CATEGORY_DRUGS =
		new EntityCategory("drugs", Drug.class);
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
			CATEGORY_INDICATIONS,
			CATEGORY_DRUGS,
			CATEGORY_ENDPOINTS,
			CATEGORY_ADVERSE_EVENTS,
			CATEGORY_POPULATION_CHARACTERISTICS,
			CATEGORY_STUDIES,
			CATEGORY_PAIR_WISE_META_ANALYSES,
			CATEGORY_NETWORK_META_ANALYSES,
			CATEGORY_BENEFIT_RISK_ANALYSES
		});
	
	private DomainData d_domainData;
	
	private List<DomainListener> d_listeners;
	private PropertyChangeListener d_studyListener;
	
	/**
	 * Replace the DomainData by a new instance loaded from a xml file.
	 * @param is Stream to read objects from.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadXMLDomainData(InputStream is)
	throws IOException, ClassNotFoundException {
		try {
			d_domainData = XMLHelper.fromXml(is);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		domainDataReinit();
	}
	
	/**
	 * Save the Domain to an xml stream.
	 * @param os Stream to write objects to.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void saveXMLDomainData(OutputStream os)
	throws IOException {
		try {
			String s = XMLHelper.toXml(d_domainData, DomainData.class);
			os.write(s.getBytes());
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
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
		this(new DomainData());
	}
	
	public DomainImpl(DomainData loadedData) {
		d_domainData = loadedData;
		d_listeners = new ArrayList<DomainListener>();
		d_studyListener = new StudyChangeListener();
	}

	private class StudyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireDomainChanged(DomainEvent.Type.STUDIES);
		}		
/*
 NOTE: 	Found an error "expected IllegalArgumentException but found AssertionException" from 
 		multiple sources trailing through here to Mock.
 		This was solved by moving these tests around within their classes. Suspecting bug in 
 		JUnit. 
*/
	}
	
	public void setDomainData(DomainData d) {
		d_domainData = d;
	}
	
	public DomainData getDomainData() {
		return d_domainData;
	}
	
	public void addOutcomeMeasure(OutcomeMeasure om) {
		if (om instanceof Endpoint)
			addEndpoint((Endpoint) om);
		else if (om instanceof AdverseEvent) {
			addAdverseEvent((AdverseEvent) om);
		} else {
			throw new IllegalStateException("Illegal OutcomeMeasure type " + om.getClass());
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
	
	public void addMetaAnalysis(MetaAnalysis ma) throws 
			NullPointerException, IllegalArgumentException, EntityIdExistsException {
		if (ma == null) {
			throw new NullPointerException("Meta-Study may not be null");
		}
		/*
		if (!getStudies().containsAll(ma.getStudies()))
			throw new IllegalArgumentException("Not All studies in this Meta-Study are in the domain");
		
		Indication firstInd = (Indication) ma.getStudies().get(0).getIndication();
		if (!getIndications().contains(firstInd)) {
			throw new IllegalArgumentException("Indication not in domain");
		}*/
		
		for (MetaAnalysis m : d_domainData.getMetaAnalyses()) {
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

	public ListHolder<Study> getStudies(Variable e) 
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof Domain) {
			Domain other = (Domain)o;
			return (
				getEndpoints().equals(other.getEndpoints()) &&
				getDrugs().equals(other.getDrugs()) &&
				getIndications().equals(other.getIndications()) &&
				getAdverseEvents().equals(other.getAdverseEvents()) &&
				getPopulationCharacteristics().equals(other.getPopulationCharacteristics()) &&
				getStudies().equals(other.getStudies()) &&
				getMetaAnalyses().equals(other.getMetaAnalyses())
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
		for (Study s : d_domainData.getStudies()) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}
		for (MetaAnalysis s : d_domainData.getMetaAnalyses()) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}		
		for (BenefitRiskAnalysis<?> s : d_domainData.getBenefitRiskAnalyses()) {
			if (s.getDependencies().contains(e)) {
				deps.add(s);
			}
		}		
		return deps;
	}
	
	public void deleteEntity(Entity entity) throws DependentEntitiesException {
		if (entity instanceof Drug) {
			deleteDrug((Drug) entity);
		} else if (entity instanceof Endpoint) {
			deleteEndpoint((Endpoint) entity);
		} else if (entity instanceof AdverseEvent) {
			deleteAdverseEvent((AdverseEvent) entity);
		} else if (entity instanceof PopulationCharacteristic) {
			deletePopulationCharacteristic((PopulationCharacteristic) entity);
		} else if (entity instanceof Study) {
			deleteStudy((Study) entity);
		} else if (entity instanceof MetaAnalysis) {
			deleteMetaAnalysis((MetaAnalysis) entity);
		} else if (entity instanceof MetaBenefitRiskAnalysis) {
			deleteBenefitRiskAnalysis((MetaBenefitRiskAnalysis) entity);
		} else if (entity instanceof Indication) {
			deleteIndication((Indication) entity);
		} else {
			throw new RuntimeException("Unhandled entity type " + entity.getClass().getSimpleName());
		}
	}
	
	public void deletePopulationCharacteristic(PopulationCharacteristic v) throws DependentEntitiesException {
		checkDependents(v);
		d_domainData.removeVariable(v);
		fireDomainChanged(DomainEvent.Type.VARIABLES);
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
		for (DomainListener l : new ArrayList<DomainListener>(d_listeners)) {
			l.domainChanged(new DomainEvent(type));
		}
	}

	public SortedSet<Indication> getIndications() {
		return Collections.unmodifiableSortedSet(d_domainData.getIndications());
	}

	public SortedSet<MetaAnalysis> getMetaAnalyses() {
		return Collections.unmodifiableSortedSet(d_domainData.getMetaAnalyses()); 
	}

	public void deleteMetaAnalysis(MetaAnalysis ma)
			throws DependentEntitiesException {
		checkDependents(ma);
		d_domainData.removeMetaAnalysis(ma);
		fireDomainChanged(DomainEvent.Type.ANALYSES);
	}

	public void deleteBenefitRiskAnalysis(MetaBenefitRiskAnalysis bra)
			throws DependentEntitiesException {
		checkDependents(bra);
		d_domainData.removeBRAnalysis(bra);
		fireDomainChanged(DomainEvent.Type.BENEFITRISK_ANALYSIS);
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

	public SortedSet<PopulationCharacteristic> getPopulationCharacteristics() {
		return Collections.unmodifiableSortedSet(d_domainData.getVariables());
	}

	public void addPopulationCharacteristic(PopulationCharacteristic c) {
		if (c == null) {
			throw new NullPointerException("Categorical Variable may not be null");
		}
		d_domainData.addVariable(c);
	
		fireDomainChanged(DomainEvent.Type.VARIABLES);
	}

	public ListHolder<PopulationCharacteristic> getPopulationCharacteristicsHolder() {
		return new VariablesHolder();
	}
	
	@SuppressWarnings("serial")
	private class VariablesHolder extends AbstractListHolder<PopulationCharacteristic> implements DomainListener {
		private List<PopulationCharacteristic> d_vars;
		
		public VariablesHolder() {
			d_vars = getVars();
			addListener(this);
		}

		private ArrayList<PopulationCharacteristic> getVars() {
			return new ArrayList<PopulationCharacteristic>(getPopulationCharacteristics());
		}
		
		@Override
		public List<PopulationCharacteristic> getValue() {
			return d_vars;
		}

		public void domainChanged(DomainEvent evt) {
			if (evt.getType().equals(DomainEvent.Type.VARIABLES)) {
				List<PopulationCharacteristic> old = d_vars;
				d_vars = getVars();
				fireValueChange(old, d_vars);
			}
		}
	}

	public void addAdverseEvent(AdverseEvent ade) {
		if (ade == null) {
			throw new NullPointerException();
		}
		d_domainData.addAdverseEvent(ade);
		fireDomainChanged(DomainEvent.Type.ADVERSE_EVENTS);
	}

	public void deleteAdverseEvent(AdverseEvent ade)
			throws DependentEntitiesException {
		checkDependents(ade);
		d_domainData.removeAdverseEvent(ade);
		fireDomainChanged(DomainEvent.Type.ADVERSE_EVENTS);		
	}

	public SortedSet<AdverseEvent> getAdverseEvents() {
		return Collections.unmodifiableSortedSet(d_domainData.getAdverseEvents());
	}

	public void clearDomain() {
		d_domainData = new DomainData();
		domainDataReinit();
	}

	public void addBenefitRiskAnalysis(BenefitRiskAnalysis<?> brAnalysis) {
		if (brAnalysis == null) {
			throw new NullPointerException();
		}
		d_domainData.addBenefitRiskAnalysis(brAnalysis);
		fireDomainChanged(DomainEvent.Type.BENEFITRISK_ANALYSIS);
	}

	public SortedSet<BenefitRiskAnalysis<?>> getBenefitRiskAnalyses() {
		return Collections.unmodifiableSortedSet(d_domainData.getBenefitRiskAnalyses());
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
	public SortedSet<? extends Entity> getCategoryContents(EntityCategory node) {
		if (node == null) {
			return null;
		}
		try {
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(
					Domain.class, node.getPropertyName());
			return (SortedSet<? extends Entity>)BeanUtils.getValue(this, propertyDescriptor);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ListHolder<? extends Entity> getCategoryContentsModel(
			final EntityCategory node) {
		final DefaultListHolder<? extends Entity> listHolder = new DefaultListHolder<Entity>(new ArrayList<Entity>(getCategoryContents(node)));
		addListener(new DomainListener() {
			public void domainChanged(DomainEvent evt) {
				listHolder.setValue(new ArrayList<Entity>(getCategoryContents(node)));
			}
		});
		return listHolder;
	}

	public SortedSet<NetworkMetaAnalysis> getNetworkMetaAnalyses() {
		SortedSet<NetworkMetaAnalysis> nwAnalyses = new TreeSet<NetworkMetaAnalysis>();
		for (MetaAnalysis ma : getMetaAnalyses()) {
			if (ma instanceof NetworkMetaAnalysis) {
				nwAnalyses.add((NetworkMetaAnalysis)ma);
			}
		}
		return nwAnalyses;
	}

	public SortedSet<PairWiseMetaAnalysis> getPairWiseMetaAnalyses() {
		SortedSet<PairWiseMetaAnalysis> pwAnalyses = new TreeSet<PairWiseMetaAnalysis>();
		for (MetaAnalysis ma : getMetaAnalyses()) {
			if (ma instanceof PairWiseMetaAnalysis) {
				pwAnalyses.add((PairWiseMetaAnalysis)ma);
			}
		}
		return pwAnalyses;
	}
}
