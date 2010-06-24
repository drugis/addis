/**
 * 
 */
package org.drugis.addis.entities;


public class EntityCategory {
	private final String d_property;
	private final Class<? extends Entity> d_entityClass;

	public EntityCategory(String propertyName, Class<? extends Entity> entityClass) {
		d_property = propertyName;
		d_entityClass = entityClass;
	}
	
	public String toString() {
		return d_entityClass.getSimpleName();
	}
	
	public String getPropertyName() {
		return d_property;
	}
	
	public Class<? extends Entity> getEntityClass() {
		return d_entityClass;
	}
}