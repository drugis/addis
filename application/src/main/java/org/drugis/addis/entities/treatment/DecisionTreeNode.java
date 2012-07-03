package org.drugis.addis.entities.treatment;

import org.drugis.common.beans.AbstractObservable;


public abstract class DecisionTreeNode extends AbstractObservable {
	/**
	 * Classify the given object.
	 * @param object Object to classify.
	 * @return The relevant child node. null if and only if {@link #isLeaf()}.
	 */
	public abstract boolean decide(Object object);
	
	public abstract String getName();
}
