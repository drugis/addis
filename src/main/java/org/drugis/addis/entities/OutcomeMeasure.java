package org.drugis.addis.entities;

import javolution.xml.XMLFormat;
import javolution.xml.XMLFormat.InputElement;
import javolution.xml.XMLFormat.OutputElement;
import javolution.xml.stream.XMLStreamException;


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
		
		protected static final XMLFormat<Direction> XML = new XMLFormat<Direction>(Direction.class) {
			public Direction newInstance(Class<Direction> cls, InputElement ie) throws XMLStreamException {
				// In newInstance, only use getAttribute, not get. Thats why no indication can be instantiated at this point
				if (ie.getAttribute("direction", null).equals(HIGHER_IS_BETTER.toString()))
					return Direction.HIGHER_IS_BETTER;
				else
					return Direction.LOWER_IS_BETTER;
			}
			public boolean isReferenceable() {
				return true;
			}
			public void read(InputElement ie, Direction d) throws XMLStreamException {
			}
			
			public void write(Direction d, OutputElement oe) throws XMLStreamException {
				oe.setAttribute("direction", d.toString());
			}
		};
	}


	public void setType(Variable.Type type);

	public Variable.Type getType();

	public Direction getDirection();
	
	public BasicMeasurement buildMeasurement();
	
	public BasicMeasurement buildMeasurement(int size);
}