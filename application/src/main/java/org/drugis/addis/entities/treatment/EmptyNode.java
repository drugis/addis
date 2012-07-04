package org.drugis.addis.entities.treatment;

public class EmptyNode extends DecisionTreeNode {

	@Override
	public boolean decide(Object object) {
		return true;
	}

	@Override
	public String getName() {
		return "Empty root node";
	}

}
