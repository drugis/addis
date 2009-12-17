package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

public class ContinuousVariable extends AbstractEntity implements Variable {
	private static final long serialVersionUID = -7658488761002395117L;
	
	private String d_name;
	
	public ContinuousVariable(String name) {
		d_name = name;
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	public Measurement buildMeasurement() {
		return new BasicContinuousMeasurement(0, 0, 0);
	}

	public String getName() {
		return d_name;
	}
}
