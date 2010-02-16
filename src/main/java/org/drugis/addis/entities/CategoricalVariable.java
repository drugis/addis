package org.drugis.addis.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class CategoricalVariable extends AbstractOutcomeMeasure implements PopulationCharacteristic {
	private static final long serialVersionUID = 8700874872019027607L;
	private String[] d_categories;
	
	public CategoricalVariable(String name, String[] categories) {
		super(name, Type.CATEGORICAL);
		d_categories = categories;
		d_description = "";
	}

	public String[] getCategories() {
		return d_categories;
	}
	
	public String toString() {
		return getName();
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CategoricalVariable) {
			CategoricalVariable cv = (CategoricalVariable) o;
			if (cv.getName().equals(getName())) {
				return Arrays.equals(cv.getCategories(), getCategories());
			}
		}
		return false;
	}
	
	public FrequencyMeasurement buildMeasurement() {
		return new FrequencyMeasurement(this);
	}

	public int compareTo(Variable other) {
		return getName().compareTo(other.getName());
	}

	public FrequencyMeasurement buildMeasurement(int size) {
		FrequencyMeasurement m = new FrequencyMeasurement(this);
		m.setSampleSize(size);
		return m;
	}
}
