package org.drugis.addis.entities.treatment;

import org.drugis.common.beans.AbstractObservable;

import com.jgoodies.binding.value.ValueHolder;

public class CategoryNode extends AbstractObservable implements DecisionTreeNode {
	public static final String PROPERTY_NAME = "name";
	private String d_name;
	private ValueHolder d_nameModel;

	public CategoryNode() { 
		this("");
	}
	
	public CategoryNode(String name) {
		d_name = name;
		d_nameModel = new ValueHolder(d_name);
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
	public DecisionTreeNode decide(Object object) {
		return null;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
	
	public String toString() { 
		return getName();
	}
}
