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

import static org.drugis.addis.presentation.wizard.TreatmentCategorizationWizardPresentation.findLeafNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseQuantityChoiceNode;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

import edu.uci.ics.jung.graph.util.Pair;

public class TreatmentCategorizationWizardPresentationTest {

	private TreatmentCategorization d_bean;
	private TreatmentCategorizationWizardPresentation d_pm;
	private Domain d_domain;

	@Before
	public void setUp() {
		d_bean = TreatmentCategorization.createDefault("HD/LD", ExampleData.buildDrugCandesartan(), DoseUnit.MILLIGRAMS_A_DAY);
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new TreatmentCategorizationWizardPresentation(d_bean, d_domain);
	}

	@Test
	public void testInitialization() {
		assertEquals(ExampleData.buildDrugCandesartan(), d_pm.getDrug().getValue());
		assertEquals("HD/LD", d_pm.getName().getValue());
		assertEquals(Collections.emptyList(), d_pm.getCategories());
		
		assertTrue(d_pm.getOptionsForUnknownDose().contains(d_pm.getModelForUnknownDose().getValue()));
		assertTrue(d_pm.getOptionsForKnownDose().contains(d_pm.getModelForKnownDose().getValue()));
		assertTrue(d_pm.getOptionsForFixedDose().contains(d_pm.getModelForFixedDose().getValue()));
		assertTrue(d_pm.getOptionsForFlexibleDose().contains(d_pm.getModelForFlexibleDose().getValue()));
	}

	@Test
	public void testMessWithCategories() {
		final Category catNode1 = new Category(d_bean, "foo");
		final Category catNode2 = new Category(d_bean, "bar");
		d_pm.getCategories().add(catNode1);
		d_pm.getCategories().add(catNode2);
		assertEquals(Arrays.asList(catNode1, catNode2), d_pm.getCategories());
		d_pm.getCategories().remove(catNode1);
		assertEquals(Arrays.asList(catNode2), d_pm.getCategories());
	}
	
	@Test
	public void testSplitNodePreservesOptions() {
		Category foo = new Category(d_bean, "foo");
		d_pm.getCategories().add(foo);
		
		d_pm.getModelForFixedDose().setValue(d_pm.getFixedRangeNode());
		
		RangeEdge edge0 = RangeEdge.createDefault();
		ObservableList<DecisionTreeNode> options0 = d_pm.getOptionsForEdge(edge0);
		LeafNode child0 = findLeafNode(d_pm.getOptionsForEdge(edge0), foo);
		d_pm.getBean().getDecisionTree().addChild(edge0, d_pm.getFixedRangeNode(), child0);
		Pair<RangeEdge> splitRange = d_pm.splitRange(edge0, 20.0, false);

		assertSame(child0, d_pm.getModelForEdge(splitRange.getFirst()).getValue());
		assertSame(options0, d_pm.getOptionsForEdge(splitRange.getFirst()));
		assertNotSame(options0, d_pm.getOptionsForEdge(splitRange.getSecond()));
		assertTrue(d_pm.getOptionsForEdge(splitRange.getSecond()).contains(d_pm.getModelForEdge(splitRange.getSecond()).getValue()));
	}
	
