package nl.rug.escher.addis.entities;

import com.jgoodies.binding.beans.Model;

public class Drug extends Model {
	private static final long serialVersionUID = 5156008576438893074L;

	private String d_name;
	
	public static final String PROPERTY_NAME = "name";
	
	public Drug() {
		
	}

	public Drug(String name) {
		d_name = name;
	}

	public String getName() {
		return d_name;
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Drug) {
			Drug other = (Drug) o;
			if (other.getName() == null) {
				return getName() == null;
			}
			return other.getName().equals(getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName() == null ? 0 : getName().hashCode();
	}
}
