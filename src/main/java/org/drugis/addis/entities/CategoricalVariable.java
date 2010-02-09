package org.drugis.addis.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.OutcomeMeasure.Type;

public class CategoricalVariable extends AbstractEntity implements Variable {
	private static final long serialVersionUID = 8700874872019027607L;
	private String[] d_categories;
	private String d_name;
	
	public CategoricalVariable(String name, String[] categories) {
		d_name = name;
		d_categories = categories;
	}

	public String[] getCategories() {
		return d_categories;
	}

	/* (non-Javadoc)
	 * @see org.drugis.addis.entities.Variable#getName()
	 */
	public String getName() {
		return d_name;
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

	public Type getType() {
		return Type.CATEGORICAL;
	}
}
