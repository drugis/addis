package org.drugis.addis.entities.treatment;

import javax.naming.OperationNotSupportedException;

public abstract class LeafNode extends DecisionTreeNode {

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
	
	public String getChildLabel(int index) {
		return "";
	}
	
	public String toString() { 
		return getName();
	}
 }