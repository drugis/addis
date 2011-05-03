package org.drugis.addis.entities;

import org.drugis.common.EqualsUtil;

/**
 * Implements a named entity that has equality and natural order defined based on the name.
 * @param <T> What the subclass should be comparable to.
 */
public abstract class AbstractNamedEntity<T extends TypeWithName> extends AbstractEntity implements Comparable<T>, TypeWithName {
	private String d_name = "";

	public AbstractNamedEntity(String name) {
		d_name = name;
	}
	
	public String getName() {
		return d_name;
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
	

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getName() + ")";
	}
	
	/**
	 * Extenders should override equals to check the type of the other.
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof AbstractNamedEntity<?>) {
			AbstractNamedEntity<?> other = (AbstractNamedEntity<?>) o;
			return EqualsUtil.equal(other.getName(), getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName() == null ? 0 : getName().hashCode();
	}

	public int compareTo(T other) {
		if (other == null) {
			return 1;
		}
		return getName().compareTo(other.getName());
	}
}
