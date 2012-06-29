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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.ExcludeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class DosedDrugTreatmentPresentation extends PresentationModel<DosedDrugTreatment> {	
	public class DecisionTreeCoordinate {
		final public Class<? extends AbstractDose> beanClass;
		final public String propertyName;
		final public int index;
		
		public DecisionTreeCoordinate(Class<? extends AbstractDose> beanClass, String property, int rangeIdx) {
			this.beanClass = beanClass;
			this.propertyName = property;
			this.index = rangeIdx; 
		}
		
		@Override
		public int hashCode() {
			return beanClass.hashCode() + 31 * (propertyName == null ? 0 : propertyName.hashCode()) + 31 * 31 * index;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof DecisionTreeCoordinate) { 
				DecisionTreeCoordinate other = (DecisionTreeCoordinate) obj;
				return EqualsUtil.equal(other.propertyName, propertyName) && EqualsUtil.equal(other.beanClass, beanClass) &&
						other.index == index;
			}
			return false;
		}
	}
	
	private ContentAwareListModel<CategoryNode> d_categories;
	private Map<DecisionTreeCoordinate, DecisionTreeNode> d_nodeMap = 
			new HashMap<DecisionTreeCoordinate, DecisionTreeNode>(); 
	
	private Map<DecisionTreeCoordinate, ValueHolder<Object>> d_selectedCategoryMap = 
			new HashMap<DecisionTreeCoordinate, ValueHolder<Object>>(); 
	
	private final Domain d_domain;
	private ExcludeNode d_excludeNode = new ExcludeNode();

	public DosedDrugTreatmentPresentation(DosedDrugTreatment bean) {	
		this(bean, null);
	}

	public DosedDrugTreatmentPresentation(DosedDrugTreatment bean, Domain domain) {		
		super(bean);
		d_domain = domain;
		d_categories = new ContentAwareListModel<CategoryNode>(bean.getCategories());
		if(bean.getRootNode() instanceof TypeNode) {
			TypeNode root = (TypeNode) bean.getRootNode();
			for(Class<? extends AbstractDose> type : root.getTypes()) {
				d_nodeMap.put(new DecisionTreeCoordinate(type, null, 0), bean.getRootNode());
			}
		}
	}

	/**
	 * 
	 * @param The type to be added to the selection mapping
	 * @return The newly-created ValueHolder
	 */
	private ValueHolder<Object> addDoseTypeHolder(DecisionTreeCoordinate dosePropertyPair) {
		d_selectedCategoryMap.put(dosePropertyPair, new ModifiableHolder<Object>(d_excludeNode));
		return d_selectedCategoryMap.get(dosePropertyPair);
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
	 * @return null if Fixed and flexible categorizations differ, the selected category ValueHolder otherwise
	 */
	public ValueHolder<Object> getSelectedKnownCategory() { 
		if ((getSelectedCategory(FixedDose.class).getValue()).equals(getSelectedCategory(FlexibleDose.class).getValue())) { 
			return getSelectedCategory(FlexibleDose.class);
		}
		return new UnmodifiableHolder<Object>(d_excludeNode);
	}
	
	public ValueHolder<Object> getSelectedCategory(Class<? extends AbstractDose> type) { 	
		return getSelectedCategory(type, null, 0);
	}
	
	public ValueHolder<Object> getSelectedCategory(Class<? extends AbstractDose> type, String property) {
		return getSelectedCategory(type, property, 0);
	}

	public ValueHolder<Object> getSelectedCategory(Class<? extends AbstractDose> type, String property, int rangeIdx) {
		DecisionTreeCoordinate coordinate = new DecisionTreeCoordinate(type, property, rangeIdx);
		if (d_selectedCategoryMap.get(coordinate ) == null) {
			return addDoseTypeHolder(coordinate);
		}
		return d_selectedCategoryMap.get(coordinate);
	}
	
	public Collection<ValueHolder<Object>> getSelectedCategories() { 
		return Collections.unmodifiableCollection(d_selectedCategoryMap.values());
	}
	
	
	/**
	 * Sets the child of a node
	 * @param node the node to set the child on, if not a RangeNode or TypeNode it will throw an IllegalArgumentException
	 * @param beanType The subclass of AbstractDose to set the node on 
	 * @param child the object to set as child, if not an DecisionTreeNode only the internal mapping is updated (@see {@link #getSelectedCategory(Class))}
	 */
	public void setChildNode(Class<? extends AbstractDose> beanClass, 
			Object child) {
		setChildNode(new DecisionTreeCoordinate(beanClass, null, 0), child);
	}
	
	public void setChildNode(Class<? extends AbstractDose> beanClass, 
			String property, 
			Object child) {
		setChildNode(new DecisionTreeCoordinate(beanClass, property, 0), child);
	}
	
	public void setChildNode(Class<? extends AbstractDose> beanClass, 
			String property,
			int rangeIndex,
			Object child) {
		setChildNode(new DecisionTreeCoordinate(beanClass, property, rangeIndex), child);
	}
	
	private void setChildNode(DecisionTreeCoordinate coordinate, Object child) {
		ValueHolder<Object> selection = d_selectedCategoryMap.get(coordinate);
		selection.setValue(child);
		if(child instanceof DecisionTreeNode) {
			DecisionTreeNode node = d_nodeMap.get(coordinate);
			DecisionTreeNode category = (DecisionTreeNode)child;
			if(node == null) { 
				d_nodeMap.put(coordinate, category);
			}
			if (node instanceof TypeNode) {
				TypeNode typeNode = (TypeNode)node;
				typeNode.setType(coordinate.beanClass, category);	
			} else if(node instanceof RangeNode) {
				RangeNode rangeNode = (RangeNode) node; 
				rangeNode.setChildNode(coordinate.index, category);
			} else if(node instanceof ExcludeNode){ 
				throw new IllegalArgumentException("Cannot set the child of a " + GUIHelper.humanize(node.getClass().getSimpleName().toString()));
			}
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

}
