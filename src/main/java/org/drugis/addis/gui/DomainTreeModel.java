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

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
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
		} else {
			for (CategoryNode cat : TYPES) {
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
		int typeIdx = Arrays.asList(TYPES).indexOf(node);
		if (typeIdx >= 0) {
			return TYPES[typeIdx];
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (d_root == parent) {
			return TYPES.length;
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
			return Arrays.asList(TYPES).indexOf(child);
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

	public boolean isLeaf(Object node) {
		for (CategoryNode cat : TYPES) {
			// FIXME: TreeSet seems to have a broken contains() impl., hence the hax
			if (Arrays.asList(getCategoryContents(cat).toArray()).contains(node)) {
				return true;
			}
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
