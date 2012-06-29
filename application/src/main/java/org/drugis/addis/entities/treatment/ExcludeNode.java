package org.drugis.addis.entities.treatment;

public class ExcludeNode extends LeafNode {
	public static final String NAME = "Exclude";
	
	public String toString() { 
		return "* " + ExcludeNode.NAME;
	}
	
	public String getName() {
		return NAME;
	}
}
