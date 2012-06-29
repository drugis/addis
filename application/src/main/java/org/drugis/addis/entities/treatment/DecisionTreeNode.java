package org.drugis.addis.entities.treatment;

import javax.naming.OperationNotSupportedException;


public interface DecisionTreeNode {
	/**
	 * @return True if this node is a leaf node (has no children).
	 */
	public boolean isLeaf();
	
	/**
	 * Classify the given object.
	 * @param object Object to classify.
	 * @return The relevant child node. null if and only if {@link #isLeaf()}.
	 */
	public DecisionTreeNode decide(Object object);
	
	/**
	 * @return the number of children of this node
	 */
	public int getChildCount();
	
	/**
	 * Gets a child by an index
	 * @return the DecisionTreeNode node
	 */
	public DecisionTreeNode getChildNode(int index);
	
	/**
	 * Set the child node for the index-th range.
	 * @param index Index of the range.
	 * @param node Desired child node.
	 * @throws OperationNotSupportedException If node is a leaf node
	 * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #getChildCount()}.
	 */
	public void setChildNode(int index, DecisionTreeNode node) throws OperationNotSupportedException;
	
	public String getName();
}
