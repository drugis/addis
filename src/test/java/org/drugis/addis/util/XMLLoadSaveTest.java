package org.drugis.addis.util;

import static org.junit.Assert.*;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class XMLLoadSaveTest {
	
	@Before
	public void setUp()  {
		
	}
	
	@Ignore
	public void doStudy() throws XMLStreamException {
		Study s = ExampleData.buildStudyChouinard();
		String xml = XMLHelper.toXml(s, Study.class);
		System.out.println(xml);
		assertEquals(s,XMLHelper.fromXml(xml));
	}
	
	@Test
	public void doIndication() throws XMLStreamException {
		Indication i = ExampleData.buildIndicationDepression();
		String xml = XMLHelper.toXml(i, Indication.class);
//		System.out.println(xml);
		assertEquals(i,XMLHelper.fromXml(xml));
	}

}
