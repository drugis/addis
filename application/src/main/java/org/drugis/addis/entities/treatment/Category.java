package org.drugis.addis.entities.treatment;

import org.drugis.common.beans.AbstractObservable;

public class Category extends AbstractObservable {
	public static final String PROPERTY_NAME = "name";
	private String d_name;
	
	public Category() {
		this("");
	}
	
	public Category(String name) {
		d_name = name;
	}

	public String getName() {
		return d_name;
	}

	public void setName(String newValue) {
		String oldValue = d_name;
		d_name = newValue;
		firePropertyChange(PROPERTY_NAME, oldValue, newValue);
	}
}
