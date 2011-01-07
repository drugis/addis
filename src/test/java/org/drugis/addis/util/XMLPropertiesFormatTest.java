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

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLPropertiesFormat.NullPropertyDefinition;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

public class XMLPropertiesFormatTest {
	
	public static class SerializeMeIfYouCan {
		public String d_name = null;
		public Integer d_count = null;

		public SerializeMeIfYouCan() { }
		
		PropertyDefinition<?>[] properties = new PropertyDefinition[] {
			new PropertyDefinition<String>("name", String.class) {
				public String getValue() { return d_name; }
				public void setValue(Object v) { d_name = (String) v; }
			},
			new PropertyDefinition<Integer>("count", Integer.class) {
				public Integer getValue() { return d_count; }
				public void setValue(Object v) { d_count = (Integer) v; }
			}
		};
	}
	
	public static final XMLFormat<SerializeMeIfYouCan> XML = new XMLFormat<SerializeMeIfYouCan>(SerializeMeIfYouCan.class) {

		@SuppressWarnings("unchecked")
		@Override
		public void read(InputElement ie, SerializeMeIfYouCan obj) throws XMLStreamException {
			XMLPropertiesFormat.readProperties(ie, Arrays.asList(obj.properties));
		}

		@SuppressWarnings("unchecked")
		@Override
		public void write(SerializeMeIfYouCan obj, OutputElement oe) throws XMLStreamException {
			XMLPropertiesFormat.writeProperties(Arrays.asList(obj.properties), oe);
		}
	};
	
	public static class TestBinding extends XMLBinding {
		private static final long serialVersionUID = -8449260196866847926L;

		@SuppressWarnings("unchecked")
		@Override
		public XMLFormat getFormat(Class cls) throws XMLStreamException {
	    	if (cls.equals(SerializeMeIfYouCan.class)) {
	    		return XML;
	    	} else {
	    		return super.getFormat(cls);
	    	}
	    }
	}
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testElementDefinition() {
		NullPropertyDefinition<Integer> npd = new NullPropertyDefinition<Integer>("Annie", Integer.class);
		assertEquals("Annie", npd.getTagName());
		assertEquals(Integer.class, npd.getType());
	}
	
	@Test
	public void testRead() throws XMLStreamException {
		String xml = "<element><name value=\"test\"/><count value=\"3\"/></element>";
		StringReader sreader = new StringReader(xml);
		XMLObjectReader reader = new XMLObjectReader().setInput(sreader).setBinding(new TestBinding());
		SerializeMeIfYouCan yesICan = reader.read("element", SerializeMeIfYouCan.class);
		assertEquals("test", yesICan.d_name);
		assertEquals(new Integer(3), yesICan.d_count);
	}
	
	@Test
	public void testRead2() throws XMLStreamException {
		String xml = "<element><count value=\"3\"/><name value=\"test\"/></element>";
		StringReader sreader = new StringReader(xml);
		XMLObjectReader reader = new XMLObjectReader().setInput(sreader).setBinding(new TestBinding());
		SerializeMeIfYouCan yesICan = reader.read("element", SerializeMeIfYouCan.class);
		assertEquals("test", yesICan.d_name);
		assertEquals(new Integer(3), yesICan.d_count);
	}
	
	
	@Test
	public void testWrite() throws XMLStreamException {
		SerializeMeIfYouCan yesYouCan = new SerializeMeIfYouCan();
		yesYouCan.d_count = 12;
		yesYouCan.d_name = "JYU";
		StringWriter out = new StringWriter();
		XMLObjectWriter writer = new XMLObjectWriter().setOutput(out).setBinding(new TestBinding());
		writer.write(yesYouCan, "element", SerializeMeIfYouCan.class);
		writer.close();

		StringReader sreader = new StringReader(out.toString());
		XMLObjectReader reader = new XMLObjectReader().setInput(sreader).setBinding(new TestBinding());
		SerializeMeIfYouCan yesICan = reader.read("element", SerializeMeIfYouCan.class);
		assertEquals("JYU", yesICan.d_name);
		assertEquals(new Integer(12), yesICan.d_count);
	}
}
