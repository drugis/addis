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

package org.drugis.addis.presentation.wizard;

import static org.apache.commons.collections15.CollectionUtils.find;

import java.util.HashMap;

import org.apache.commons.collections15.Predicate;
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
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.presentation.DecisionTreeChildModel;
import org.drugis.addis.presentation.DecisionTreeOutEdgesModel;
import org.drugis.addis.presentation.DoseUnitPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.beans.SuffixedObservableList;
import org.drugis.common.beans.TransformOnceObservableList;
import org.drugis.common.beans.TransformedObservableList.Transform;
import org.drugis.common.beans.ValueEqualsModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

import edu.uci.ics.jung.graph.util.Pair;

@SuppressWarnings("serial")
public class DosedDrugTreatmentWizardPresentation extends PresentationModel<DosedDrugTreatment> {
	public static enum CategorySpecifiers implements DecisionTreeNode {
		CONSIDER("Consider dose type"),
		DO_NOT_CONSIDER("Do not consider dose type");
	
		private final String d_title;
	
		private CategorySpecifiers(final String title) {
			d_title = title;
		}
	
		@Override
		public String getName() {
			return d_title;
		}
	
		@Override
		public String toString() {
			return getName();
		}
	}

	private final Domain d_domain;
	private final ObservableList<Category> d_contentAwareCategories;

	private final ValueHolder<DecisionTreeNode> d_knownDoseChoice;
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

	public DosedDrugTreatmentWizardPresentation(final DosedDrugTreatment bean, final Domain domain) {
		super(bean);
		d_domain = domain;
		d_contentAwareCategories = new ContentAwareListModel<Category>(bean.getCategories());

		// Magic nodes
		d_fixedRangeNode = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, getBean().getDoseUnit());
		d_flexibleLowerNode = createMinDoseNode();
		d_flexibleUpperNode = createMaxDoseNode();

		d_knownDoseChoice = new ModifiableHolder<DecisionTreeNode>(new LeafNode());
		d_knownDoseOptions = createOptions(
				CategorySpecifiers.CONSIDER,
				CategorySpecifiers.DO_NOT_CONSIDER,
				d_knownDoseChoice.getValue());

		d_considerDoseType = new ValueEqualsModel(d_knownDoseChoice, CategorySpecifiers.CONSIDER);
		d_ignoreDoseType = new ValueEqualsModel(d_knownDoseChoice, CategorySpecifiers.DO_NOT_CONSIDER);

		final DecisionTreeEdge unknownDoseEdge = findTypeEdge(UnknownDose.class);
		d_choiceForEdge.put(unknownDoseEdge, createModelForEdge(unknownDoseEdge));
		d_optionsForEdge.put(unknownDoseEdge, createOptions(getEdgeTarget(unknownDoseEdge)));
		
		final DecisionTreeEdge fixedDoseEdge = findTypeEdge(FixedDose.class);
		d_choiceForEdge.put(fixedDoseEdge, createModelForEdge(fixedDoseEdge));
		d_optionsForEdge.put(fixedDoseEdge, createOptions(d_fixedRangeNode, getEdgeTarget(fixedDoseEdge)));
		
		final DecisionTreeEdge flexibleDoseEdge = findTypeEdge(FlexibleDose.class);
		d_choiceForEdge.put(flexibleDoseEdge, createModelForEdge(flexibleDoseEdge));
		d_optionsForEdge.put(flexibleDoseEdge, createOptions(d_flexibleLowerNode, d_flexibleUpperNode, getEdgeTarget(flexibleDoseEdge)));

