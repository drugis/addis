package org.drugis.addis.entities.treatment;

import org.drugis.addis.entities.AbstractDose;

import edu.uci.ics.jung.graph.DelegateTree;

public class DoseDecisionTree extends DelegateTree<DecisionTreeNode, String> {

	private static final long serialVersionUID = 5924217742805415944L;	
	
	public DoseDecisionTree(DecisionTreeNode rootNode) { 
		setRoot(rootNode);
	}
	
	public DecisionTreeNode getCategory(AbstractDose dose) {
		DecisionTreeNode node = searchNode(dose, getRoot());
		return (node != null) ? node : null;
	}

	/**
	 * Add a cut-off value. This splits an existing range in two.
	 * The lower range will always be initialized with the child node of the original range.
	 * @param parent The parent of the set of range nodes to split.
	 * @param value The cut-off value.
	 * @param includeInRightSide True if the value should be included in the range
	 * where it is a lower bound, and excluded where it is an upper bound.
	 * @param rightNode The child node to set for the new upper range, or null to use the child node of the original range. 
	 * @return The index of the range where this cut-off is a lower bound.
	 * @throws IllegalArgumentException If the value does not lie within the specified range for this node, 
	 * or if it is equal to an existing cut-off value.
	 */
	public RangeNode addCutOff(DecisionTreeNode parent, double value, boolean includeInRightSide) {
		RangeNode leftNode = findByValue(parent, value);

		if (leftNode != null) {
			RangeNode rightNode = leftNode.splitOnValue(value, includeInRightSide);
			addChild(parent, rightNode);
			return rightNode;
		}
		return null;
	}
	
	public void addChild(DecisionTreeNode parent, DecisionTreeNode child) {
		String edgeName = parent.hashCode() + child.hashCode() + ""; // needs to be unique
		addChild(edgeName, parent, child);
	}
	
	private RangeNode findByValue(DecisionTreeNode parent, double value) {
		for(DecisionTreeNode child : getChildren(parent)) {
			if(child instanceof RangeNode) {
				if(((RangeNode) child).getInterval().getRange().containsDouble(value)) return (RangeNode) child;
			}
		}
		throw new IllegalArgumentException("No range matches " + value + " in " + parent.getName() );
	}
	
	private DecisionTreeNode searchNode(AbstractDose dose, DecisionTreeNode parent) {
		if (getChildCount(parent) == 0
				&& parent.decide(dose)) {
			return parent;
		}
		for (DecisionTreeNode child : getChildren(parent)) {
			DecisionTreeNode match = null;
			if (child.decide(dose)) {
				match = searchNode(dose, child);
				if (match != null) {
					return match;
				}
			}
		}
		return null;
	}
	
}
