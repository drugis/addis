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

import java.io.InputStream;
import java.io.StringReader;

import org.drugis.addis.entities.DomainData;

import javolution.io.AppendableWriter;
import javolution.text.TextBuilder;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.XMLReferenceResolver;
import javolution.xml.stream.XMLStreamException;

public class XMLHelper {

	public static <T> String toXml(T obj, Class<T> cls) throws XMLStreamException {	     
	    TextBuilder xml = TextBuilder.newInstance();
	    AppendableWriter out = new AppendableWriter().setOutput(xml);
		XMLObjectWriter writer = new XMLObjectWriter().setOutput(out).setBinding(new AddisBinding());
		writer.setReferenceResolver(new XMLReferenceResolver());		
		writer.setIndentation("\t");
		if (cls.equals(DomainData.class))
			writer.write(obj, "addis-data", cls);
		else
			writer.write(obj, cls.getCanonicalName(), cls);
		writer.close();
		return xml.toString();
	}

	public static <T> T fromXml(String xml) throws XMLStreamException {	     
		StringReader sreader = new StringReader(xml);
		XMLObjectReader reader = new XMLObjectReader().setInput(sreader).setBinding(new AddisBinding());
		reader.setReferenceResolver(new XMLReferenceResolver());
		return reader.<T>read();
	}
	
	public static <T> T fromXml(InputStream xmlStream) throws XMLStreamException {	     
		//StringReader sreader = new StringReader(xml);
		XMLObjectReader reader = new XMLObjectReader().setInput(xmlStream).setBinding(new AddisBinding());
		reader.setReferenceResolver(new XMLReferenceResolver());
		return reader.<T>read();
	}

}
