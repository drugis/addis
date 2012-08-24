/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

package org.drugis.addis.util.jaxb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.data.AddisData;

public class JAXBHandler {
	public static class XmlFormatType {
		public static final int INVALID = -1;
		public static final int LEGACY_VERSION = 0;
		public static final int CURRENT_VERSION = currentSchemaVersion();
		
		private final int d_version;

		XmlFormatType(int version) {
			d_version = version;
		}

		public int getVersion() {
			return d_version;
		}
		
		public boolean isLegacy() {
			return d_version == LEGACY_VERSION;
		}
		
		public boolean isFuture() {
			return d_version > CURRENT_VERSION;
		}
		
		public boolean isValid() {
			return d_version > INVALID;
		}
	}
	
	public static int currentSchemaVersion() {
		try {
			InputStream is = Domain.class.getResourceAsStream("current-schema-version");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			return Integer.parseInt(br.readLine());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	private static JAXBContext s_jaxb;

	private static void initialize() throws JAXBException {
		if (s_jaxb == null) {
			s_jaxb = JAXBContext.newInstance("org.drugis.addis.entities.data");
		}
	}
	
	public static void marshallAddisData(AddisData data, OutputStream os) throws JAXBException {
		initialize();
		Marshaller marshaller = s_jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "http://drugis.org/files/addis-" + XmlFormatType.CURRENT_VERSION + ".xsd");
		marshaller.marshal(data, os);
	}
	
	public static AddisData unmarshallAddisData(InputStream is) throws JAXBException {
		initialize();
		Unmarshaller unmarshaller = s_jaxb.createUnmarshaller();
		unmarshaller.setEventHandler(new AddisDataValidationEventHandler());
		return (AddisData) unmarshaller.unmarshal(is);
	}
	
	// should be moved somewhere else and changed
	public static class AddisDataValidationEventHandler implements ValidationEventHandler  {
		public boolean handleEvent(ValidationEvent ve) {
			ValidationEventLocator  locator = ve.getLocator();
			//Print message from valdation event
			System.err.println("Invalid AddisData document: " + locator.getURL());
			System.err.println("Error: " + ve.getMessage());
			//Output line and column number
			System.err.println("Error at column " + locator.getColumnNumber() + 
								", line " + locator.getLineNumber());
			if (ve.getSeverity() == ValidationEvent.ERROR) {
				return true; // keeps unmarshalling
			} else if (ve.getSeverity() == ValidationEvent.FATAL_ERROR) {
				System.err.println("Corrupt AddisData document ... stopped unmarshalling.");
			}
			return false;
		}
	}

	public static XmlFormatType determineXmlType(InputStream is) throws IOException {
		is.mark(1024);
		byte[] buffer = new byte[1024];
		int bytesRead = is.read(buffer);
		if (bytesRead < 0) {
			return new XmlFormatType(XmlFormatType.INVALID);
		}
		String str = new String(buffer, 0, bytesRead);
		
		Pattern addisPattern = Pattern.compile("^(<\\?xml[^\\?]*\\?>[\\s]*)?<addis-data[^>]*>");
		Matcher addisMatcher = addisPattern.matcher(str);
		if (!addisMatcher.find()) {
			return new XmlFormatType(XmlFormatType.INVALID);
		}
		Pattern versionPattern = Pattern.compile("http://drugis.org/files/addis-([0-9]*).xsd");
		Matcher versionMatcher = versionPattern.matcher(str);
		XmlFormatType type = null;
		if (versionMatcher.find()) {
			type = new XmlFormatType(Integer.parseInt(versionMatcher.group(1)));
		} else {
			type = new XmlFormatType(XmlFormatType.LEGACY_VERSION);
		}
		is.reset();
		return type;
	}
}
