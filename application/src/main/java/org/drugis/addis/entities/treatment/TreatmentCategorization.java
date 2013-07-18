/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.entities.treatment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

import edu.uci.ics.jung.graph.util.Pair;

public class TreatmentCategorization extends AbstractEntity implements Comparable<TreatmentCategorization>, TypeWithName {
	public static final ChoiceNode ROOT_NODE = new ChoiceNode(AbstractDose.class, "class");
	public static final String PROPERTY_DOSE_UNIT = "doseUnit";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_CATEGORIES = "categories";

	private final ObservableList<Category> d_categories = new ArrayListModel<Category>();
	private Drug d_drug;
	private DecisionTree d_decisionTree;

	private final DoseUnit d_doseUnit;
	private String d_name;

	/**
	 * Create a TreatmentCategorization with a decision tree consisting solely of {@link TreatmentCategorization#ROOT_NODE}.
	 * @param name Name for the categorization.
	 * @param drug Drug to categorize.
	 * @param unit Unit to perform dose comparisons in.
	 * @return A new TreatmentCategorization.
	 */
	public static TreatmentCategorization createBare(String name, Drug drug, DoseUnit unit) {
		return new TreatmentCategorization(name, drug, unit, false);
	}

	/**
	 * Create a TreatmentCategorization with a default decision tree, having branches for UnknownDose, FixedDose and FlexibleDose.
	 * @param name Name for the categorization.
	 * @param drug Drug to categorize.
	 * @param unit Unit to perform dose comparisons in.
	 * @return A new TreatmentCategorization.
	 */
	public static TreatmentCategorization createDefault(String name, Drug drug, DoseUnit unit) {
		return new TreatmentCategorization(name, drug, unit, true);
	}
	
	/**
	 * Create a TreatmentCategorization with a default decision tree, having branches for UnknownDose, FixedDose and FlexibleDose.
	 * @return A new TreatmentCategorization.
	 */
	public static TreatmentCategorization createDefault() {
		return createDefault("", null, DoseUnit.createMilliGramsPerDay());
	}
	
	/**
	 * Create a trivial TreatmentCategorization that will accept any dose of the given drug.
	 * @param drug Drug to accept.
	 * @return A new TreatmentCategorization.
	 */
	public static TreatmentCategorization createTrivial(Drug drug) {
		TreatmentCategorization categorization = new TreatmentCategorization("", drug, DoseUnit.createMilliGramsPerDay(), false);
		Category category = new Category(categorization);
		categorization.addCategory(category);
		categorization.d_decisionTree = new DecisionTree(new LeafNode(category));
		return categorization;
	}
	
	private TreatmentCategorization(final String name, final Drug drug, final DoseUnit unit, boolean withDefault) {
		d_name = name;
		d_drug = drug;
		d_doseUnit = unit;
		d_decisionTree = new DecisionTree(ROOT_NODE);
		if (withDefault) { 
			addDefaultEdges(d_decisionTree);
		}
	}

	private static void addDefaultEdges(final DecisionTree tree) {
		final DecisionTreeNode root = tree.getRoot();
		tree.addEdge(new TypeEdge(UnknownDose.class), root, new LeafNode());
		tree.addEdge(new TypeEdge(FixedDose.class), root, new LeafNode());
		tree.addEdge(new TypeEdge(FlexibleDose.class), root, new LeafNode());
	}

	public boolean isTrivial() {
		return getRootNode() instanceof LeafNode;
	}

	@Override
	public String getName() {
		return d_name;
	}

	
	public void setName(final String name) {
		final String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}

	public Drug getDrug() {
		return d_drug;
	}

	public void setDrug(final Drug drug) {
		final Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, drug);
	}

	public DoseUnit getDoseUnit() {
		return d_doseUnit;
	}


	public void addCategory(final Category categoryNode) {
		d_categories.add(categoryNode);
	}

	public ObservableList<Category> getCategories() {
		return d_categories;
	}

	public Category getCategory(final AbstractDose dose) {
		return d_decisionTree.decide(dose).getCategory();
	}

	@Override
	public String getLabel() {
		return (getDrug() == null ? "" : getDrug().getLabel()) + " " +  getName();
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return new HashSet<Entity>(Arrays.asList(d_drug, d_doseUnit.getUnit()));
	}

	public DecisionTree getDecisionTree() {
		return d_decisionTree;
	}

	public DecisionTreeNode getRootNode() {
		return d_decisionTree.getRoot();
	}

	public Pair<RangeEdge> splitRange(final ChoiceNode parent, final double value, final boolean lowerRangeOpen) {
		final RangeEdge edge = (RangeEdge) d_decisionTree.findMatchingEdge(parent, value);
		return splitRange(edge, value, lowerRangeOpen);
	}
	
	/**
	 * Add a cut-off value. This splits the existing range in two.
	 * The lower range will always be initialized with the child node of the original range, the higher range will be excluded by default.
	 * @param range The range to split.
	 * @param value The cut-off value.
	 * @param lowerRangeOpen If true, the upper bound of the lower range will be open.
	 * Otherwise, it will be closed. Vice versa for the lower bound of the upper range.
	 */
	public Pair<RangeEdge> splitRange(final RangeEdge edge, final double value, final boolean lowerRangeOpen) {
		final DecisionTreeNode parent = d_decisionTree.getEdgeSource(edge);
		final DecisionTreeNode child = d_decisionTree.getEdgeTarget(edge);

		final Pair<RangeEdge> ranges = splitOnValue(edge, value, lowerRangeOpen);
		d_decisionTree.removeChild(child);
		d_decisionTree.addChild(ranges.getFirst(), parent, child);
		d_decisionTree.addChild(ranges.getSecond(), parent, new LeafNode());
		return ranges;
	}

	private static Pair<RangeEdge> splitOnValue(final RangeEdge range, final double value, final boolean isLowerRangeOpen) {
		final RangeEdge left = new RangeEdge(range.getLowerBound(), range.isLowerBoundOpen(), value, isLowerRangeOpen);
		final RangeEdge right = new RangeEdge(value, !isLowerRangeOpen, range.getUpperBound(), range.isUpperBoundOpen());
		return new Pair<RangeEdge>(left, right);
	}

	@Override
	public int compareTo(TreatmentCategorization o) {
		int drugCompare = d_drug.compareTo(o.d_drug);
		if (drugCompare != 0) { 
			return drugCompare;
		}
		return d_name.compareTo(o.d_name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TreatmentCategorization) { 
			TreatmentCategorization other = (TreatmentCategorization) obj;
			return EqualsUtil.equal(d_drug, other.d_drug) && EqualsUtil.equal(d_name, other.d_name);
		}
		return false;
	}
	
	/**
	 * The implementation of deepEquals(Entity) for TreatmentCategorization and
	 * Category is complicated by their circular dependency. However, Category
	 * is just a (TreatmentCategorization, String) pair, and here we can assume
	 * the TreatmentCategorizations to be known. Therefore it suffices to
	 * shallow equals the categories.
	 */
	@Override
	public boolean deepEquals(Entity obj) {
		if (equals(obj)) { 
			TreatmentCategorization other = (TreatmentCategorization) obj;
			return d_categories.equals(other.d_categories) 
					&& d_drug.deepEquals(other.d_drug) 
					&& d_decisionTree.equivalent(other.d_decisionTree);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return EqualsUtil.hashCode(d_drug) * 31 + EqualsUtil.hashCode(d_name);
	}
}