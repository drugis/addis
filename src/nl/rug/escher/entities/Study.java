package nl.rug.escher.entities;

import com.jgoodies.binding.beans.Model;

public class Study extends Model {
	private String d_id;
	
	public final static String PROPERTY_ID = "id";

	public String getId() {
		return d_id;
	}

	public void setId(String id) {
		String oldVal = d_id;
		d_id = id;
		firePropertyChange(PROPERTY_ID, oldVal, d_id);
	}
	
	public String toString() {
		return getId();
	}
}
