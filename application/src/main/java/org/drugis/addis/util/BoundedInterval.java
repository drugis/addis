package org.drugis.addis.util;

import org.apache.commons.lang.math.DoubleRange;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.common.beans.AbstractObservable;

public class BoundedInterval extends AbstractObservable {
	public static String PROPERTY_NODE = "node";
	
	private final DoubleRange d_range;
	private final boolean d_lowerBoundIsOpen;
	private final boolean d_upperBoundIsOpen;
	private DecisionTreeNode d_node;
	
	public BoundedInterval(DoubleRange range, boolean lowerBoundIsOpen, boolean upperBoundIsOpen) {
		d_range = range;
		d_lowerBoundIsOpen = lowerBoundIsOpen;
		d_upperBoundIsOpen = upperBoundIsOpen;
	}

	public BoundedInterval(double lowerBound, boolean lowerBoundIsOpen, double upperBound, boolean upperBoundIsOpen) {
		this(new DoubleRange(
				 lowerBound + (lowerBoundIsOpen ? RangeNode.EPSILON : 0), 
				 upperBound	- (upperBoundIsOpen ? RangeNode.EPSILON : 0)),
				 lowerBoundIsOpen,
				 upperBoundIsOpen);
	}

	public DoubleRange getRange() {
		return d_range;
	}

	public boolean isLowerBoundOpen() {
		return d_lowerBoundIsOpen;
	}

	public boolean isUpperBoundOpen() {
		return d_upperBoundIsOpen;
	}

	public DecisionTreeNode getNode() {
		return d_node;
	}

	public void setNode(DecisionTreeNode node) {
		DecisionTreeNode old = d_node;
		d_node = node;
		firePropertyChange(PROPERTY_NODE, old, d_node);
	}
}