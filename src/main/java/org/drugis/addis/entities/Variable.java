package org.drugis.addis.entities;


public interface Variable extends Entity, Comparable<Variable> {
	
	public enum Type {
		CONTINUOUS("Continuous"),
		RATE("Rate"),
		CATEGORICAL("Categorical");
		
		
		private String d_name;
		
		Type(String name) {
			d_name = name;
		}
		
		public String toString() {
			return d_name;
		}
	}

	public static final String PROPERTY_NAME = "name";
	public final static String PROPERTY_TYPE = "type";	
	public final static String PROPERTY_DESCRIPTION = "description";
	public static final String UOM_DEFAULT_RATE = "Ratio of Patients";
	public static final String UOM_DEFAULT_CONTINUOUS = "";
	public static final String PROPERTY_UNIT_OF_MEASUREMENT = "unitOfMeasurement";

	public void setDescription(String description);

	public String getDescription();

	public void setUnitOfMeasurement(String um);

	public String getUnitOfMeasurement();

	public void setName(String name);

	public String getName();

	public Variable.Type getType();
	
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
}