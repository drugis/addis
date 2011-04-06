package org.drugis.addis.entities;

public interface Activity extends Entity {
	
	public String getDescription();

	public boolean deepEquals(Entity other);
}
