package org.drugis.addis.entities.treatment;

import org.drugis.common.beans.AbstractObservable;


public abstract class DecisionTreeNode extends AbstractObservable implements Cloneable {
	/**
	 * Classify the given object.
	 * @param object Object to classify.
	 * @return The relevant child node. null if and only if {@link #isLeaf()}.
	 */
	public abstract boolean decide(Object object);
	
	public abstract String getName();
	
	public abstract boolean similar(DecisionTreeNode other);
	
	public Class<?> getBeanClass() { 
		return null;
	}
	
	public String getPropertyName() {
		return "";
	}
	
	public String toString() { 
		return getName();
	}
	
	public DecisionTreeNode clone() throws CloneNotSupportedException { 
		return (DecisionTreeNode) super.clone();
	}
}
