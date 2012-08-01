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

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.TypeEdge;
import org.drugis.addis.presentation.DecisionTreeOutEdgesModel;
import org.drugis.common.JUnitUtil;
import org.drugis.common.event.ListDataEventMatcher;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class DecisionTreeOutEdgesModelTest {
	private DecisionTree d_tree;
	private DecisionTreeEdge d_edge;
	private LeafNode d_child1;
	private DecisionTreeNode d_root;

	@Before
	public void setUp() {
		d_root = new ChoiceNode(Object.class, "class");
		d_tree = new DecisionTree(d_root);
		d_edge = new TypeEdge(String.class);
		d_child1 = new LeafNode();
		d_tree.addChild(d_edge, d_root, d_child1);
	}

	@Test
	public void testInitialization() {
		final DecisionTreeOutEdgesModel model1 = new DecisionTreeOutEdgesModel(d_tree, d_root);
		JUnitUtil.assertAllAndOnly(d_tree.getOutEdges(d_root), model1);

		final DecisionTreeOutEdgesModel model2 = new DecisionTreeOutEdgesModel(d_tree, d_child1);
		assertEquals(Collections.EMPTY_LIST, model2);
	}

	@Test
	public void testChangeNodes() {
		final DecisionTreeOutEdgesModel model1 = new DecisionTreeOutEdgesModel(d_tree, d_root);
		final ListDataListener mockListener = EasyMock.createStrictMock(ListDataListener.class);
		mockListener.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(model1, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		mockListener.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(model1, ListDataEvent.INTERVAL_ADDED, 0, 0)));
		EasyMock.replay(mockListener);

		model1.addListDataListener(mockListener);
		final LeafNode cat1 = new LeafNode(new Category("foo"));
		d_tree.replaceChild(d_edge, cat1);

		JUnitUtil.assertAllAndOnly(d_tree.getOutEdges(d_root), model1);
		EasyMock.verify(mockListener);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testModify() {
		final DecisionTreeOutEdgesModel model1 = new DecisionTreeOutEdgesModel(d_tree, d_root);
		model1.add(new TypeEdge(Integer.class));
	}
}