	@Test
	public void testUnknownDoseDirectlyToTree() {
		Category foo = new Category(d_bean, "foo");
		d_pm.getCategories().add(foo);
		
		d_pm.getModelForUnknownDose().setValue(findLeafNode(d_pm.getOptionsForUnknownDose(), foo));
		
		DecisionTreeNode edgeTarget = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(UnknownDose.class));
		assertNodeHasCategory(edgeTarget, foo);
	}
	
	@Test
	public void testKnownDoseNotToTree() {
		Category foo = new Category(d_bean, "foo");
		d_pm.getCategories().add(foo);
		
		DecisionTreeNode fixedExpected = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(FixedDose.class));
		DecisionTreeNode flexibleExpected = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(FixedDose.class));

		d_pm.getModelForKnownDose().setValue(findLeafNode(d_pm.getOptionsForKnownDose(), foo));
		
		DecisionTreeNode fixedTarget = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(FixedDose.class));
		assertEquals(fixedExpected, fixedTarget);
		
		DecisionTreeNode flexibleTarget = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(FixedDose.class));
		assertEquals(flexibleExpected, flexibleTarget);
	}

	@Test
	public void testRewriteWithLeaf() {
		Category foo = new Category(d_bean, "foo");
		d_pm.getCategories().add(foo);
		Category bar = new Category(d_bean, "bar");
		d_pm.getCategories().add(bar);
		
		d_pm.getModelForUnknownDose().setValue(findLeafNode(d_pm.getOptionsForUnknownDose(), foo));
		d_pm.getModelForKnownDose().setValue(findLeafNode(d_pm.getOptionsForKnownDose(), bar));
		d_pm.transformTree();
		assertNodeHasCategory(d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(UnknownDose.class)), foo);
		DecisionTreeNode fixedNode = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(FixedDose.class));
		assertNodeHasCategory(fixedNode, bar);
		assertTrue(d_pm.getOptionsForFixedDose().contains(fixedNode));
		DecisionTreeNode flexibleNode = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(FlexibleDose.class));
		assertNodeHasCategory(flexibleNode, bar);
		assertTrue(d_pm.getOptionsForFlexibleDose().contains(flexibleNode));

		d_pm.getModelForKnownDose().setValue(findLeafNode(d_pm.getOptionsForKnownDose(), null));
		d_pm.transformTree();
		fixedNode = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(FixedDose.class));
		flexibleNode = d_bean.getDecisionTree().getEdgeTarget(d_pm.findTypeEdge(FlexibleDose.class));
		assertNodeHasCategory(fixedNode, null);
		assertNodeHasCategory(flexibleNode, null);
	}
	
	@Test
	public void testRewriteDoNotConsiderDoseType() {
		Category foo = new Category(d_bean, "foo");
		d_pm.getCategories().add(foo);
		Category bar = new Category(d_bean, "bar");
		d_pm.getCategories().add(bar);
		
		d_pm.getModelForKnownDose().setValue(TreatmentCategorizationWizardPresentation.CategorySpecifiers.DO_NOT_CONSIDER);
		d_pm.getModelForFixedDose().setValue(d_pm.getFixedRangeNode());
		// Add ranges to tree (normally handled by RangeInputPresentation).
		
		RangeEdge edge0 = RangeEdge.createDefault();
		d_pm.getBean().getDecisionTree().addChild(edge0, d_pm.getFixedRangeNode(), findLeafNode(d_pm.getOptionsForEdge(edge0), bar));
		Pair<RangeEdge> splitRange = d_pm.splitRange(edge0, 20.0, false);
		d_pm.getModelForEdge(splitRange.getSecond()).setValue(findLeafNode(d_pm.getOptionsForEdge(splitRange.getSecond()), foo));
		
		d_pm.transformTree();
		
		// The transformed tree should consider MIN_DOSE first.
		assertEquals(d_pm.getFlexibleLowerRangeNode(), d_pm.getModelForFlexibleDose().getValue());
		
		// The ranges for MIN_DOSE should be identical to those for FixedDose
		List<DecisionTreeEdge> lowerEdges = d_pm.getFlexibleLowerRanges();
		ObservableList<DecisionTreeEdge> fixedEdges = d_pm.getOutEdges(d_pm.getFixedRangeNode());
		assertEquals(2, lowerEdges.size());
		assertEquals(fixedEdges.get(0).toString(), lowerEdges.get(0).toString());
		assertEquals(fixedEdges.get(1).toString(), lowerEdges.get(1).toString());
		
		for (int i = 0; i < lowerEdges.size(); i++) {
			RangeEdge fixedRange = (RangeEdge) fixedEdges.get(i);
			RangeEdge lowerRange = (RangeEdge) lowerEdges.get(i);
			DoseQuantityChoiceNode upper = (DoseQuantityChoiceNode) d_pm.getModelForEdge(lowerRange).getValue();
			// FIXME: test that the node is in the options list.

			// For each MIN_DOSE range, the MAX_DOSE should subsequently be considered
			assertEquals(FlexibleDose.class, upper.getBeanClass());
			assertEquals(FlexibleDose.PROPERTY_MAX_DOSE, upper.getPropertyName());
			assertSame(d_bean.getDoseUnit(), upper.getDoseUnit());
			
			// There should be two out-edges, unless the upper bound is +infinity
			List<DecisionTreeEdge> upperEdges = d_pm.getOutEdges(upper);
			assertEquals(lowerRange.toString(), upperEdges.get(0).toString());
			assertNodeHasCategory(
					(DecisionTreeNode) d_pm.getModelForEdge(upperEdges.get(0)).getValue(),
					((LeafNode)d_pm.getModelForEdge(fixedRange).getValue()).getCategory());
			if (!Double.isInfinite(lowerRange.getUpperBound())) {
				assertEquals(2, upperEdges.size());
				RangeEdge expected = new RangeEdge(lowerRange.getUpperBound(), !lowerRange.isUpperBoundOpen(), Double.POSITIVE_INFINITY, true);
				assertEquals(expected.toString(), upperEdges.get(1).toString());
				assertNodeHasCategory(
						(DecisionTreeNode) d_pm.getModelForEdge(upperEdges.get(1)).getValue(),
						null);
			} else {
				assertEquals(1, upperEdges.size());
			}
		}
	}
	
	@Test
	public void testRewriteDoNotConsiderDoseTypeWithExclusion() {
		Category bar = new Category(d_bean, "bar");
		d_pm.getCategories().add(bar);
		
		d_pm.getModelForKnownDose().setValue(TreatmentCategorizationWizardPresentation.CategorySpecifiers.DO_NOT_CONSIDER);
		d_pm.getModelForFixedDose().setValue(d_pm.getFixedRangeNode());
		// Add ranges to tree (normally handled by RangeInputPresentation).
		RangeEdge edge0 = RangeEdge.createDefault();
		d_pm.getBean().getDecisionTree().addChild(edge0, d_pm.getFixedRangeNode(), findLeafNode(d_pm.getOptionsForEdge(edge0), null));
		Pair<RangeEdge> splitRange = d_pm.splitRange(edge0, 20.0, false);
		d_pm.getModelForEdge(splitRange.getSecond()).setValue(findLeafNode(d_pm.getOptionsForEdge(splitRange.getSecond()), bar));
		
		d_pm.transformTree();
		
		// The lower range is excluded anyway, so don't split on MAX_DOSE.
		DoseQuantityChoiceNode upper = (DoseQuantityChoiceNode) d_pm.getModelForEdge(d_pm.getFlexibleLowerRanges().get(0)).getValue();
		ObservableList<DecisionTreeEdge> edges = d_pm.getOutEdges(upper);
		assertEquals(1, edges.size());
		RangeEdge edge = (RangeEdge) edges.get(0);
		RangeEdge expected = new RangeEdge(0.0, false, Double.POSITIVE_INFINITY, true);
		assertEquals(expected.toString(), edge.toString());
		assertNodeHasCategory((DecisionTreeNode) d_pm.getModelForEdge(edge).getValue(), null);
	}
	
	@Test
	public void testFlexibleDoseNodeChaining() {
		// If MIN_DOSE is considered first, allow MAX_DOSE next
		d_pm.getModelForFlexibleDose().setValue(d_pm.getFlexibleLowerRangeNode());
		RangeEdge edge1 = d_pm.addDefaultRangeEdge(d_pm.getFlexibleLowerRangeNode());
		assertNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge1), FlexibleDose.PROPERTY_MIN_DOSE));
		assertNotNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge1), FlexibleDose.PROPERTY_MAX_DOSE));
		assertTrue(d_pm.getOptionsForEdge(edge1).contains(d_pm.getModelForEdge(edge1).getValue()));
		
		// If MAX_DOSE is considered first, allow MIN_DOSE next
		d_pm.getModelForFlexibleDose().setValue(d_pm.getFlexibleUpperRangeNode());
		RangeEdge edge2 = d_pm.addDefaultRangeEdge(d_pm.getFlexibleUpperRangeNode());
		assertNotNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge2), FlexibleDose.PROPERTY_MIN_DOSE));
		assertNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge2), FlexibleDose.PROPERTY_MAX_DOSE));
		assertTrue(d_pm.getOptionsForEdge(edge2).contains(d_pm.getModelForEdge(edge2).getValue()));
		
		// No MIN_DOSE / MAX_DOSE for fixed dose nodes
		d_pm.getModelForFixedDose().setValue(d_pm.getFixedRangeNode());
		RangeEdge edge3 = d_pm.addDefaultRangeEdge(d_pm.getFixedRangeNode());
		assertNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge3), FlexibleDose.PROPERTY_MIN_DOSE));
		assertNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge3), FlexibleDose.PROPERTY_MAX_DOSE));
		assertTrue(d_pm.getOptionsForEdge(edge3).contains(d_pm.getModelForEdge(edge3).getValue()));
		
		// If MAX_DOSE is considered first, and MIN_DOSE is considered second, no further extra options
		DoseQuantityChoiceNode lower2 = findDoseQuantityNode(d_pm.getOptionsForEdge(edge2), FlexibleDose.PROPERTY_MIN_DOSE);
		d_pm.getModelForEdge(edge2).setValue(lower2);
		RangeEdge edge4 = d_pm.addDefaultRangeEdge(lower2);
		assertNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge4), FlexibleDose.PROPERTY_MIN_DOSE));
		assertNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge4), FlexibleDose.PROPERTY_MAX_DOSE));
		assertTrue(d_pm.getOptionsForEdge(edge4).contains(d_pm.getModelForEdge(edge4).getValue()));
	}
	
	
	@Test
	public void testFlexibleDoseNodeChainingOnSplit() {
		// If MIN_DOSE is considered first, allow MAX_DOSE next
		d_pm.getModelForFlexibleDose().setValue(d_pm.getFlexibleLowerRangeNode());
		RangeEdge edge0 = d_pm.addDefaultRangeEdge(d_pm.getFlexibleLowerRangeNode());
		
		Pair<RangeEdge> splitRange = d_pm.splitRange(edge0, 20.0, false);
		RangeEdge edge1 = splitRange.getFirst();
		RangeEdge edge2 = splitRange.getSecond();
		assertNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge1), FlexibleDose.PROPERTY_MIN_DOSE));
		assertNotNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge1), FlexibleDose.PROPERTY_MAX_DOSE));
		assertTrue(d_pm.getOptionsForEdge(edge1).contains(d_pm.getModelForEdge(edge1).getValue()));
		assertNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge2), FlexibleDose.PROPERTY_MIN_DOSE));
		assertNotNull(findDoseQuantityNode(d_pm.getOptionsForEdge(edge2), FlexibleDose.PROPERTY_MAX_DOSE));
		assertTrue(d_pm.getOptionsForEdge(edge2).contains(d_pm.getModelForEdge(edge2).getValue()));
	}

	private static DoseQuantityChoiceNode findDoseQuantityNode(ObservableList<DecisionTreeNode> options, final String propertyName) {
		return (DoseQuantityChoiceNode) CollectionUtils.find(options, new Predicate<DecisionTreeNode>() {
			public boolean evaluate(DecisionTreeNode object) {
				if (object instanceof DoseQuantityChoiceNode) {
					DoseQuantityChoiceNode node = (DoseQuantityChoiceNode) object;
					return propertyName.equals(node.getPropertyName());
				}
				return false;
			}
		});
	}

	private static void assertNodeHasCategory(DecisionTreeNode node, Category category) {
		assertTrue(node instanceof LeafNode);
		assertEquals(category, ((LeafNode)node).getCategory());
	}
}
