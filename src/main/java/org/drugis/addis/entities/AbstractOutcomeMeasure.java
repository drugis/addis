package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractOutcomeMeasure extends AbstractEntity implements OutcomeMeasure {
	private static final long serialVersionUID = 5902516465466960966L;

	public static final String UOM_DEFAULT_RATE = "Ratio of Patients";
	public static final String UOM_DEFAULT_CONTINUOUS = "";
	protected String d_name;
	String d_description = "";
	protected String d_unitOfMeasurement;
	protected Type d_type;
	public static final String PROPERTY_UNIT_OF_MEASUREMENT = "unitOfMeasurement";

	protected AbstractOutcomeMeasure(String name, Type type) {
		d_name = name;
		d_type = type;
				
		if (d_type == Type.RATE)
			d_unitOfMeasurement = UOM_DEFAULT_RATE;
		else
			d_unitOfMeasurement = UOM_DEFAULT_CONTINUOUS;
	}
	
	public void setDescription(String description) {
		String oldVal = d_description;
		d_description = description;
		firePropertyChange(PROPERTY_DESCRIPTION, oldVal, d_description);
	}

	public String getDescription() {
		return d_description;
	}

	public void setUnitOfMeasurement(String um) {
		String oldVal = d_unitOfMeasurement;
		d_unitOfMeasurement = um;
		firePropertyChange(PROPERTY_UNIT_OF_MEASUREMENT, oldVal, d_unitOfMeasurement);
	}

	public String getUnitOfMeasurement() {
		return d_unitOfMeasurement;
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

	public int compareTo(OutcomeMeasure other) {
		return getName().compareTo(other.getName());
	}

	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractOutcomeMeasure) {
			AbstractOutcomeMeasure other = (AbstractOutcomeMeasure)o;
			if (other.getName() == null && getName() == null) {
				return true;
			}
			return other.getName().equals(getName());
		}
		return false;		
	}

	public BasicMeasurement buildMeasurement(Arm a) {
		switch (getType()) {
		case CONTINUOUS:
			return new BasicContinuousMeasurement(0.0, 0.0, a.getSize());
		case RATE:
			return new BasicRateMeasurement(0, a.getSize());
		default:
			throw new IllegalStateException("Not all enum cases covered");
		}
	}
}
