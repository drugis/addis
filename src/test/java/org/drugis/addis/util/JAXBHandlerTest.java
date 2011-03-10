package org.drugis.addis.util;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBException;

import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.util.JAXBHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

public class JAXBHandlerTest {
	@Test
	public void testUnmarshallMarshallXMLCompare() throws JAXBException, SAXException, FileNotFoundException {
		// read xml file
		AddisData data = JAXBHandler.unmarshallAddisData(new FileInputStream("schema/schematestxml.xml"));

		// write out
		JAXBHandler.marshallAddisData(data, new FileOutputStream("schema/jaxb_marshall_test.xml"));

		// read back generated xml
		AddisData data_clone = JAXBHandler.unmarshallAddisData(new FileInputStream("schema/jaxb_marshall_test.xml"));
		
		// compare
		assertEquals(data, data_clone);
	}
}