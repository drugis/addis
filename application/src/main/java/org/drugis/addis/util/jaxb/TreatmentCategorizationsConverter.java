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

package org.drugis.addis.util.jaxb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.data.Edge;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeEdgeComparator;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseQuantityChoiceNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TypeEdge;
import org.drugis.addis.util.jaxb.JAXBConvertor.ConversionException;
import org.drugis.common.EqualsUtil;


public class TreatmentCategorizationsConverter {

	public static Class<? extends AbstractDose> getDoseClass(org.drugis.addis.entities.data.DoseType type) {
		switch (type) {
		case FIXED_DOSE:
			return FixedDose.class;
		case FLEXIBLE_DOSE:
			return FlexibleDose.class;
		case UNKNOWN_DOSE:
			return UnknownDose.class;
		default:
			throw new IllegalStateException("Unknown ENUM value " + type);
		}
	}

	public static TreatmentCategorization load(org.drugis.addis.entities.data.TreatmentCategorization t, Domain domain) throws ConversionException {
		String name = t.getName();
		Drug drug = JAXBConvertor.findNamedItem(domain.getDrugs(), t.getDrug());
		DoseUnit unit = JAXBConvertor.convertDoseUnit(t.getUnit(), domain);

		TreatmentCategorization tc = TreatmentCategorization.createBare(name, drug, unit);

		for (org.drugis.addis.entities.data.Category category : t.getCategory()) {
			tc.addCategory(new Category(tc, category.getName()));
		}

		convertDecisionTree(t.getDecisionTree(), tc.getDecisionTree(), t, tc, domain);
		return tc;
	}

	private static void convertDecisionTree(
			org.drugis.addis.entities.data.DecisionTree src,
			DecisionTree dest,
			org.drugis.addis.entities.data.TreatmentCategorization srcTc,
			TreatmentCategorization destTc,
			Domain domain) throws ConversionException {
		convertSubtree(dest, destTc, src.getRootNode(), dest.getRoot());
	}

	private static void convertSubtree(DecisionTree dest,
			TreatmentCategorization destTc,
			org.drugis.addis.entities.data.TypeNode srcParent,
			DecisionTreeNode destParent)
			throws ConversionException {
		convertSubTree(dest, destTc, destParent, srcParent.getTypeEdge());
	}

	private static void convertSubtree(DecisionTree dest,
			TreatmentCategorization destTc,
			org.drugis.addis.entities.data.ChoiceNode srcParent,
			DecisionTreeNode destParent)
			throws ConversionException {
		convertSubTree(dest, destTc, destParent, srcParent.getRangeEdge());
	}

	private static void convertSubTree(DecisionTree dest, TreatmentCategorization destTc, DecisionTreeNode destParent,
			List<? extends org.drugis.addis.entities.data.Edge> edges) throws ConversionException {
		for (org.drugis.addis.entities.data.Edge e : edges) {
			DecisionTreeEdge edge = convertEdge(e);
			DecisionTreeNode child;
			if (e.getChoiceNode() != null) {
				child = convertChoiceNode(e.getChoiceNode(), destTc);
				dest.addChild(edge, destParent, child);
				convertSubtree(dest, destTc, e.getChoiceNode(), child);
			} else {
				child = convertLeafNode(e.getLeafNode(), destTc);
				dest.addChild(edge, destParent, child);
			}
		}
	}

	private static DecisionTreeNode convertLeafNode(org.drugis.addis.entities.data.LeafNode leafNode, TreatmentCategorization destTc) {
		Category destCat = null;
		for(Category cat : destTc.getCategories()) {
			 if(leafNode.getCategory() != null && EqualsUtil.equal(cat.getName(), leafNode.getCategory().getName())) {
				 destCat = cat;
			 }
		}
		return new LeafNode(destCat); // if destCat is null, the LeafNode equals to an ExcludeNode
	}

	private static DecisionTreeNode convertChoiceNode(org.drugis.addis.entities.data.ChoiceNode choiceNode,
			TreatmentCategorization destTc) {
		return new DoseQuantityChoiceNode(getDoseClass(choiceNode.getObjectType()), choiceNode.getProperty(), destTc.getDoseUnit());
	}

	private static DecisionTreeEdge convertEdge(Edge e) throws ConversionException {
		if (e instanceof org.drugis.addis.entities.data.RangeEdge) {
			org.drugis.addis.entities.data.RangeEdge rangeEdge = (org.drugis.addis.entities.data.RangeEdge) e;
			return new RangeEdge(
					rangeEdge.getRangeLowerBound(),
					rangeEdge.isIsRangeLowerBoundOpen(),
					rangeEdge.getRangeUpperBound(),
					rangeEdge.isIsRangeUpperBoundOpen());
		} else if (e instanceof org.drugis.addis.entities.data.TypeEdge) {
			org.drugis.addis.entities.data.TypeEdge typeEdge = (org.drugis.addis.entities.data.TypeEdge) e;
			return new TypeEdge(getDoseClass(typeEdge.getMatchType()));
		}
		throw new ConversionException("Illegal edge type (" + e.getClass() + ")");
	}

