package org.drugis.addis.util.convertors;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.drugis.addis.util.JAXBConvertor.ConversionException;
import org.drugis.addis.util.JAXBConvertorTest;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TreatmentCategorizationsConverterTest {

	private JAXBConvertorTest d_jaxbConverterTest;
	private static final String TEST_DATA = "../testDataWithTreatmentCategories.addis";

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
