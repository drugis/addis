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
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.common.CollectionUtil;

import com.jgoodies.binding.beans.BeanUtils;


public class DomainTreeModel implements TreeModel {


	public static class CategoryNode {
		private final String d_singular;
		private final String d_plural;
		private final String d_property;
		private final Class<? extends Entity> d_entityClass;
		
		public CategoryNode(String singular, String plural, String propertyName,
				Class<? extends Entity> entityClass) {
			d_singular = singular;
			d_plural = plural;
			d_property = propertyName;
			d_entityClass = entityClass;
		}

		public CategoryNode(String singular, String propertyName, 
				Class<? extends Entity> entityClass) {
			this(singular, singular + "s", propertyName, entityClass);
		}
		
		public String getSingular() {
			return d_singular;
		}
		
		public String getPlural() {
			return d_plural;
		}
		
		public String toString() {
			return getPlural();
		}
		
		public String getPropertyName() {
			return d_property;
		}
		
		public Class<? extends Entity> getEntityClass() {
			return d_entityClass;
		}
	}
	
	private static final CategoryNode NODE_INDICATIONS =
		new CategoryNode("Indication", "indications", Indication.class);
	private static final CategoryNode NODE_DRUGS =
		new CategoryNode("Drug", "drugs", Drug.class);
	private static final CategoryNode NODE_ENDPOINTS =
		new CategoryNode("Endpoint", "endpoints", Endpoint.class);
	private static final CategoryNode NODE_ADVERSE_EVENTS =
		new CategoryNode("Adverse drug event", "adverseEvents", AdverseEvent.class);
	private static final CategoryNode NODE_POPULATION_CHARACTERISTICS =
		new CategoryNode("Population characteristic", "populationCharacteristics", PopulationCharacteristic.class);
	private static final CategoryNode NODE_STUDIES =
		new CategoryNode("Study", "Studies", "studies", Study.class);
	private static final CategoryNode NODE_META_ANALYSES =
		new CategoryNode("Meta-analysis", "Meta-analyses", "metaAnalyses", MetaAnalysis.class);
	private static final CategoryNode NODE_BENEFIT_RISK_ANALYSES =
		new CategoryNode("Benefit-risk analysis", "Benefit-risk analyses",
				"benefitRiskAnalyses", BenefitRiskAnalysis.class);
	
	private static final List<CategoryNode> CATEGORIES = 
		Arrays.asList(new CategoryNode[] {
			NODE_INDICATIONS,
			NODE_DRUGS,
			NODE_ENDPOINTS,
			NODE_ADVERSE_EVENTS,
			NODE_POPULATION_CHARACTERISTICS,
			NODE_STUDIES,
			NODE_META_ANALYSES,
			NODE_BENEFIT_RISK_ANALYSES
		});
	
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
		if (d_root == parent && childIndex >= 0 && childIndex < getCategories().size()) {
			return getCategories().get(childIndex);
		} else {
			for (CategoryNode cat : getCategories()) {
				if (isCategoryRequest(cat, parent, childIndex)) {
					return CollectionUtil.getElementAtIndex(getCategoryContents(cat), childIndex);
				}
			}
		}
		return null;
	}
	
	private SortedSet<?> getCategoryContents(CategoryNode node) {
		if (node == null) {
			return null;
		}
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

	private CategoryNode getCategoryNode(Object node) {
		int typeIdx = getCategories().indexOf(node);
		if (typeIdx >= 0) {
			return getCategories().get(typeIdx);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (d_root == parent) {
			return getCategories().size();
		} else {
			SortedSet<?> contents = getCategoryContents(getCategoryNode(parent));
			if (contents != null) {
				return contents.size();
			}
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == d_root) {
			return getCategories().indexOf(child);
		} else {
			SortedSet<?> contents = getCategoryContents(getCategoryNode(parent));
			if (contents != null) {
				return CollectionUtil.getIndexOfElement(contents, child);
			}
		}
		return -1;
	}

	public Object getRoot() {
		return d_root;
	}
	
	public CategoryNode getEntityCategory(Entity entity) {
		for (CategoryNode cat : getCategories()) {
			if (cat.getEntityClass().isInstance(entity)) {
				return cat;
			}
		}
		return null;
	}

	public boolean isLeaf(Object node) {
		if (node instanceof Entity) {
			CategoryNode category = getEntityCategory((Entity) node);
			if (category != null) {
				return getCategoryContents(category).contains(node);
			}
		}
		return false;
	}
	
	public TreePath getPathTo(Object node) {
		if (d_root.equals(node)) {
			return new TreePath(new Object[] { d_root });
		} else if (getCategories().contains(node)) {
			return new TreePath(new Object[] { d_root, node });
		} else if (isLeaf(node)) {
			return new TreePath(new Object[] { d_root, getEntityCategory((Entity)node), node }); 
		}
		return null;
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
		return NODE_INDICATIONS;
	}

	public Object getStudiesNode() {
		return NODE_STUDIES;
	}

	public Object getEndpointsNode() {
		return NODE_ENDPOINTS;
	}
	
	public Object getAdverseEventsNode() {
		return NODE_ADVERSE_EVENTS;
	}
	
	public Object getDrugsNode() {
		return NODE_DRUGS;
	}
	
	public Object getMetaAnalysesNode() {
		return NODE_META_ANALYSES;
	}
	
	public Object getPopulationCharacteristicsNode() {
		return NODE_POPULATION_CHARACTERISTICS;
	}
	
	public Object getBenefitRiskAnalysesNode() {
		return NODE_BENEFIT_RISK_ANALYSES;
	}

	public static List<CategoryNode> getCategories() {
		return CATEGORIES;
	}
	
}