		d_considerFixed = new ValueEqualsModel(getModelForFixedDose(), d_fixedRangeNode);
		d_considerFlexibleLower = new ValueEqualsModel(getModelForFlexibleDose(), d_flexibleLowerNode);
		d_considerFlexibleUpper = new ValueEqualsModel(getModelForFlexibleDose(), d_flexibleUpperNode);
	}

	private DecisionTreeNode getEdgeTarget(final DecisionTreeEdge edge) {
		return getBean().getDecisionTree().getEdgeTarget(edge);
	}

	public ValueModel getDrug() {
		return getModel(DosedDrugTreatment.PROPERTY_DRUG);
	}

	public ValueModel getName() {
		return getModel(DosedDrugTreatment.PROPERTY_NAME);
	}

	public ObservableList<Category> getCategories() {
		return getBean().getCategories();
	}

	public DoseUnit getDoseUnit() {
		return getBean().getDoseUnit();
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

	public ValueModel getModelForEdge(final DecisionTreeEdge edge) {
		if (d_choiceForEdge.get(edge) == null) {
			d_choiceForEdge.put(edge, createModelForEdge(edge));
		}
		return d_choiceForEdge.get(edge);
	}

	/**
	 * Selection holder for action on unknown doses.
	 */
	public ValueModel getModelForUnknownDose() {
		return getModelForEdge(findTypeEdge(UnknownDose.class));
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
		return getModelForEdge(findTypeEdge(FixedDose.class));
	}

	/**
	 * Selection holder for action on flexible doses.
	 */
	public ValueModel getModelForFlexibleDose() {
		return getModelForEdge(findTypeEdge(FlexibleDose.class));
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
		final TransformOnceObservableList<Category, DecisionTreeNode> transformedCategories = new TransformOnceObservableList<Category, DecisionTreeNode>(
				d_contentAwareCategories,
				new Transform<Category, DecisionTreeNode>() {
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

	/**
	 * Transform the tree according to the option chosen for "known dose".
	 */
	public void transformTree() {
		Object knownDose = getModelForKnownDose().getValue();
		if (knownDose instanceof LeafNode) {
			transformLeaf();
		} else if (knownDose.equals(CategorySpecifiers.DO_NOT_CONSIDER)) { 
			assert(d_fixedRangeNode == getModelForFixedDose().getValue());
			transformSubtree();
		}
	}

	private void transformSubtree() {
		DecisionTree tree = getBean().getDecisionTree();
		getModelForFlexibleDose().setValue(d_flexibleLowerNode);
		
		for (DecisionTreeEdge edge : getOutEdges(d_fixedRangeNode)) {
			DoseQuantityChoiceNode node = new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, getDoseUnit());
			RangeEdge rangeEdge = (RangeEdge) edge;
			tree.addChild(RangeEdge.copy(rangeEdge), d_flexibleLowerNode, node);
			
			LeafNode leafChild = new LeafNode(((LeafNode)tree.getEdgeTarget(edge)).getCategory());

			if (leafChild.getCategory() == null) {
				RangeEdge upperEdge = new RangeEdge(rangeEdge.getLowerBound(), rangeEdge.isLowerBoundOpen(), Double.POSITIVE_INFINITY, true);
				tree.addChild(upperEdge, node, new LeafNode());
			} else { 
				tree.addChild(RangeEdge.copy(rangeEdge), node, leafChild);
				if (!Double.isInfinite(rangeEdge.getUpperBound())) { 
					RangeEdge upperEdge = new RangeEdge(rangeEdge.getUpperBound(), !rangeEdge.isUpperBoundOpen(), Double.POSITIVE_INFINITY, true);
					tree.addChild(upperEdge, node, new LeafNode());
				}
			}
		}
	}

	private void transformLeaf() {
		final Category category = ((LeafNode) getModelForKnownDose().getValue()).getCategory();
		getModelForFlexibleDose().setValue(findLeafNode(getOptionsForFlexibleDose(), category));
		getModelForFixedDose().setValue(findLeafNode(getOptionsForFixedDose(), category));
	}
	
	/**
	 * {@link DosedDrugTreatment#splitRange(RangeEdge, double, boolean)}
	 */
	public Pair<RangeEdge> splitRange(final RangeEdge range, final double value, final boolean lowerRangeOpen) {
		ObservableList<DecisionTreeNode> options1 = getOptionsForEdge(range);
		Pair<RangeEdge> ranges = getBean().splitRange(range, value, lowerRangeOpen);
		
		// Ensure options list is preserved for first range
		d_optionsForEdge.remove(range);
		d_optionsForEdge.put(ranges.getFirst(), options1);
		
		// Ensure exclude-node for second range is in the options list
		DecisionTree tree = getBean().getDecisionTree();
		ObservableList<DecisionTreeNode> options2 = createOptionsForChildrenOfNode((ChoiceNode) tree.getEdgeSource(ranges.getSecond()));
		d_optionsForEdge.put(ranges.getSecond(), options2);
		getModelForEdge(ranges.getSecond()).setValue(findLeafNode(options2, null));
		
		return ranges;
	}
	
	/**
	 * In the haystack, find a leaf node with the needle as its category.
	 */
	public static LeafNode findLeafNode(final ObservableList<DecisionTreeNode> haystack, final Category needle) {
		return (LeafNode)find(haystack, new Predicate<DecisionTreeNode>() {
			public boolean evaluate(DecisionTreeNode object) {
				if (object instanceof LeafNode) {
					LeafNode node = (LeafNode) object;
					return EqualsUtil.equal(node.getCategory(), needle);
				}
				return false;
			}
		});
	}

	public RangeEdge addDefaultRangeEdge(ChoiceNode node) {
		RangeEdge rangeEdge = createRangeEdge(node);
		ObservableList<DecisionTreeNode> options = createOptionsForChildrenOfNode(node);
		d_optionsForEdge.put(rangeEdge, options);
		getBean().getDecisionTree().addChild(rangeEdge, node, findLeafNode(options, null));
		return rangeEdge;
	}

	private RangeEdge createRangeEdge(ChoiceNode node) {
		DecisionTree tree = getBean().getDecisionTree();
		String nodeProperty = node.getPropertyName();
		ChoiceNode parentNode = (ChoiceNode)tree.getParent(node);
		if (EqualsUtil.equal(nodeProperty, FlexibleDose.PROPERTY_MIN_DOSE) 
				&& EqualsUtil.equal(parentNode.getPropertyName(), FlexibleDose.PROPERTY_MAX_DOSE)) {
			RangeEdge range = (RangeEdge) tree.getParentEdge(node);
			return new RangeEdge(0.0, false, range.getUpperBound(), range.isUpperBoundOpen());
		} else if (EqualsUtil.equal(nodeProperty, FlexibleDose.PROPERTY_MAX_DOSE) 
				&& EqualsUtil.equal(parentNode.getPropertyName(), FlexibleDose.PROPERTY_MIN_DOSE)) {
			RangeEdge range = (RangeEdge) tree.getParentEdge(node);
			return new RangeEdge(range.getLowerBound(), range.isLowerBoundOpen(), Double.POSITIVE_INFINITY, true);
		} else { 
			return RangeEdge.createDefault();

		}
	}

	private ObservableList<DecisionTreeNode> createOptionsForChildrenOfNode(ChoiceNode node) {
		DecisionTree tree = getBean().getDecisionTree();
		String nodeProperty = node.getPropertyName();
		String parentProperty = ((ChoiceNode)tree.getParent(node)).getPropertyName();
		if (nodeProperty.equals(FlexibleDose.PROPERTY_MIN_DOSE) && !parentProperty.equals(FlexibleDose.PROPERTY_MAX_DOSE)) {
			return createOptions(createMaxDoseNode(), new LeafNode());
		} else if (nodeProperty.equals(FlexibleDose.PROPERTY_MAX_DOSE) && !parentProperty.equals(FlexibleDose.PROPERTY_MIN_DOSE)) {
			return createOptions(createMinDoseNode(), new LeafNode());
		} else {
			return createOptions(new LeafNode());
		}
	}
	
	private DoseQuantityChoiceNode createMinDoseNode() {
		return new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, getBean().getDoseUnit());
	}
	
	private DoseQuantityChoiceNode createMaxDoseNode() {
		return new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, getBean().getDoseUnit());
	}
}
