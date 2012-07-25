package org.drugis.addis.entities.treatment;

public interface DecisionTreeEdge {
	/**
	 * Accept or reject the given object.
	 * @param object Object to classify.
	 * @return True for accept, false for reject.
	 */
	public boolean decide(Object object);
}
