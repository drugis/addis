package org.drugis.addis.entities.treatment;

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
	
	public String getName();
}
