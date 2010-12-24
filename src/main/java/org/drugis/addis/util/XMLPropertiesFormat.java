/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		Object val = pd.getType() == null ? ie.get(pd.getTagName()) : ie.get(pd.getTagName(), pd.getType());
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
			if (pd.getType() == null) {
				oe.add(pd.getValue(), pd.getTagName());
			} else {
				oe.add(pd.getValue(), pd.getTagName(), pd.getType());
			}
		}
	}
}
