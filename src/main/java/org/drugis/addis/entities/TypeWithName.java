package org.drugis.addis.entities;

import com.jgoodies.binding.beans.Observable;

/**
 * Type with a (string) name property.
 * Implementers must fire bean events for name changes. 
 */
public interface TypeWithName extends Observable {
	public static final String PROPERTY_NAME = "name";
	
	public String getName();
	
	/**
	 * Set the name. Implementers must fire bean events for name changes.
	 */
	public void setName(String name);
}
