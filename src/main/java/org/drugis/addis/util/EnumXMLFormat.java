/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import javolution.text.CharArray;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

@SuppressWarnings("unchecked")
public class EnumXMLFormat<T extends Enum>  extends XMLFormat<T> {
	
	public EnumXMLFormat(Class<T> c){
		super( c);
	}
	
	@Override
	public T newInstance(Class<T> enumClass, InputElement ie) throws XMLStreamException {
		String selectedOption = ie.getAttribute("value").toString();
		
		return (T) Enum.valueOf(enumClass, selectedOption);
	}
	@Override
	public boolean isReferenceable() {
		return false;
	}
	@Override
	public void read(InputElement ie, T enumInstance) throws XMLStreamException {
	}
	
	@Override
	public void write(T enumInstance, OutputElement oe) throws XMLStreamException {
		oe.setAttribute("value" ,enumInstance.name());
	}

	public static <E extends Enum<E>> E getEnumAttribute(InputElement ie, String attr, E def) throws XMLStreamException {
		CharArray val = ie.getAttribute(attr);
		return val == null ? def : E.valueOf((Class<E>)def.getClass(), val.toString());
	}
}
