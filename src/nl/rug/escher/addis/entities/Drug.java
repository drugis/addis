package nl.rug.escher.addis.entities;

import com.jgoodies.binding.beans.Model;

public class Drug extends Model {
	private String d_name;
	
	public static final String PROPERTY_NAME = "name";

	public String getName() {
		return d_name;
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
	
	public String toString() {
		return getName();
	}
}
