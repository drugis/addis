package org.drugis.addis.util;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.AssertEntityEquals;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.util.JAXBConvertor.ConversionException;
import org.junit.Before;
import org.junit.Test;

public class JAXBConvertorTest {

	private JAXBContext d_jaxb;
	private Marshaller d_marshaller;
	private Unmarshaller d_unmarshaller;
	
	@Before
	public void setup() throws JAXBException{
		d_jaxb = JAXBContext.newInstance("org.drugis.addis.entities.data" );
		d_unmarshaller = d_jaxb.createUnmarshaller();
		d_marshaller = d_jaxb.createMarshaller();
//		d_unmarshaller.setEventHandler(new AddisDataValidationEventHandler());
	}
	
	@Test
	public void testAddisDataToDomainData() throws JAXBException, XMLStreamException, ConversionException {
		InputStream xmlStream = getClass().getResourceAsStream("defaultData.xml");
		assertNotNull(xmlStream);
		InputStream transformedXmlStream = getClass().getResourceAsStream("defaulttransformed.xml");
		assertNotNull(transformedXmlStream);
		DomainData importedDomainData = (DomainData)XMLHelper.fromXml(xmlStream);
		Domain importedDomain = new DomainImpl(importedDomainData);
		
		AddisData data = (AddisData) d_unmarshaller.unmarshal(transformedXmlStream);
		JAXBConvertor convertor = new JAXBConvertor();
		Domain domainData = convertor.addisDataToDomain(data);
		AssertEntityEquals.assertDomainEquals(importedDomain, domainData);
	}
}
