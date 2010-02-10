package org.drugis.addis.entities;

import org.drugis.addis.entities.OutcomeMeasure.Type;

public interface Variable extends Entity, Comparable<Variable> {
	
	public static final String PROPERTY_NAME = "name";
	public final static String PROPERTY_TYPE = "type";	

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
	
	/**
	 * Build a Measurement on this variable.
	 * @param size Default group size
	 * @return An appropriate type of Measurement.
	 */
	public Measurement buildMeasurement(int size);

	public Type getType();
}