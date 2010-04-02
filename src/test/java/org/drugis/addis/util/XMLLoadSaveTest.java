package org.drugis.addis.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.DomainData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Variable.Type;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class XMLLoadSaveTest {
	
	@Before
	public void setUp()  {
		
	}
	
	@Test
	public void doIndication() throws XMLStreamException {
		Indication i = ExampleData.buildIndicationDepression();
		String xml = XMLHelper.toXml(i, Indication.class);
//		System.out.println(xml);
		assertEquals(i,XMLHelper.fromXml(xml));
	}
	
	@Test
	public void doEndpoint() throws XMLStreamException {
		Endpoint i = ExampleData.buildEndpointCgi();
		i.setDirection(Direction.LOWER_IS_BETTER);
		i.setType(Type.CATEGORICAL);
		String xml = XMLHelper.toXml(i, Endpoint.class);
		System.out.println("\n"+xml+"\n");
		Endpoint objFromXml = XMLHelper.fromXml(xml);
		assertEquals(i.getDirection(),objFromXml.getDirection());
		assertEquals(i.getType(),objFromXml.getType());
		assertEquals(i, objFromXml);
		i.setDirection(Direction.HIGHER_IS_BETTER);
		i.setType(Type.CONTINUOUS);
	}
	
	@Test
	public void doEndpointHamd() throws XMLStreamException {
		Endpoint i = ExampleData.buildEndpointHamd();
		String xml = XMLHelper.toXml(i, Endpoint.class);
		System.out.println("\n"+xml+"\n");
		Endpoint objFromXml = XMLHelper.fromXml(xml);
		assertEquals(i.getDirection(),objFromXml.getDirection());
		assertEquals(i.getType(),objFromXml.getType());
		assertEquals(i, objFromXml);
	}
	
	@Test
	public void doListOfEndpoints() throws XMLStreamException {
		List<Endpoint> list = new ArrayList();
		
		list.add(ExampleData.buildEndpointCgi());
		list.add(ExampleData.buildEndpointHamd());
		list.add(ExampleData.buildEndpointCVdeath());
		
		XMLSet xmlSet = new XMLSet(list,"endpoints");
		
		String xml = XMLHelper.toXml(xmlSet,XMLSet.class);
		System.out.println("\n"+xml+"\n");
		XMLSet objFromXml = XMLHelper.fromXml(xml);
		
		assertEquals(list,(List) objFromXml.getSet());
	}
	
	@Test
	public void doAdverseEvent() throws XMLStreamException {
		AdverseEvent ade = new AdverseEvent("name", Variable.Type.RATE);
		String xml = XMLHelper.toXml(ade, AdverseEvent.class);
		System.out.println("\n"+xml+"\n");
		AdverseEvent objFromXml = XMLHelper.fromXml(xml);
		assertEquals(ade.getDirection(),objFromXml.getDirection());
		assertEquals(ade.getType(),objFromXml.getType());
		assertEquals(ade, objFromXml);
	}
	
	@Test
	public void doDrug() throws XMLStreamException {
		Drug d = ExampleData.buildDrugParoxetine();
		String xml = XMLHelper.toXml(d, Drug.class);
//		System.out.println(xml);
		assertEquals(d,XMLHelper.fromXml(xml));
	}	
	
	@Test
	public void doArm() throws XMLStreamException {
		Arm d = ExampleData.buildStudyAdditionalThreeArm().getArms().get(0);
		String xml = XMLHelper.toXml(d, Arm.class);
		System.out.println(xml);
		Arm parsedArm = XMLHelper.fromXml(xml);
		assertEquals(d.getDose(), parsedArm.getDose());
		assertEquals(d.getDrug(), parsedArm.getDrug());
		assertEquals(d.getSize(), parsedArm.getSize());
	}	
	
	@Test
	public void doPopulationChars() throws XMLStreamException {
		CategoricalPopulationCharacteristic gender = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
		String xml = XMLHelper.toXml(gender, CategoricalPopulationCharacteristic.class);
		
		System.out.println("\n"+xml+"\n");
		
		CategoricalPopulationCharacteristic objFromXml = XMLHelper.fromXml(xml);
		assertEquals(gender, objFromXml);
	}
	
	@Test
	public void doStudy() throws XMLStreamException {
		Study s = ExampleData.buildStudyChouinard();
//		s.putNote(s.getArms().get(0), new Note(Source.MANUAL, "this is the test text"));
		String xml = XMLHelper.toXml(s, Study.class);
		System.out.println(xml);
		
		Study parsedStudy = new Study();
		parsedStudy = (Study)XMLHelper.fromXml(xml);

		assertEquals(s.getStudyId(),parsedStudy.getStudyId());
		System.out.println(parsedStudy.getMeasurements());
		System.out.println(parsedStudy.getIndication());
		// TODO: characteristicmap
		// TODO: measurements
		// TODO: notes
	}
	
	@Ignore
	public void doDomain() throws XMLStreamException {
		DomainImpl d = new DomainImpl();
		ExampleData.initDefaultData(d);
		DomainData data = d.getDomainData();
		data.addVariable(new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"}));
		
		String xml = XMLHelper.toXml(data, DomainData.class);
		System.out.println(xml);
		DomainData loadedDomainData = XMLHelper.fromXml(xml);
		assertEquals(d.getIndications(), loadedDomainData.getIndications());
		
		// FIXME
	}

}
