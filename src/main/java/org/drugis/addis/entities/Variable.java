package org.drugis.addis.entities;

public interface Variable extends Entity, Comparable<Variable> {
	
	public static final String PROPERTY_NAME = "name";

	/**
	 * The name of this variable.
	 * @return Name.
	 */
	public String getName();
	
	/**
	 * Build a Measurement on this variable.
	 * @return An appropriate type of Measurement.
	 */
	public Measurement buildMeasurement();
}