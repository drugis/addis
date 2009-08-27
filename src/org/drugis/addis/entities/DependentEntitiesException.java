package org.drugis.addis.entities;

import java.util.Collection;

@SuppressWarnings("serial")
public class DependentEntitiesException extends Exception {

	private Collection<Entity> d_dependents;

	public DependentEntitiesException(Collection<Entity> dependents) {
		this.d_dependents = dependents;
	}
	
	public Collection<Entity> getDependents() {
		return d_dependents;
	}
	
}
