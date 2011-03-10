package org.drugis.addis.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.drugis.addis.entities.data.AddisData;

public class JAXBHandler {
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
		marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "http://drugis.org/files/addis-1.xsd");
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
}
