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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.TypeEdge;
import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class DecisionTreeChildModelTest {
	private DecisionTreeNode d_root;
	private DecisionTree d_tree;
	private DecisionTreeEdge d_edge;
	private LeafNode d_child1;
	private DecisionTreeChildModel d_model;
	private LeafNode d_child2;

	@Before
	public void setUp() {
		d_root = new ChoiceNode(Object.class, "class");
		d_tree = new DecisionTree(d_root);
		d_edge = new TypeEdge(String.class);
		d_child1 = new LeafNode();
		d_tree.addChild(d_edge, d_root, d_child1);
		d_child2 = new LeafNode();
		d_model = new DecisionTreeChildModel(d_tree, d_edge);
	}

	@Test
	public void testGetValue() {
		assertEquals(d_child1, d_model.getValue());

		d_tree.replaceChild(d_edge, d_child2);
		assertEquals(d_child2, d_model.getValue());

		d_tree.removeChild(d_child2);
		assertNull(d_model.getValue());
	}

	@Test
	public void testSetValue() {
		d_model.setValue(d_child2);

		assertEquals(d_child2, d_tree.getEdgeTarget(d_edge));
		assertEquals(d_child2, d_model.getValue());
	}

	@Test
	public void testReplaceEvents() {
		final PropertyChangeListener listener = EasyMock.createStrictMock(PropertyChangeListener.class);
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(d_model, "value", d_child1, null)));
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(d_model, "value", null, d_child2)));
		EasyMock.replay(listener);

		d_model.addPropertyChangeListener(listener);
		d_tree.replaceChild(d_edge, d_child2);
		EasyMock.verify(listener);
	}
}
