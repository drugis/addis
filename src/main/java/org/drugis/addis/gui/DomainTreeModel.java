/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.gui;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.common.CollectionUtil;

import com.jgoodies.binding.beans.BeanUtils;


public class DomainTreeModel implements TreeModel {
	public static class CategoryNode {
		private final String d_label;
		private final String d_property;

		public CategoryNode(String label, String property) {
			d_label = label;
			d_property = property;
		}
		
		public String toString() {
			return d_label;
		}
		
		public String getPropertyName() {
			return d_property;
		}
	}
	public static final CategoryNode[] TYPES = new CategoryNode[] {
		new CategoryNode("Indications", "indications"),
		new CategoryNode("Drugs", "drugs"),
		new CategoryNode("Endpoints", "endpoints"),
		new CategoryNode("Adverse drug events", "adverseEvents"),
		new CategoryNode("Population characteristics", "variables"),
		new CategoryNode("Studies", "studies"),
		new CategoryNode("Analyses", "metaAnalyses"),
		new CategoryNode("Benefit-risk analyses", "benefitRiskAnalyses")
	} ;
	
	public static final int INDICATIONS = 0;
	public static final int DRUGS = 1;	
	public static final int ENDPOINTS = 2;
	public static final int ADVERSE_EVENTS = 3;
	public static final int POPULATION_CHARACTERISTICS = 4;
	public static final int STUDIES = 5;
	public static final int ANALYSES = 6;
	public static final int BENEFITRISK_ANALYSIS = 7;

	private String d_root = "Database";
	private Domain d_domain;
	
	private List<TreeModelListener> d_listeners;
	
	
	private class DomainListenerImpl implements DomainListener {
		public void domainChanged(DomainEvent evt) {
			fireTreeStructureChanged();	
		}
	}
	
	public DomainTreeModel(Domain domain) {
		d_domain = domain;
		d_domain.addListener(new DomainListenerImpl());
		
		d_listeners = new ArrayList<TreeModelListener>();
	}

