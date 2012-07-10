package org.drugis.addis.entities.treatment;

import com.jgoodies.binding.value.ValueHolder;

public class CategoryNode extends DecisionTreeNode implements LeafNode {
	public static final String PROPERTY_NAME = "name";
	private ValueHolder d_nameModel;

	public CategoryNode() { 
		this("");
	}
	
	public CategoryNode(String name) {
		d_nameModel = new ValueHolder(name);
	}
	
	public String getName() {
		return (String) d_nameModel.getValue();
	}
	
	public ValueHolder getNameModel() { 
		return d_nameModel;
	}
	
	public void setName(String name) { 
		d_nameModel.setValue(name);
	}

	@Override
	public boolean decide(Object object) {
		return true;
	}
	
	@Override
	public DecisionTreeNode clone() throws CloneNotSupportedException {
		return new CategoryNode(getName());
	}
}
