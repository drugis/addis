package org.drugis.addis.util.jaxb;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.drugis.addis.util.jaxb.JAXBConvertor.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TreatmentCategorizationsConverterTest {

	private JAXBConvertorTest d_jaxbConverterTest;
	private static final String TEST_DATA = JAXBConvertorTest.TEST_DATA_PATH + "testDataWithTreatmentCategories.addis";

	@Before 
	public void setUp() throws JAXBException { 
		d_jaxbConverterTest = new JAXBConvertorTest();
		d_jaxbConverterTest.setup();
	}

	@Test
	public void testRoundTripConversion() throws JAXBException, ConversionException, SAXException, IOException, TransformerException {
		d_jaxbConverterTest.doRoundTripTest(getTransformedSavedResultsData());

	}
	
	private static InputStream getTransformedSavedResultsData() throws TransformerException, IOException {
		return JAXBConvertorTest.getTestData(TEST_DATA);
	}
}
