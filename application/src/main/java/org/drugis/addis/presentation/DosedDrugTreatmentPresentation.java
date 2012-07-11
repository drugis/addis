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

package org.drugis.addis.presentation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections15.CollectionUtils.*;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.math3.util.Pair;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseDecisionTree;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.EmptyNode;
import org.drugis.addis.entities.treatment.ExcludeNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.ContentAwareListModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class DosedDrugTreatmentPresentation extends PresentationModel<DosedDrugTreatment> {	
	
	public static class NamedValueHolder<T> extends ModifiableHolder<T> { 
		public NamedValueHolder(T value) {
			super(value);
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsUtil.equal(this.toString(), obj.toString());
		}
		
		@Override
		public int hashCode() {
			return this.toString().hashCode() * 31;
		}
	}
	private final ContentAwareListModel<CategoryNode> d_categories;
	private final Map<DecisionTreeNode, NamedValueHolder<Object>> d_selectedCategoryMap = 
			new HashMap<DecisionTreeNode, NamedValueHolder<Object>>(); 
	
	private final Map<Pair<Class<?>, String>, DecisionTreeNode> d_nodeMap = 
			new HashMap<Pair<Class<?>,String>, DecisionTreeNode>();
	
	private final Domain d_domain;
	private final DoseDecisionTree d_tree;

	public DosedDrugTreatmentPresentation(DosedDrugTreatment bean) {	
		this(bean, null);
	}

	public DosedDrugTreatmentPresentation(DosedDrugTreatment bean, Domain domain) {		
		super(bean);
		d_domain = domain;
		d_categories = new ContentAwareListModel<CategoryNode>(bean.getCategories());
		d_tree = getBean().getDecisionTree();

		Collection<DecisionTreeNode> children = d_tree.getChildren(bean.getRootNode());
		for(DecisionTreeNode child : children) { 
			if(child instanceof TypeNode) {
				addNodeMapping((TypeNode) child);
			}
		}
	}

	public Drug getDrug() {
		return getBean().getDrug();
	}

	public ValueHolder<String> getName() {
		return new ModifiableHolder<String>(getBean().getName());
	}

	public ObservableList<CategoryNode> getCategories() {
		return d_categories;
	}

	public DoseUnit getDoseUnit() {
		return getBean().getDoseUnit();
	}

	public void setDoseUnit(DoseUnit unit) {
		getBean().setDoseUnit(unit);
	}

	public DoseUnitPresentation getDoseUnitPresentation() {
		return new DoseUnitPresentation(getDoseUnit());
	}
	
	
	/**
	 * 
	 * @param The type to be added to the selection mapping
	 * @return The newly-created ValueHolder
	 */
	private void addNodeMapping(DecisionTreeNode node) {
		updateNodeMapping(new ModifiableHolder<Object>(new EmptyNode()), node);
	}
	
	/**
	 * @param node the node of which the selection was set
	 * @return the item previously selected by a combobox, the DefaultNode if none set
	 */
	public ValueHolder<Object> getSelectedCategory(DecisionTreeNode node) {
		if (d_selectedCategoryMap.get(node) == null) {
			return new NamedValueHolder<Object>(buildDefaultNode());
		}
		return d_selectedCategoryMap.get(node);
	}
	
	/**
	 * Sets the child of a node
	 * @param parent the node to set the child on
	 * @param selected the object to set as child, if not an DecisionTreeNode only the internal mapping is updated 
	 * @see DosedDrugTreatmentPresentation#getSelectedCategory(DecisionTreeNode)
	 */
	public void setSelected(DecisionTreeNode parent, Object selected) {
		ValueHolder<Object> current = d_selectedCategoryMap.get(parent); // Only used to maintain a state for the combo boxes
		if(current == null) {
			addNodeMapping(parent);
			current = d_selectedCategoryMap.get(parent);
		}
		if(selected instanceof DecisionTreeNode) {
			DecisionTreeNode child = (DecisionTreeNode) selected;
			setDecisionTree(parent, child);
			updateNodeMapping(current, child);
		} else { 
			updateNodeMapping(current, buildDefaultNode());
		}
		
		current.setValue(selected);
	}

	private void setDecisionTree(DecisionTreeNode parent, DecisionTreeNode child) {
		if(child instanceof LeafNode) {
			try {
				child = child.clone(); // To ensure uniqueness in the tree implementation
			} catch (Exception e) {
				throw new IllegalStateException("Tried to clone an uncopyable object " + child + " to ensure uniqueness in the DecisionTree");
			}
		}
		d_tree.setChild(parent, child);
	}

	/**
	 * Add the DosedDrugTreatment to the domain. Throws an exception if the treatment is already in the domain.
	 * Note that domain can be null in which case a null-pointer exception will occur.
	 * @return The DosedDrugTreatment that was added.
	 */
	public DosedDrugTreatment commit() {
		if (d_domain.getTreatments().contains(getBean())) {
			throw new IllegalStateException("Treatment already exists in domain");
		}
		
		d_domain.getTreatments().add(getBean());
		return getBean();
	}

	public List<RangeNode> splitRange(RangeNode node, double value, boolean includeInRightSide) {
		ValueHolder<Object> selected = getSelectedCategory(node);
		
		List<RangeNode> ranges = d_tree.splitChildRange(getBean().getDecisionTree().getParent(node), value, includeInRightSide);

		d_selectedCategoryMap.remove(node);
		d_nodeMap.remove(new Pair<Class<?>, String>(node.getBeanClass(), node.getPropertyName()));
		
		updateNodeMapping(selected, ranges.get(0));
		updateNodeMapping(selected, ranges.get(1));

		return ranges;
	}

	private void updateNodeMapping(ValueHolder<Object> selected, DecisionTreeNode child) {
		d_nodeMap.put(new Pair<Class<?>, String>(child.getBeanClass(), child.getPropertyName()), child);
		d_selectedCategoryMap.put(child, new NamedValueHolder<Object>(selected.getValue()));
	}

	/**
	 * Gets the parent node of a node with a combination of a given beanClass and propertyName
	 * @param beanClass any class, usually Class<? extends AbstractDose>
	 * @param propertyName
	 * @return the parent of the beanClass-propertyName pair if present, 
	 * otherwise the parent of the node mapped to only beanClass, the root of the tree if none present
	 */
	public DecisionTreeNode getNode(final Class<?> beanClass, final String propertyName) {
		System.out.println("Getting node for " + beanClass + " " + propertyName);
		DecisionTreeNode node = d_nodeMap.get(new Pair<Class<?>, String>(beanClass, propertyName));
		System.out.println(node != null ? "Node for " + beanClass + " " + propertyName + " is " + node :  beanClass + " " + propertyName + " has no node ");
		if(node == null) { 
			Pair<Class<?>, String> key = find(d_nodeMap.keySet(), new Predicate<Pair<Class<?>, String>>() {
				public boolean evaluate(Pair<Class<?>, String> object) {
					return EqualsUtil.equal(object.getKey(), beanClass);
				}
			});
			node = d_nodeMap.get(key);
			System.out.println(node != null ? "But did find " + key.getKey() + " " + key.getValue() : " and nothing that looks like it eiter ");

		}
		return node == null ? getBean().getRootNode() : node; // return the root of the tree otherwise
	}
	
	/**
	 * Removes all children of a specified node
	 * @param node 
	 */
	public void clearNode(DecisionTreeNode node) { 
		forAllDo(d_tree.getChildren(node), new Closure<DecisionTreeNode>() {
			public void execute(DecisionTreeNode orphan) {
				d_tree.removeChild(orphan);
				d_selectedCategoryMap.remove(orphan);
				d_nodeMap.remove(new Pair<Class<?>, String>(orphan.getBeanClass(), orphan.getPropertyName()));
			}
		});
	}

	public static DecisionTreeNode buildDefaultNode() {
		return new ExcludeNode(); 
	}
}
