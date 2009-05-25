package nl.rug.escher.addis.entities;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.jgoodies.binding.beans.Model;

@PersistenceCapable(identityType=IdentityType.APPLICATION,detachable="true")
public class Endpoint extends Model {
	public enum Type {
		CONTINUOUS,
		RATE;
		
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	@Persistent(primaryKey="true")
	private String d_name;
	@Persistent
	private String d_description;
	@Persistent
	private Type d_type;
	
	public final static String PROPERTY_NAME = "name";
	public final static String PROPERTY_DESCRIPTION = "description";
	public final static String PROPERTY_TYPE = "type";
	
	public Endpoint() {
		
	}
	
	public Endpoint(String name) {
		d_name = name;
	}
	
	public BasicMeasurement buildMeasurement() {
		switch (getType()) {
		case CONTINUOUS:
			return new BasicContinuousMeasurement(this);
		case RATE:
			return new BasicRateMeasurement(this);
		default:
			throw new IllegalStateException("Not all enum cases covered");
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof Endpoint) {
			Endpoint other = (Endpoint)o;
			if (other.getName() == null) {
				return getName() == null;
			}
			return other.getName().equals(getName());
		}
		return false;
	}
	
	public int hashCode() {
		if (d_name != null) {
			return d_name.hashCode();
		}
		return 0;
	}
	
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
	
	public String toString() {
		return getName();
	}

	public void setType(Type type) {
		Type oldVal = d_type;
		d_type = type;
		firePropertyChange(PROPERTY_TYPE, oldVal, d_type);
	}

	public Type getType() {
		return d_type;
	}
}