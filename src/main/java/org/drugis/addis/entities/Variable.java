package org.drugis.addis.entities;

import javolution.xml.XMLFormat;
import javolution.xml.XMLFormat.InputElement;
import javolution.xml.XMLFormat.OutputElement;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.OutcomeMeasure.Direction;


public interface Variable extends Entity, Comparable<Variable> {
	
	public enum Type {
		CONTINUOUS("Continuous"),
		RATE("Rate"),
		CATEGORICAL("Categorical");
		
		
		private String d_name;
		
		Type() {
		}
		
		Type(String name) {
			d_name = name;
		}
		
		public String toString() {
			return d_name;
		}
		
		protected static final XMLFormat<Type> XML = new XMLFormat<Type>(Type.class) {
			public Type newInstance(Class<Type> cls, InputElement ie) throws XMLStreamException {
				// In newInstance, only use getAttribute, not get. Thats why no indication can be instantiated at this point
				if (ie.getAttribute("type", null).equals(CONTINUOUS.toString()))
					return Type.CONTINUOUS;
				else if (ie.getAttribute("type", null).equals(RATE.toString()))
					return Type.RATE;
				else
					return Type.CATEGORICAL;
			}
			public boolean isReferenceable() {
				return false;
			}
			public void read(InputElement ie, Type d) throws XMLStreamException {
			}
			public void write(Type d, OutputElement oe) throws XMLStreamException {
				oe.setAttribute("type", d.toString());
			}
		};
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