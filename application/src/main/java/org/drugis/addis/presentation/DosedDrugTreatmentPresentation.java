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

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseDecisionTree;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.common.beans.ContentAwareListModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class DosedDrugTreatmentPresentation extends PresentationModel<DosedDrugTreatment> {	
	
	private ContentAwareListModel<CategoryNode> d_categories;
	
	private Map<DecisionTreeNode, ValueHolder<Object>> d_selectedCategoryMap = 
			new HashMap<DecisionTreeNode, ValueHolder<Object>>(); 
	
	private final Domain d_domain;

	public DosedDrugTreatmentPresentation(DosedDrugTreatment bean) {	
		this(bean, null);
	}

	public DosedDrugTreatmentPresentation(DosedDrugTreatment bean, Domain domain) {		
		super(bean);
		d_domain = domain;
		d_categories = new ContentAwareListModel<CategoryNode>(bean.getCategories());

		Collection<DecisionTreeNode> children = bean.getDecisionTree().getChildren(bean.getRootNode());
		for(DecisionTreeNode child : children) { 
			if(child instanceof TypeNode) {
				addDoseTypeHolder((TypeNode) child);
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
	private ValueHolder<Object> addDoseTypeHolder(DecisionTreeNode node) {
		d_selectedCategoryMap.put(node, new ModifiableHolder<Object>(null));
		return d_selectedCategoryMap.get(node);
	}
	
	public ValueHolder<Object> getSelectedCategory(DecisionTreeNode node) {
		if (d_selectedCategoryMap.get(node) == null) {
			return new ModifiableHolder<Object>(null);
		}
		return d_selectedCategoryMap.get(node);
	}
	
	/**
	 * Sets the child of a node
	 * @param node the node to set the child on
	 * @param category the object to set as child, if not an DecisionTreeNode only the internal mapping is updated (@see {@link #getSelectedCategory(Class))}
	 */
	public void setSelected(DecisionTreeNode node, Object category) {
		ValueHolder<Object> selection = d_selectedCategoryMap.get(node); // Only used to maintain a state for the combo boxes
		if(selection == null) {
			selection = addDoseTypeHolder(node);
		}
		selection.setValue(category);

		if(category instanceof DecisionTreeNode) {
			DecisionTreeNode catNode = (DecisionTreeNode) category;
			getBean().getDecisionTree().setChild(node, catNode);
		}
	}

	/**
	 * Add the DosedDrugTreatment to the domain. Throws an exception if the treatment is already in the domain, throws an exception.
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
		DoseDecisionTree tree = getBean().getDecisionTree();
		
		DecisionTreeNode parent = tree.getParent(node) != null ? tree.getParent(node) : tree.getRoot();

		List<RangeNode> ranges = tree.splitChildRange(parent, value, includeInRightSide);
		
		d_selectedCategoryMap.remove(node);
		d_selectedCategoryMap.put(ranges.get(0), new ModifiableHolder<Object>(selected.getValue()));
		d_selectedCategoryMap.put(ranges.get(1), new ModifiableHolder<Object>(selected.getValue()));
		return ranges;
	}

}
