package org.drugis.addis.util;

import java.util.List;

import javolution.xml.XMLFormat.InputElement;
import javolution.xml.XMLFormat.OutputElement;
import javolution.xml.stream.XMLStreamException;

/**
 * Format (or read) a list of properties to (from) XML using Javalution.  
 */
public class XMLPropertiesFormat {
	/**
	 * Element definition: how to read/write a field of a class. 
	 */
	public abstract static class PropertyDefinition<T> {
		private final String d_tagName;
		private final Class<T> d_cls;

		public PropertyDefinition(String tagName, Class<T> cls) {
			d_tagName = tagName;
			d_cls = cls;
		}
		
		public abstract void setValue(Object val);
		public abstract T getValue();
		
		public String getTagName() {
			return d_tagName;
		}
		
		public Class<T> getType() {
			return d_cls;
		}
	}
	
	/**
	 * Element definition for element that is to be ignored/discarded. 
	 */
	public static class NullPropertyDefinition<T> extends PropertyDefinition<T> {
		public NullPropertyDefinition(String tagName, Class<T> cls) { super(tagName, cls); }
		public T getValue() { return null; }
		public void setValue(Object v) { }
	};
	
	/**
	 * Read a list of properties from ie, where they may be a part of ie in any order.
	 * @param ie A Javalution XML input element
	 * @param props The list of properties to be read
	 * @throws XMLStreamException 
	 */
	@SuppressWarnings("unchecked")
	public static void readProperties(InputElement ie, List<PropertyDefinition> props) throws XMLStreamException {
		while(ie.hasNext()) {
			for (PropertyDefinition<?> propertyDefinition : props) {
				if (tryRead(ie, propertyDefinition)) {
					break;
				}
			}
		}
	}
	
	private static boolean tryRead(InputElement ie, PropertyDefinition<?> pd) throws XMLStreamException {
		Object val = ie.get(pd.getTagName(), pd.getType());
		if (val != null) {
			pd.setValue(val);
			return true;
		}
		return false;
	}

	/**
	 * Write a list of properties to oe, in list order.
	 * @param props The list of properties to be written
	 * @param oe A Javalution XML output element
	 * @throws XMLStreamException 
	 */
	@SuppressWarnings("unchecked")
	public static void writeProperties(List<PropertyDefinition> props, OutputElement oe) throws XMLStreamException {
		for (PropertyDefinition pd : props) {
			oe.add(pd.getValue(), pd.getTagName(), pd.getType());
		}
	}
}
