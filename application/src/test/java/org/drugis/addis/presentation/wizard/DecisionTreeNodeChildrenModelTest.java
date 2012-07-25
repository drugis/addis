package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseDecisionTree;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.presentation.DecisionTreeNodeChildrenModel;
import org.drugis.common.JUnitUtil;
import org.drugis.common.event.ListDataEventMatcher;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class DecisionTreeNodeChildrenModelTest {
	private DoseDecisionTree d_tree;
	private DecisionTreeNode d_child;
	
	@Before
	public void setUp() {
		d_tree = DoseDecisionTree.createDefaultTree();
		d_child = d_tree.getChildren(d_tree.getRoot()).iterator().next();
	}

	@Test
	public void testInitialization() {
		
		DecisionTreeNodeChildrenModel model1 = new DecisionTreeNodeChildrenModel(d_tree, d_tree.getRoot());
		JUnitUtil.assertAllAndOnly(d_tree.getChildren(d_tree.getRoot()), model1);
		
		DoseDecisionTree tree = new DoseDecisionTree(new EmptyNode());
		DecisionTreeNodeChildrenModel model2 = new DecisionTreeNodeChildrenModel(tree, tree.getRoot());
		assertEquals(Collections.EMPTY_LIST, model2);
	}
	
	@Test
	public void testChangeNodes() {
		DecisionTreeNodeChildrenModel model1 = new DecisionTreeNodeChildrenModel(d_tree, d_child);
		ListDataListener mockListener = EasyMock.createStrictMock(ListDataListener.class);
		mockListener.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(model1, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		mockListener.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(model1, ListDataEvent.INTERVAL_ADDED, 0, 0)));
		EasyMock.replay(mockListener);
		
		model1.addListDataListener(mockListener);
		LeafNode cat1 = new LeafNode(new Category("foo"));
		d_tree.setChild(d_child, cat1);
		d_tree.setChild(cat1, new LeafNode(new Category("bar")));

		JUnitUtil.assertAllAndOnly(d_tree.getChildren(d_child), model1);
		EasyMock.verify(mockListener);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testModify() {
		DecisionTreeNodeChildrenModel model1 = new DecisionTreeNodeChildrenModel(d_tree, d_child);
		model1.add(new LeafNode(new Category("foobar")));
	}
}
