package org.drugis.addis.entities.treatment;

import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.value.ValueHolder;

public class CategoryNode extends DecisionTreeNode {
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
	public boolean equals(Object obj) {
		if(obj instanceof CategoryNode) {
			CategoryNode other = (CategoryNode) obj; 
			return EqualsUtil.equal(getName(), other.getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public DecisionTreeNode clone() throws CloneNotSupportedException {
		return new CategoryNode(getName());
	}
}
