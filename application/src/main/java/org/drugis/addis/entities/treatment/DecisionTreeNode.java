package org.drugis.addis.entities.treatment;



public abstract class DecisionTreeNode {
	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}
}
