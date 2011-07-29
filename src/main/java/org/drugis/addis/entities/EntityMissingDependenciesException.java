package org.drugis.addis.entities;

import java.util.Collection;

public class EntityMissingDependenciesException extends RuntimeException {
	private Collection<Entity> d_dependencies;

	public EntityMissingDependenciesException(Collection<Entity> dependencies) {
		super("Missing dependencies: " + dependencies);
		d_dependencies = dependencies;
	}
	
	public Collection<Entity> getDependencies() {
		return d_dependencies;
	}
}
