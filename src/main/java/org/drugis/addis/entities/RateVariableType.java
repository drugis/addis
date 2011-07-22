package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

public class RateVariableType extends AbstractEntity implements VariableType {
	public BasicMeasurement buildMeasurement() {
		return new BasicRateMeasurement(null, null);
	}

	public BasicMeasurement buildMeasurement(int size) {
		return new BasicRateMeasurement(null, size);
	}

	public String getType() {
		return "Rate";
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof RateVariableType);
	}
	
	@Override
	public String toString() {
		return getType();
	}
}
