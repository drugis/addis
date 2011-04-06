package org.drugis.addis.entities;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Set;

public enum PredefinedActivity implements Activity {
	RANDOMIZATION("Randomization"),
	SCREENING("Screening"),
	WASH_OUT("Wash out"),
	FOLLOW_UP("Follow up");

	private final String d_description;

	PredefinedActivity(String description) {
		d_description = description;
	}
	
	public String getDescription() {
		return d_description;
	}

	/**
	 * Deep equality and shallow equality are equivalent for this type.
	 */
	public boolean deepEquals(Entity other) {
		return equals(other);
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {}
	public void removePropertyChangeListener(PropertyChangeListener listener) {}
}
