package org.drugis.addis.entities;


public interface Characteristic {

	public String getDescription();

	public Class<?> getValueType();
	
	@Override
	public boolean equals(Object other);
	
	@Override
	public int hashCode();
}