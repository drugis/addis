package org.drugis.addis.entities;


public interface OutcomeMeasure extends Comparable<OutcomeMeasure>, Entity {

	public final static String PROPERTY_NAME = "name";
	public final static String PROPERTY_DESCRIPTION = "description";
	public final static String PROPERTY_TYPE = "type";
	public final static String PROPERTY_DIRECTION = "direction";
	public static final String PROPERTY_UNIT_OF_MEASUREMENT = "unitOfMeasurement";
	public static final String UOM_DEFAULT_RATE = "Ratio of Patients";
	public static final String UOM_DEFAULT_CONTINUOUS = "";

	public enum Type {
		CONTINUOUS("Continuous"),
		RATE("Rate");
		
		private String d_name;
		
		Type(String name) {
			d_name = name;
		}
		
		public String toString() {
			return d_name;
		}
	}

	public enum Direction {
		HIGHER_IS_BETTER("Higher is better"),
		LOWER_IS_BETTER("Lower is better");
		
		
		String d_string;
		Direction(String s) {
			d_string = s;
		}
		
		public String toString() {
			return d_string;
		}
	}

	public void setDescription(String description);

	public String getDescription();

	public void setUnitOfMeasurement(String um);

	public String getUnitOfMeasurement();

	public void setName(String name);

	public String getName();

	public void setType(Type type);

	public Type getType();

	public Direction getDirection();
	
	public BasicMeasurement buildMeasurement(Arm a);
}