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

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseQuantityChoiceNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge.CategorySpecifiers;
import org.drugis.common.beans.SuffixedObservableList;
import org.drugis.common.beans.TransformOnceObservableList;
import org.drugis.common.beans.TransformedObservableList.Transform;
import org.drugis.common.beans.ValueEqualsModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class DosedDrugTreatmentPresentation extends PresentationModel<DosedDrugTreatment> {
	private final Domain d_domain;
	private final ObservableList<Category> d_categories;

	private final ValueModel d_knownDoseChoice;
	private final ObservableList<DecisionTreeNode> d_knownDoseOptions;

	private final ValueModel d_considerDoseType;
	private final ValueModel d_ignoreDoseType;

	private final DoseQuantityChoiceNode d_fixedRangeNode;
	private final DoseQuantityChoiceNode d_flexibleLowerNode;
	private final DoseQuantityChoiceNode d_flexibleUpperNode;

	private final ValueModel d_considerFixed;
	private final ValueModel d_considerFlexibleLower;
	private final ValueModel d_considerFlexibleUpper;
	private final HashMap<DecisionTreeEdge, ValueModel> d_choiceForEdge = new HashMap<DecisionTreeEdge, ValueModel>();
	private final HashMap<DecisionTreeEdge, ObservableList<DecisionTreeNode>> d_optionsForEdge = new HashMap<DecisionTreeEdge, ObservableList<DecisionTreeNode>>();

	public DosedDrugTreatmentPresentation(final DosedDrugTreatment bean) {
		this(bean, null);
	}

	public DosedDrugTreatmentPresentation(final DosedDrugTreatment bean, final Domain domain) {
		super(bean);
		d_domain = domain;
		d_categories = bean.getCategories();

		// Magic nodes
		d_fixedRangeNode = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, getBean().getDoseUnit());
		d_flexibleLowerNode = new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, getBean().getDoseUnit());
		d_flexibleUpperNode = new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, getBean().getDoseUnit());

		d_knownDoseChoice = new ModifiableHolder<DecisionTreeNode>();
		d_knownDoseOptions = createOptions(
				DosedDrugTreatmentKnowledge.CategorySpecifiers.CONSIDER,
				DosedDrugTreatmentKnowledge.CategorySpecifiers.DO_NOT_CONSIDER,
				new LeafNode());

		d_considerDoseType = new ValueEqualsModel(d_knownDoseChoice, CategorySpecifiers.CONSIDER);
		d_ignoreDoseType = new ValueEqualsModel(d_knownDoseChoice, CategorySpecifiers.DO_NOT_CONSIDER);

		final DecisionTreeEdge unknownDoseEdge = findTypeEdge(UnknownDose.class);
		d_choiceForEdge.put(unknownDoseEdge, createModelForEdge(unknownDoseEdge));
		d_optionsForEdge.put(unknownDoseEdge, createOptions(new LeafNode()));
		final DecisionTreeEdge fixedDoseEdge = findTypeEdge(FixedDose.class);
		d_choiceForEdge.put(fixedDoseEdge, createModelForEdge(fixedDoseEdge));
		d_optionsForEdge.put(fixedDoseEdge, createOptions(d_fixedRangeNode, new LeafNode()));
		final DecisionTreeEdge flexibleDoseEdge = findTypeEdge(FlexibleDose.class);
		d_choiceForEdge.put(flexibleDoseEdge, createModelForEdge(flexibleDoseEdge));
		d_optionsForEdge.put(flexibleDoseEdge, createOptions(d_flexibleLowerNode, d_flexibleUpperNode, new LeafNode()));

		d_considerFixed = new ValueEqualsModel(getModelForFixedDose(), d_fixedRangeNode);
		d_considerFlexibleLower = new ValueEqualsModel(getModelForFlexibleDose(), d_flexibleLowerNode);
		d_considerFlexibleUpper = new ValueEqualsModel(getModelForFlexibleDose(), d_flexibleUpperNode);
	}

	public ValueModel getDrug() {
		return getModel(DosedDrugTreatment.PROPERTY_DRUG);
	}

	public ValueModel getName() {
		return getModel(DosedDrugTreatment.PROPERTY_NAME);
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

	/**
	 * ValueModel that holds the decision (DecisionTreeNode) for the given edge.
	 */
	private ValueModel createModelForEdge(final DecisionTreeEdge edge) {
		final DecisionTree tree = getBean().getDecisionTree();
		final DecisionTreeChildModel model = new DecisionTreeChildModel(tree, edge);
		return model;
	}

	public DecisionTreeEdge findTypeEdge(final Class<?> type) {
		final DecisionTree tree = getBean().getDecisionTree();
		return getBean().getDecisionTree().findMatchingEdge(tree.getRoot(), type);
	}

	/**
	 * Selection holder for action on unknown doses.
	 */
	public ValueModel getModelForUnknownDose() {
		return d_choiceForEdge.get(findTypeEdge(UnknownDose.class));
	}

	/**
	 * Selection holder for action on "known" doses (fixed or flexible).
	 */
	public ValueModel getModelForKnownDose() {
		return d_knownDoseChoice;
	}

	/**
	 * Selection holder for action on fixed doses.
	 */
	public ValueModel getModelForFixedDose() {
		return d_choiceForEdge.get(findTypeEdge(FixedDose.class));
	}

	/**
	 * Selection holder for action on flexible doses.
	 */
	public ValueModel getModelForFlexibleDose() {
		return d_choiceForEdge.get(findTypeEdge(FlexibleDose.class));
	}

	/**
	 * ValueModel (Boolean) that indicates whether fixed and flexible doses should be treated separately.
	 */
	public ValueModel getConsiderDoseType() {
		return d_considerDoseType;
	}

	/**
	 * ValueModel (Boolean) that indicates whether fixed and flexible doses should be treated identically.
	 */
	public ValueModel getIgnoreDoseType() {
		return d_ignoreDoseType;
	}

	/**
	 * ValueModel (Boolean) that indicates whether quantity should be considered for fixed doses.
	 */
	public ValueModel getConsiderFixed() {
		return d_considerFixed;
	}

	/**
	 * ValueModel (Boolean) that indicates whether the MIN_DOSE should be considered first for flexible doses.
	 */
	public ValueModel getConsiderFlexibleLowerFirst() {
		return d_considerFlexibleLower;
	}

	/**
	 * ValueModel (Boolean) that indicates whether the MAX_DOSE should be considered first for flexible doses.
	 */
	public ValueModel getConsiderFlexibleUpperFirst() {
		return d_considerFlexibleUpper;
	}

	public ObservableList<DecisionTreeEdge> getFlexibleLowerRanges() {
		return new DecisionTreeOutEdgesModel(getBean().getDecisionTree(), d_flexibleLowerNode);
	}

	public ObservableList<DecisionTreeEdge> getFlexibleUpperRanges() {
		return new DecisionTreeOutEdgesModel(getBean().getDecisionTree(), d_flexibleUpperNode);
	}

	public ChoiceNode getFlexibleLowerRangeNode() {
		return d_flexibleLowerNode;
	}

	public ChoiceNode getFlexibleUpperRangeNode() {
		return d_flexibleUpperNode;
	}

	public ChoiceNode getFixedRangeNode() {
		return d_fixedRangeNode;
	}

	private ObservableList<DecisionTreeNode> createOptions(final DecisionTreeNode ... extraOptions) {
		final TransformOnceObservableList<Category, DecisionTreeNode> transformedCategories = new TransformOnceObservableList<Category, DecisionTreeNode>(d_categories,
				new Transform<Category, DecisionTreeNode>() {
					@Override
					public DecisionTreeNode transform(final Category a) {
						return new LeafNode(a);
					}
		});
		return new SuffixedObservableList<DecisionTreeNode>(transformedCategories, extraOptions);
	}

	public ObservableList<DecisionTreeNode> getOptionsForEdge(final DecisionTreeEdge edge) {
		if (d_optionsForEdge.get(edge) == null) {
			d_optionsForEdge.put(edge, createOptions(new LeafNode()));
		}
		return d_optionsForEdge.get(edge);
	}

	public ObservableList<DecisionTreeNode> getOptionsForKnownDose() {
		return d_knownDoseOptions;
	}

	public ObservableList<DecisionTreeNode> getOptionsForUnknownDose() {
		return getOptionsForEdge(findTypeEdge(UnknownDose.class));
	}

	public ObservableList<DecisionTreeNode> getOptionsForFixedDose() {
		return getOptionsForEdge(findTypeEdge(FixedDose.class));
	}

	public ObservableList<DecisionTreeNode> getOptionsForFlexibleDose() {
		return getOptionsForEdge(findTypeEdge(FlexibleDose.class));
	}

	public ValueModel getModelForEdge(final DecisionTreeEdge edge) {
		if (d_choiceForEdge.get(edge) == null) {
			d_choiceForEdge.put(edge, createModelForEdge(edge));
		}
		return d_choiceForEdge.get(edge);
	}
}
