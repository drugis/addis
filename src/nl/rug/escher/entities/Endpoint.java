package nl.rug.escher.entities;

import com.jgoodies.binding.beans.Model;

public class Endpoint extends Model {
	private String d_name;
	private String d_description;
	
	public final static String PROPERTY_NAME = "name";
	public final static String PROPERTY_DESCRIPTION = "description";
	
	public void setDescription(String description) {
		String oldVal = d_description;
		d_description = description;
		firePropertyChange(PROPERTY_DESCRIPTION, oldVal, d_description);
	}
	
	public String getDescription() {
		return d_description;
	}
	
	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
	
	public String getName() {
		return d_name;
	}
}