	public Object getChild(Object parent, int childIndex) {
		if (d_root == parent && childIndex >= 0 && childIndex < TYPES.length) {
			return TYPES[childIndex];
		} else if (isIndicationRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getIndications(), childIndex);
		} else if (isEndpointRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getEndpoints(), childIndex);
		} else if (isAdverseEventRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getAdverseEvents(), childIndex);
		} else if (isPopulationCharacteristicRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getVariables(), childIndex);
		} else if (isDrugsRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getDrugs(), childIndex);
		} else if (isStudyRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getStudies(), childIndex);
		} else if (isMetaStudyRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getMetaAnalyses(), childIndex);
		} else if (isBenefitRiskRequest(parent, childIndex)) {
			return CollectionUtil.getElementAtIndex(d_domain.getBenefitRiskAnalyses(), childIndex);
		}
		return null;
	}
	
	private SortedSet<?> getCategoryContents(CategoryNode node) {
		try {
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(
					Domain.class, node.getPropertyName());
			return (SortedSet<?>)BeanUtils.getValue(d_domain, propertyDescriptor);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isCategoryRequest(CategoryNode categoryNode, Object parent,
			int childIndex) {
		return categoryNode == parent && childIndex >= 0 && childIndex < getCategoryContents(categoryNode).size();
	}


	private boolean isIndicationRequest(Object parent, int childIndex) {
		return isCategoryRequest(TYPES[INDICATIONS], parent, childIndex);
	}

	private boolean isStudyRequest(Object parent, int childIndex) {
		return isCategoryRequest(TYPES[STUDIES], parent, childIndex);
	}
	
	private boolean isDrugsRequest(Object parent, int childIndex) {
		return isCategoryRequest(TYPES[DRUGS], parent, childIndex);
	}	

	private boolean isEndpointRequest(Object parent, int childIndex) {
		return isCategoryRequest(TYPES[ENDPOINTS], parent, childIndex);
	}
	
	private boolean isAdverseEventRequest(Object parent, int childIndex) {
		return isCategoryRequest(TYPES[ADVERSE_EVENTS], parent, childIndex);
	}
	
	private boolean isPopulationCharacteristicRequest(Object parent, int childIndex) {
		return isCategoryRequest(TYPES[POPULATION_CHARACTERISTICS], parent, childIndex);
	}
	
	private boolean isMetaStudyRequest(Object parent, int childIndex) {
		return isCategoryRequest(TYPES[ANALYSES], parent, childIndex);
	}
	
	private boolean isBenefitRiskRequest(Object parent, int childIndex) {
		return isCategoryRequest(TYPES[BENEFITRISK_ANALYSIS], parent, childIndex);
	}

	public int getChildCount(Object parent) {
		if (d_root == parent) {
			return TYPES.length;
		} else if (TYPES[INDICATIONS] == parent) {
			return d_domain.getIndications().size();
		} else if (TYPES[ENDPOINTS] == parent) {
			return d_domain.getEndpoints().size();
		} else if (TYPES[ADVERSE_EVENTS] == parent) {
			return d_domain.getAdverseEvents().size();
		} else if (TYPES[POPULATION_CHARACTERISTICS] == parent) {
			return d_domain.getVariables().size();
		} else if (TYPES[STUDIES] == parent) {
			return d_domain.getStudies().size();
		} else if (TYPES[DRUGS] == parent) {
			return d_domain.getDrugs().size();
		} else if (TYPES[ANALYSES] == parent) {
			return d_domain.getMetaAnalyses().size();
		} else if (TYPES[BENEFITRISK_ANALYSIS] == parent) {
			return d_domain.getBenefitRiskAnalyses().size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == d_root) {
			return Arrays.asList(TYPES).indexOf(child);
		} if (parent == TYPES[INDICATIONS]) {
			return CollectionUtil.getIndexOfElement(d_domain.getIndications(), child);
		} if (parent == TYPES[ENDPOINTS]) {
			return CollectionUtil.getIndexOfElement(d_domain.getEndpoints(), child);
		} if (parent == TYPES[ADVERSE_EVENTS]) {
			return CollectionUtil.getIndexOfElement(d_domain.getAdverseEvents(), child);
		} if (parent == TYPES[POPULATION_CHARACTERISTICS]) {
			return CollectionUtil.getIndexOfElement(d_domain.getVariables(), child);
		} if (parent == TYPES[STUDIES]) {
			return CollectionUtil.getIndexOfElement(d_domain.getStudies(), child);
		} if (parent == TYPES[DRUGS]) {
			return CollectionUtil.getIndexOfElement(d_domain.getDrugs(), child);			
		} if (parent == TYPES[ANALYSES]) {
			return CollectionUtil.getIndexOfElement(d_domain.getMetaAnalyses(), child);			
		} if (parent == TYPES[BENEFITRISK_ANALYSIS]) {
			return CollectionUtil.getIndexOfElement(d_domain.getBenefitRiskAnalyses(), child);			
		}
		return -1;
	}

	public Object getRoot() {
		return d_root;
	}

	public boolean isLeaf(Object node) {
		if (node instanceof Indication) {
			return d_domain.getIndications().contains(node);
		}
		if (node instanceof Endpoint) {
			return d_domain.getEndpoints().contains(node);
		}
		if (node instanceof AdverseEvent) {
			return d_domain.getAdverseEvents().contains(node);
		}		
		if (node instanceof Variable) {
			return d_domain.getVariables().contains(node);
		}		
		if (node instanceof MetaAnalysis) {
			return d_domain.getMetaAnalyses().contains(node);
		}
		if (node instanceof BenefitRiskAnalysis) {
			return d_domain.getBenefitRiskAnalyses().contains(node);
		}
		if (node instanceof Study) {
			return d_domain.getStudies().contains(node);
		}
		if (node instanceof Drug) {
			return d_domain.getDrugs().contains(node);			
		}
		return false;
	}

	public void addTreeModelListener(TreeModelListener listener) {
		if (!d_listeners.contains(listener)) {
			d_listeners.add(listener);
		}
	}
	
	public void removeTreeModelListener(TreeModelListener listener) {
		d_listeners.remove(listener);
	}

	private void fireTreeStructureChanged() {
		for (TreeModelListener l : d_listeners) {
			l.treeStructureChanged(new TreeModelEvent(this, new Object[]{d_root}));
		}
	}

	public void valueForPathChanged(TreePath path, Object node) {
	}
	
	public Object getIndicationsNode() {
		return getChild(getRoot(), DomainTreeModel.INDICATIONS);
	}

	public Object getStudiesNode() {
		return getChild(getRoot(), DomainTreeModel.STUDIES);
	}

	public Object getEndpointsNode() {
		return getChild(getRoot(), DomainTreeModel.ENDPOINTS);
	}
	
	public Object getAdverseEventsNode() {
		return getChild(getRoot(), DomainTreeModel.ADVERSE_EVENTS);
	}
	
	public Object getDrugsNode() {
		return getChild(getRoot(), DomainTreeModel.DRUGS);
	}
	
	public Object getAnalysesNode() {
		return getChild(getRoot(), DomainTreeModel.ANALYSES);
	}
	
	public Object getPopulationCharacteristicsNode() {
		return getChild(getRoot(), DomainTreeModel.POPULATION_CHARACTERISTICS);
	}
	
	public Object getBenefitRiskAnlysisNode() {
		return getChild(getRoot(), DomainTreeModel.BENEFITRISK_ANALYSIS);
	}
	
}
