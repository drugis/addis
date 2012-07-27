package org.drugis.addis.entities.treatment;

import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractNamedEntity;
import org.drugis.addis.entities.Entity;

public class Category extends AbstractNamedEntity<Category> {
	public Category() {
		this("");
	}

	public Category(final String name) {
		super(name);
		d_name = name;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Category) {
			return super.equals(o);
		}
		return false;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
}
