package org.drugis.addis.entities.treatment;


public class ExcludeNode extends DecisionTreeNode implements LeafNode {
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
	
	public boolean similar(DecisionTreeNode other) {
		return other instanceof ExcludeNode;
	}
}
