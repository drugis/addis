/**
 * 
 */
package org.drugis.addis.entities;


public class EntityCategory {
	private final String d_singular;
	private final String d_plural;
	private final String d_property;
	private final Class<? extends Entity> d_entityClass;
	
	public EntityCategory(String singular, String plural, String propertyName,
			Class<? extends Entity> entityClass) {
		d_singular = singular;
		d_plural = plural;
		d_property = propertyName;
		d_entityClass = entityClass;
	}

	public EntityCategory(String singular, String propertyName, 
			Class<? extends Entity> entityClass) {
		this(singular, singular + "s", propertyName, entityClass);
	}
	
	public String getSingular() {
		return d_singular;
	}
	
	public String getPlural() {
		return d_plural;
	}
	
	public String toString() {
		return getPlural();
	}
	
	public String getPropertyName() {
		return d_property;
	}
	
	public Class<? extends Entity> getEntityClass() {
		return d_entityClass;
	}
}