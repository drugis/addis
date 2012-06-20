package org.drugis.addis.entities.treatment;

public class ExcludeNode implements DecisionTreeNode {
	public static final String NAME = "Exclude";
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public DecisionTreeNode decide(Object object) {
		return null;
	}

}
