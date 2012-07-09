package org.drugis.addis.entities.treatment;


public class ExcludeNode extends DecisionTreeNode {
	public static final String NAME = "Exclude";
	
	public String toString() { 
		return "* " + ExcludeNode.NAME;
	}
	
	public String getName() {
		return NAME;
	}

	@Override
	public boolean decide(Object object) {
		return true;
	}
	
	@Override
	public DecisionTreeNode clone() throws CloneNotSupportedException {
		return new ExcludeNode();
	}
}
