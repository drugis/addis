package org.drugis.addis.entities;

public interface VariableType extends Entity {
	public final static String PROPERTY_TYPE = "type";
	
	/**
	 * Build a Measurement on this variable.
	 * @return An appropriate type of Measurement.
	 */
	public BasicMeasurement buildMeasurement();
	
	/**
	 * Build a Measurement on this variable.
	 * @param size Default group size
	 * @return An appropriate type of Measurement.
	 */
	public BasicMeasurement buildMeasurement(int size);

	/**
	 * Human-readable representation of the type name. 
	 */
	public String getType();
}
