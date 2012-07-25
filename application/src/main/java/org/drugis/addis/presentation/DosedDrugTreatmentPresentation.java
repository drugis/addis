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

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.common.beans.ContentAwareListModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class DosedDrugTreatmentPresentation extends PresentationModel<DosedDrugTreatment> {
	private final Domain d_domain;
	private final ObservableList<Category> d_categories;

	public DosedDrugTreatmentPresentation(final DosedDrugTreatment bean) {
		this(bean, null);
	}

	public DosedDrugTreatmentPresentation(final DosedDrugTreatment bean, final Domain domain) {
		super(bean);
		d_domain = domain;
		d_categories = new ContentAwareListModel<Category>(bean.getCategories());
	}

	public Drug getDrug() {
		return getBean().getDrug();
	}

	public ValueHolder<String> getName() {
		return new ModifiableHolder<String>(getBean().getName());
	}

	public ObservableList<Category> getCategories() {
		return d_categories;
	}

	public DoseUnit getDoseUnit() {
		return getBean().getDoseUnit();
	}

	public void setDoseUnit(final DoseUnit unit) {
		getBean().setDoseUnit(unit);
	}

	public DoseUnitPresentation getDoseUnitPresentation() {
		return new DoseUnitPresentation(getDoseUnit());
	}

	public void resetTree() {
		getBean().resetTree();
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

	public ObservableList<DecisionTreeEdge> getOutEdges(final DecisionTreeNode node) {
		return new DecisionTreeOutEdgesModel(getBean().getDecisionTree(), node);
	}

//	public DecisionTreeNode setKnownDoses(final DecisionTreeNode prototype) {
//		final DecisionTreeNode fixed = getType(FixedDose.class);
//		final DecisionTreeNode flexible = getType(FlexibleDose.class);
//
//		if(prototype instanceof LeafNode) {
//			setSelected(fixed, prototype);
//			setSelected(flexible, prototype);
//			return prototype;
//		} else if (prototype instanceof RangeNode) {
//			final RangeNode protoRange = (RangeNode) prototype;
//			final DoseRangeNode fixedRange = inheritPrototype(protoRange, FixedDose.class, FixedDose.PROPERTY_QUANTITY);
//			final DoseRangeNode flexLowerRange = inheritPrototype(protoRange, FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE);
//			final DoseRangeNode flexUpperRange = inheritPrototype(protoRange, FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE);
//
//			setSelected(fixed, fixedRange);
//			setSelected(flexible, flexLowerRange);
//			setSelected(flexLowerRange, flexUpperRange);
//
//			return fixedRange;
//		}
//
//		throw new IllegalArgumentException("prototype is not compatible (must be a LeafNode or a RangeNode, was: " + prototype + ")");
//	}
//
//	public void setKnownDoses(final DecisionTreeNode parent, final Object selected) {
//		setSelected(parent, selected); // Fixed case
//		if(parent instanceof RangeNode) {
//			final DecisionTreeNode prototype = inheritPrototype((RangeNode)parent, FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE);
//			final DecisionTreeNode node = find(d_tree.getVertices(), new Predicate<DecisionTreeNode>() { // this is super inefficient
//				@Override
//				public boolean evaluate(final DecisionTreeNode input) {
//					return 	input.similar(prototype) &&
//							input.getBeanClass().equals(prototype.getBeanClass()) &&
//							input.getPropertyName().equals(prototype.getPropertyName())  ;
//				}
//			});
//			if(node != null) {
//				setSelected(node, selected);
//			} else {
//				throw new IllegalArgumentException("Leaf " + node + " did not match prototype " + prototype);
//			}
//		}
//	}
//
//	private DoseRangeNode inheritPrototype(final RangeNode protoRange, final Class<? extends AbstractDose> beanClass, final String property) {
//		 return new DoseRangeNode(
//				beanClass,
//				property,
//				protoRange.getRangeLowerBound(),
//				protoRange.isRangeLowerBoundOpen(),
//				protoRange.getRangeUpperBound(),
//				protoRange.isRangeUpperBoundOpen(),
//				getDoseUnit());
//		}

	public String getCategory(final AbstractDose dose) {
		return getBean().getCategory(dose).toString();
	}

	public DecisionTreeChildModel getChoiceModelForType(final Class<?> type) {
		final DecisionTree tree = getBean().getDecisionTree();
		final DecisionTreeChildModel model = new DecisionTreeChildModel(tree, tree.findMatchingEdge(tree.getRoot(), type));
		return model;
	}
}
