package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

public class ContinuousVariable extends AbstractOutcomeMeasure implements PopulationCharacteristic {
	private static final long serialVersionUID = -1047329092617146770L;

	public ContinuousVariable(String name) {
		super(name, Type.CONTINUOUS);
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	public BasicContinuousMeasurement buildMeasurement() {
		return new BasicContinuousMeasurement(0, 0, 0);
	}

	public String toString() {
		return getName();
	}

	public int compareTo(Variable other) {
		return getName().compareTo(other.getName()); 
	}

	public BasicContinuousMeasurement buildMeasurement(int size) {
		return new BasicContinuousMeasurement(0, 0, size);
	}
}
