package org.drugis.addis.entities;

public interface Characteristic {

	public abstract boolean equals(Object other);

	public abstract int hashCode();

	public abstract String getDescription();

	public abstract Class<?> getValueType();

}