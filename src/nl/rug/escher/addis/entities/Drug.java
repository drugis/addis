package nl.rug.escher.addis.entities;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.jgoodies.binding.beans.Model;

@PersistenceCapable(identityType=IdentityType.DATASTORE,detachable="true")
public class Drug extends Model {
	private String d_name;
	
	public static final String PROPERTY_NAME = "name";
	
	public Drug() {
		
	}

	public Drug(String name) {
		d_name = name;
	}

	@Persistent //(primaryKey="true")
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