	public static org.drugis.addis.entities.data.TreatmentCategorization save(TreatmentCategorization t) {
		org.drugis.addis.entities.data.TreatmentCategorization tc = new org.drugis.addis.entities.data.TreatmentCategorization();
		tc.setName(t.getName());
		tc.setDrug(t.getDrug().getName());
		tc.setUnit(JAXBConvertor.convertDoseUnit(t.getDoseUnit()));
		for (Category category : t.getCategories()) {
			tc.getCategory().add(convertCategory(category));
		}
		tc.setDecisionTree(convertDecisionTree(t.getDecisionTree()));
		return tc;
	}

	private static org.drugis.addis.entities.data.Category convertCategory(Category category) {
		org.drugis.addis.entities.data.Category c = new org.drugis.addis.entities.data.Category();
		c.setName(category.getName());
		return c;
	}

	private static org.drugis.addis.entities.data.DecisionTree convertDecisionTree(DecisionTree tree) {
		org.drugis.addis.entities.data.DecisionTree t = new org.drugis.addis.entities.data.DecisionTree();
		t.setRootNode((org.drugis.addis.entities.data.TypeNode)convertTypeNodeRecursive(tree.getRoot(), tree));
		return t;
	}

	private static org.drugis.addis.entities.data.TypeNode convertTypeNodeRecursive(DecisionTreeNode parent, DecisionTree tree) {
		org.drugis.addis.entities.data.TypeNode node = new org.drugis.addis.entities.data.TypeNode();
		List<org.drugis.addis.entities.data.TypeEdge> edges = node.getTypeEdge();
		List<DecisionTreeEdge> outEdges = new ArrayList<DecisionTreeEdge>(tree.getOutEdges(parent));
		Collections.sort(outEdges, new DecisionTreeEdgeComparator());
		for(DecisionTreeEdge e : outEdges) {
			edges.add(convertTypeEdge(tree, e));
		}
		return node;
	}

	private static org.drugis.addis.entities.data.TypeEdge convertTypeEdge(DecisionTree tree, DecisionTreeEdge edge) {
		TypeEdge typeEdge = (TypeEdge) edge;
		org.drugis.addis.entities.data.TypeEdge e = new org.drugis.addis.entities.data.TypeEdge();
		e.setMatchType(org.drugis.addis.entities.data.DoseType.fromValue(typeEdge.getType().getSimpleName()));
		convertEdgeTarget(tree, edge, e);
		return e;
	}

	private static org.drugis.addis.entities.data.ChoiceNode convertChoiceNodeRecursive(ChoiceNode parent, DecisionTree tree) {
		org.drugis.addis.entities.data.ChoiceNode node = convertChoiceNode(parent);
		List<org.drugis.addis.entities.data.RangeEdge> edges = node.getRangeEdge();
		List<DecisionTreeEdge> outEdges = new ArrayList<DecisionTreeEdge>(tree.getOutEdges(parent));
		Collections.sort(outEdges, new DecisionTreeEdgeComparator());
		for(DecisionTreeEdge e : outEdges) {
			edges.add(convertRangeEdge(tree, e));
		}
		return node;
	}

	private static org.drugis.addis.entities.data.RangeEdge convertRangeEdge(DecisionTree tree, DecisionTreeEdge edge) {
		RangeEdge rangeEdge = (RangeEdge) edge;
		org.drugis.addis.entities.data.RangeEdge e = new org.drugis.addis.entities.data.RangeEdge();
		e.setRangeLowerBound(rangeEdge.getLowerBound());
		e.setIsRangeLowerBoundOpen(rangeEdge.isLowerBoundOpen());
		e.setRangeUpperBound(rangeEdge.getUpperBound());
		e.setIsRangeUpperBoundOpen(rangeEdge.isUpperBoundOpen());
		convertEdgeTarget(tree, edge, e);
		return e;
	}

	private static void convertEdgeTarget(DecisionTree tree, DecisionTreeEdge edge,
			org.drugis.addis.entities.data.Edge e) {
		if (tree.getEdgeTarget(edge) instanceof LeafNode) {
			e.setLeafNode(convertLeafNode((LeafNode) tree.getEdgeTarget(edge)));
		} else {
			e.setChoiceNode(convertChoiceNodeRecursive((ChoiceNode) tree.getEdgeTarget(edge), tree));
		}
	}

	private static org.drugis.addis.entities.data.LeafNode convertLeafNode(LeafNode leafNode) {
		org.drugis.addis.entities.data.LeafNode n = new org.drugis.addis.entities.data.LeafNode();
		if(leafNode.getCategory() != null) {
			n.setCategory(JAXBConvertor.nameReference(leafNode.getName()));
		}
		return n;
	}

	private static org.drugis.addis.entities.data.ChoiceNode convertChoiceNode(ChoiceNode choiceNode) {
		org.drugis.addis.entities.data.ChoiceNode n = new org.drugis.addis.entities.data.ChoiceNode();
		n.setObjectType(org.drugis.addis.entities.data.DoseType.fromValue(choiceNode.getBeanClass().getSimpleName()));
		n.setProperty(choiceNode.getPropertyName());
		return n;
	}
}
