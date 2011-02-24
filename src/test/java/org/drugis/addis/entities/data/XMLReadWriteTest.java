package org.drugis.addis.entities.data;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XMLReadWriteTest {
	private JAXBContext d_jaxb;
	private Unmarshaller d_unmarshaller;
	private Marshaller d_marshaller;

	// should be moved somewhere else and changed
	public class AddisDataValidationEventHandler implements ValidationEventHandler  {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator  locator = ve.getLocator();
				//Print message from valdation event
				System.err.println("Invalid AddisData document: " + locator.getURL());
				System.err.println("Error: " + ve.getMessage());
				//Output line and column number
				System.err.println("Error at column " + locator.getColumnNumber() + 
									", line " + locator.getLineNumber());
				return true; // keeps unmarshalling
			} else if (ve.getSeverity() == ValidationEvent.FATAL_ERROR) {
				System.err.println("Corrupt AddisData document ... stopped unmarshalling.");
			}
			return false;
		}
	}
	
	@Before
	public void setup() throws JAXBException{
		d_jaxb = JAXBContext.newInstance("org.drugis.addis.entities.data" );
		d_unmarshaller = d_jaxb.createUnmarshaller();
		d_marshaller = d_jaxb.createMarshaller();
		d_unmarshaller.setEventHandler(new AddisDataValidationEventHandler());
	}
	
	@Test
	public void testUnmarshallMarshallXMLCompare() throws JAXBException, SAXException, FileNotFoundException {
		// read xml file
		AddisData data = (AddisData) d_unmarshaller.unmarshal(new File("schema/schematestxml.xml"));
		
		// write out
		d_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		d_marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "http://drugis.org/files/addis-1.xsd");
		d_marshaller.marshal(data, new FileOutputStream("schema/jaxb_marshall_test.xml"));

		// read back generated xml
		AddisData data_clone = (AddisData) d_unmarshaller.unmarshal(new File("schema/jaxb_marshall_test.xml"));
		// compare
		assertEquals(data, data_clone);
	}
}