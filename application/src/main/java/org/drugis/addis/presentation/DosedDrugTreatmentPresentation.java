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
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.common.beans.ContentAwareListModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class DosedDrugTreatmentPresentation extends PresentationModel<DosedDrugTreatment> {
	public static final String PROPERTY_DOSE_CATEGORY = "doseCategory";
	
	private ContentAwareListModel<CategoryNode> d_categories;
	private Map<Class<? extends AbstractDose>, ValueHolder<Object>> d_doseCategoryMap = 
			new HashMap<Class<? extends AbstractDose>, ValueHolder<Object>>(); 
	
	private final Domain d_domain;
	private ExcludeNode d_excludeNode = new ExcludeNode();

	public DosedDrugTreatmentPresentation(DosedDrugTreatment bean) {	
		this(bean, null);
	}

	public DosedDrugTreatmentPresentation(DosedDrugTreatment bean, Domain domain) {		
		super(bean);
		d_domain = domain;
		d_categories = new ContentAwareListModel<CategoryNode>(bean.getCategories());
		if(getBean().getRootNode() instanceof TypeNode) { 
			TypeNode types = (TypeNode) getBean().getRootNode();
			for(Class<? extends AbstractDose> type : types.getTypeMap().keySet()) { 
				d_doseCategoryMap.put(type, new ModifiableHolder<Object>(d_excludeNode));
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
	
	public ValueHolder<Object> getKnownCategory() { 
		if((getCategory(FixedDose.class).getValue()).equals(getCategory(FlexibleDose.class).getValue())) { 
			return getCategory(FlexibleDose.class);
		}
		throw new IllegalStateException("Fixed and flexible categorizations differ, cannot return the category for all known doses");
	}
	
	public ValueHolder<Object> getCategory(Class<? extends AbstractDose> type) { 
		return d_doseCategoryMap.get(type);
	}
	
	/** Sets the category for one or more AbstractDose types
	 * @param selection the category to be set, excludes everything that is not a DecisionTreeNode 
	 * @param the types to be set
	 */
	public void setDoseCategory(Object selection, Class<? extends AbstractDose> ... types) {
		final DecisionTreeNode node = getBean().getRootNode();
		for(Class<? extends AbstractDose> type : types) { 
			d_doseCategoryMap.get(type).setValue(selection);
		}
		DecisionTreeNode category = (selection instanceof DecisionTreeNode) ? (DecisionTreeNode)selection : d_excludeNode;
		if (node instanceof TypeNode) {
			TypeNode typeNode = (TypeNode)node;
			for(Class<? extends AbstractDose> type : types) { 
				typeNode.setType(type, category);
			}
		}
		firePropertyChange(PROPERTY_DOSE_CATEGORY, null, selection);
	}
	
	public DosedDrugTreatment commit() {
		if (d_domain.getTreatments().contains(getBean())) {
			throw new IllegalStateException("Treatment already exists in domain");
		}
		
		d_domain.getTreatments().add(getBean());
		return getBean();
	}
}
