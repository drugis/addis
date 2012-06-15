package org.drugis.addis.entities.treatment;

public class ExcludeNode implements DecisionTreeNode {
	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public DecisionTreeNode decide(Object object) {
		return null;
	}

}
