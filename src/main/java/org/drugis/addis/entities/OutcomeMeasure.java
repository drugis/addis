package org.drugis.addis.entities;


public interface OutcomeMeasure extends Variable {

	public final static String PROPERTY_DIRECTION = "direction";
	
	public enum Direction {
		HIGHER_IS_BETTER("Higher is better"),
		LOWER_IS_BETTER("Lower is better");
		
		String d_string;
		
		Direction() {
		}
		
		Direction(String s) {
			d_string = s;
		}
		
		public String toString() {
			return d_string;
		}
	}


	public void setType(Variable.Type type);

	public Variable.Type getType();

	public Direction getDirection();
	
	public BasicMeasurement buildMeasurement();
	
	public BasicMeasurement buildMeasurement(int size);
}