/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.util.JAXBHandler.XmlFormatType;
import org.junit.Test;
import org.xml.sax.SAXException;

public class JAXBHandlerTest {
	private static final String V3_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
	"<addis-data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
	"            xsi:noNamespaceSchemaLocation=\"http://drugis.org/files/addis-3.xsd\">\n" +
	"</addis-data>\n";
	
	private static final String V2_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
	"<addis-data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://drugis.org/files/addis-2.xsd\">\n" +
	"</addis-data>\n";
	
	private static final String LEGACY_XML = "<?xml version=\"1.0\" ?>\n" +
	"<addis-data>\n" +
	"</addis-data>\n";

	private static final String OTHER_XML = "<?xml version=\"1.0\" ?>\n" +
	"<oranges>\n" +
	"</oranges>\n";

	
	@Test
	public void testUnmarshallMarshallXmlCompare() throws JAXBException, SAXException, TransformerException, IOException {
		// read xml file
		AddisData data = JAXBHandler.unmarshallAddisData(JAXBConvertor.transformToLatest(JAXBHandlerTest.class.getResourceAsStream("schema_test.xml"), 1));

		// write out
		String testFile = "jaxb_marshall_test.xml";
		JAXBHandler.marshallAddisData(data, new FileOutputStream(testFile));

		// read back generated xml
		AddisData data_clone = JAXBHandler.unmarshallAddisData(new FileInputStream(testFile));
		
		// compare
		assertEquals(data, data_clone);
		
		File temp = new File(testFile);
		temp.delete();
	}
	
	@Test
	public void determineXmlVersionTest() throws IOException {
		ByteArrayInputStream emptyInput = new ByteArrayInputStream("".getBytes());
		XmlFormatType emptyVersion = JAXBHandler.determineXmlType(emptyInput);
		assertFalse(emptyVersion.isValid());
		assertFalse(emptyVersion.isLegacy());
		
		ByteArrayInputStream v3input = new ByteArrayInputStream(V3_XML.getBytes());
		XmlFormatType v3version = JAXBHandler.determineXmlType(v3input);
		assertTrue(v3version.isValid());
		assertFalse(v3version.isLegacy());
		assertEquals(3, v3version.getVersion());
		
		ByteArrayInputStream v2input = new ByteArrayInputStream(V2_XML.getBytes());
		XmlFormatType v2version = JAXBHandler.determineXmlType(v2input);
		assertTrue(v2version.isValid());
		assertFalse(v2version.isLegacy());
		assertEquals(2, v2version.getVersion());
		
		ByteArrayInputStream v0input = new ByteArrayInputStream(LEGACY_XML.getBytes());
		XmlFormatType v0version = JAXBHandler.determineXmlType(v0input);
		assertTrue(v0version.isValid());
		assertTrue(v0version.isLegacy());
		
		ByteArrayInputStream otherInput = new ByteArrayInputStream(OTHER_XML.getBytes());
		XmlFormatType otherVersion = JAXBHandler.determineXmlType(otherInput);
		assertFalse(otherVersion.isValid());
	}
	
}