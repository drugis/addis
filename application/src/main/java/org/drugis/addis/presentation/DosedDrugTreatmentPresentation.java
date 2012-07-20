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

import static org.apache.commons.collections15.CollectionUtils.find;
import static org.apache.commons.collections15.CollectionUtils.forAllDo;
import static org.apache.commons.collections15.CollectionUtils.select;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.math3.util.Pair;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseDecisionTree;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.ContentAwareListModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class DosedDrugTreatmentPresentation extends PresentationModel<DosedDrugTreatment> {	
	
	private final ContentAwareListModel<CategoryNode> d_categories;
	
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
		initializeNodeMap();
	}

	private void initializeNodeMap() {
		Collection<DecisionTreeNode> children = d_tree.getChildren(d_tree.getRoot());
		for(DecisionTreeNode child : children) { 
			if(child instanceof TypeNode) {
				updateNodeMapping((TypeNode) child);
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
	
	public void resetTree() {
		d_tree.resetToDefault();
		d_nodeMap.clear();
		initializeNodeMap();
	}
	
	/**
	 * Sets the child of a node
	 * @param parent the node to set the child on
	 * @param selected the object to set as child, 
	 */
	public void setSelected(DecisionTreeNode parent, Object selected) {
		if(selected instanceof DecisionTreeNode) {
			DecisionTreeNode child = (DecisionTreeNode) selected;
			if(child instanceof LeafNode) { 
				clearNode(parent);
			} 
			setDecisionTree(parent, child);
			updateNodeMapping(child);
		} 
		updateNodeMapping(parent);
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

	private List<RangeNode> splitRange(DecisionTreeNode parent, double value, boolean includeInRightSide) {
		List<RangeNode> ranges = d_tree.splitChildRange(parent, value, includeInRightSide);

		d_nodeMap.remove(new Pair<Class<?>, String>(ranges.get(0).getBeanClass(), ranges.get(0).getPropertyName()));
		
		updateNodeMapping(ranges.get(0));
		updateNodeMapping(ranges.get(1));
		return ranges;
	}
	
	public List<RangeNode> splitRange(RangeNode node, double value, boolean includeInRightSide) {
		return splitRange(d_tree.getParent(node), value, includeInRightSide);
	}
	
	public List<RangeNode> splitKnowDoseRanges(double value, boolean includeInRightSide) {
		List<RangeNode> fixedSplits = splitRange(getType(FixedDose.class), value, includeInRightSide);
		
		DecisionTreeNode flexibleType = getType(FlexibleDose.class);
		List<RangeNode> flexibleSplits = splitRange(flexibleType, value, includeInRightSide);
		
		RangeNode left = flexibleSplits.get(0);
		RangeNode right = flexibleSplits.get(1);
		
		System.out.println("parent of left (" + left + ") = " + d_tree.getParent(left));
		System.out.println("parent of right (" + right + ") = " + d_tree.getParent(right));
		
		if(left.getPropertyName().equals(FlexibleDose.PROPERTY_MIN_DOSE)) { 
			setSelected(left, inheritPrototype(left, FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE));
			setSelected(right, inheritPrototype(right, FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE));
		} else if(left.getPropertyName().equals(FlexibleDose.PROPERTY_MAX_DOSE)) {
			setSelected(left, inheritPrototype(left, FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE));
			setSelected(right, inheritPrototype(right, FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE));
		}
		return fixedSplits;
	}

	private void updateNodeMapping(DecisionTreeNode child) {
		d_nodeMap.put(new Pair<Class<?>, String>(child.getBeanClass(), child.getPropertyName()), child);
	}

	public DecisionTreeNode getType(final Class<?> beanClass) {
		Collection<Pair<Class<?>, String>> candidates = select(d_nodeMap.keySet(), new Predicate<Pair<Class<?>, String>>() {
			public boolean evaluate(Pair<Class<?>, String> object) {
				return EqualsUtil.equal(object.getKey(), beanClass);
			}
		});
		DecisionTreeNode node = null;
		for(Pair<Class<?>, String> pair : candidates) { 
			if(d_nodeMap.get(pair) instanceof TypeNode) { 
				node = d_nodeMap.get(pair);
			}
		}
		return node; 
	}
	
	/**
	 * Removes all children of a specified node
	 * @param node 
	 */
	private void clearNode(DecisionTreeNode node) { 
		forAllDo(d_tree.getChildren(node), new Closure<DecisionTreeNode>() {
			public void execute(DecisionTreeNode orphan) {
				d_tree.removeChild(orphan);
				d_nodeMap.remove(new Pair<Class<?>, String>(orphan.getBeanClass(), orphan.getPropertyName()));
			}
		});
	}

	public Collection<DecisionTreeNode> getChildNodes(DecisionTreeNode node) {
		Collection<DecisionTreeNode> children = d_tree.getChildren(node);
		return children != null ? new TreeSet<DecisionTreeNode>(children) : Collections.<DecisionTreeNode>emptyList();
	}

	public DecisionTreeNode setKnownDoses(DecisionTreeNode prototype) {
		DecisionTreeNode fixed = getType(FixedDose.class);
		DecisionTreeNode flexible = getType(FlexibleDose.class);
		
		if(prototype instanceof LeafNode) { 
			setSelected(fixed, prototype);
			setSelected(flexible, prototype);
			return prototype;
		} else if (prototype instanceof RangeNode) {
			RangeNode protoRange = (RangeNode) prototype;
			DoseRangeNode fixedRange = inheritPrototype(protoRange, FixedDose.class, FixedDose.PROPERTY_QUANTITY);
			DoseRangeNode flexLowerRange = inheritPrototype(protoRange, FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE);
			DoseRangeNode flexUpperRange = inheritPrototype(protoRange, FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE);

			setSelected(fixed, fixedRange);
			setSelected(flexible, flexLowerRange);
			setSelected(flexLowerRange, flexUpperRange);
			
			return fixedRange;
		}
		
		throw new IllegalArgumentException("prototype is not compatible (must be a LeafNode or a RangeNode, was: " + prototype + ")");
	}
	
	public void setKnownDoses(final DecisionTreeNode parent, final Object selected) { 
		setSelected(parent, selected); // Fixed case
		if(parent instanceof RangeNode) { 
			final DecisionTreeNode prototype = inheritPrototype((RangeNode)parent, FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE); 
			final DecisionTreeNode node = find(d_tree.getVertices(), new Predicate<DecisionTreeNode>() { // this is super inefficient
				public boolean evaluate(DecisionTreeNode input) {
					return 	input.similar(prototype) &&
							input.getBeanClass().equals(prototype.getBeanClass()) && 
							input.getPropertyName().equals(prototype.getPropertyName())  ; 
				}
			});
			if(node != null) {
				setSelected(node, selected);
			} else { 
				throw new IllegalArgumentException("Leaf " + node + " did not match prototype " + prototype);
			}
		}
	}

	private DoseRangeNode inheritPrototype(RangeNode protoRange, Class<? extends AbstractDose> beanClass, String property) {
		 return new DoseRangeNode(
				beanClass, 
				property, 
				protoRange.getRangeLowerBound(), 
				protoRange.isRangeLowerBoundOpen(), 
				protoRange.getRangeUpperBound(), 
				protoRange.isRangeUpperBoundOpen(), 
				getDoseUnit());
		}
	
	public String getCategory(AbstractDose dose) { 
		return getBean().getCategory(dose).toString();
	}
}
