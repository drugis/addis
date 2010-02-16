package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public abstract class AbstractVariable extends AbstractEntity implements Variable {
	private static final long serialVersionUID = 5902516465466960966L;

	protected String d_name;
	String d_description = "";
	protected String d_unitOfMeasurement;
	protected Variable.Type d_type;
	
	protected AbstractVariable(String name, Variable.Type type) {
		d_name = name;
		d_type = type;
				
		if (d_type == Variable.Type.RATE)
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

	public void setType(Variable.Type type) {
		Variable.Type oldVal = d_type;
		d_type = type;
		firePropertyChange(PROPERTY_TYPE, oldVal, d_type);
	}

	public Variable.Type getType() {
		return d_type;
	}

	public int compareTo(Variable other) {
		return getName().compareTo(other.getName());
	}

	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof OutcomeMeasure) {
			if (!(this instanceof OutcomeMeasure)) {
				return false;
			}
			OutcomeMeasure other = (OutcomeMeasure)o;
			return EqualsUtil.equal(other.getName(), getName()); 
		}
		if (o instanceof PopulationCharacteristic) {
			if (!(this instanceof PopulationCharacteristic)) {
				return false;
			}
			PopulationCharacteristic other = (PopulationCharacteristic)o;
			return EqualsUtil.equal(other.getName(), getName()); 
		}
		return false;		
	}

	public BasicMeasurement buildMeasurement(Arm a) {
		return buildMeasurement(a.getSize());
	}

	public BasicMeasurement buildMeasurement(int size) {
		switch (getType()) {
		case CONTINUOUS:
			return new BasicContinuousMeasurement(0.0, 0.0, size);
		case RATE:
			return new BasicRateMeasurement(0, size);
		default:
			throw new IllegalStateException("Not all enum cases covered");
		}
	}
	
	public BasicMeasurement buildMeasurement() {
		return buildMeasurement(0);
	}
}
