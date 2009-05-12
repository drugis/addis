package nl.rug.escher.addis.entities;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.jgoodies.binding.beans.Model;

@PersistenceCapable
public class Endpoint extends Model {
	public enum Type {
		CONTINUOUS,
		RATE;
		
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	private String d_name;
	private String d_description;
	private Type d_type;
	
	public final static String PROPERTY_NAME = "name";
	public final static String PROPERTY_DESCRIPTION = "description";
	public final static String PROPERTY_TYPE = "type";
	
	public Measurement buildMeasurement() {
		switch (getType()) {
		case CONTINUOUS:
			return new ContinuousMeasurement(this);
		case RATE:
			return new RateMeasurement(this);
		default:
			throw new IllegalStateException("Not all enum cases covered");
		}
	}
	
	public void setDescription(String description) {
		String oldVal = d_description;
		d_description = description;
		firePropertyChange(PROPERTY_DESCRIPTION, oldVal, d_description);
	}
	
	@Persistent
	public String getDescription() {
		return d_description;
	}
	
	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
	
	@Persistent
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