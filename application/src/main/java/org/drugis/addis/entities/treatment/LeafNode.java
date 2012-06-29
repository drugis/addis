package org.drugis.addis.entities.treatment;

import javax.naming.OperationNotSupportedException;

import org.drugis.common.beans.AbstractObservable;

public class LeafNode extends AbstractObservable {

	public LeafNode() {
		super();
	}

	public boolean isLeaf() {
		return true;
	}

	public DecisionTreeNode decide(Object object) {
		return null;
	}

	public int getChildCount() {
		return 0;
	}

	public DecisionTreeNode getChildNode(int index) {
		return null;
	}

	public void setChildNode(int index, DecisionTreeNode node) throws OperationNotSupportedException {
		throw new OperationNotSupportedException("Leaf nodes cannot have children.");
	}
}