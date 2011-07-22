package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class ContinuousVariableType extends AbstractEntity implements VariableType {	
	public static final String PROPERTY_UNIT_OF_MEASUREMENT = "unitOfMeasurement";
	private String d_uom;
	
	public ContinuousVariableType() {
		this(Variable.UOM_DEFAULT_CONTINUOUS);
	}
	
	public ContinuousVariableType(String unitOfMeasurement) {
		d_uom = unitOfMeasurement;
	}

	public String getType() {
		return "Continuous";
	}
	
	public String getUnitOfMeasurement() {
		return d_uom;
	}
	
	public void setUnitOfMeasurement(String unit) {
		String oldValue = d_uom;
		d_uom = unit;
		firePropertyChange(PROPERTY_UNIT_OF_MEASUREMENT, oldValue, d_uom);
	}

	public BasicMeasurement buildMeasurement() {
		return new BasicContinuousMeasurement(null, null, null);
	}

	public BasicMeasurement buildMeasurement(int size) {
		return new BasicContinuousMeasurement(null, null, size);
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof ContinuousVariableType) {
			ContinuousVariableType other = (ContinuousVariableType) o;
			return EqualsUtil.equal(d_uom, other.d_uom);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_uom != null ? d_uom.hashCode() : 0;
	}
	
	@Override
	public String toString() {
		return getType();
	}
}