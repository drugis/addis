/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;

import java.util.TreeSet;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.AssertEntityEquals;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.CharacteristicsMap;
import org.drugis.addis.entities.DomainData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.junit.Before;
import org.junit.Test;

public class XMLLoadSaveTest {
	
	@Before
	public void setUp()  {
	}
	
	@Test
	public void doIndication() throws XMLStreamException {
		Indication i = ExampleData.buildIndicationDepression();
		String xml = XMLHelper.toXml(i, Indication.class);
//		System.out.println("\n"+xml+"\n");
		assertEquals(i, ((Indication)XMLHelper.fromXml(xml)));
		Indication parsedIndication = (Indication)XMLHelper.fromXml(xml);
		AssertEntityEquals.assertEntityEquals(i, parsedIndication);
		
	}
	
	@Test
	public void doEndpoint() throws XMLStreamException {
		Endpoint i = ExampleData.buildEndpointCgi();
		i.setDirection(Direction.LOWER_IS_BETTER);
		i.setType(Type.CATEGORICAL);
		String xml = XMLHelper.toXml(i, Endpoint.class);
//		System.out.println("\n"+xml+"\n");
		Endpoint objFromXml = XMLHelper.fromXml(xml);
		AssertEntityEquals.assertEntityEquals(i, objFromXml);
		i.setDirection(Direction.HIGHER_IS_BETTER);
		i.setType(Type.CONTINUOUS);
	}
	
	@Test
	public void doEndpointHamd() throws XMLStreamException {
		Endpoint i = ExampleData.buildEndpointHamd();
		String xml = XMLHelper.toXml(i, Endpoint.class);
//		System.out.println("\n"+xml+"\n");
		Endpoint objFromXml = XMLHelper.fromXml(xml);
		AssertEntityEquals.assertEntityEquals(i, objFromXml);
	}
	
	@Test
	public void doUnknownDose() throws XMLStreamException {
		UnknownDose d = new UnknownDose();
		String xml = XMLHelper.toXml(d, UnknownDose.class);
		UnknownDose objFromXml = XMLHelper.fromXml(xml);
		assertEquals(d, objFromXml);
	}
	
	@Test
	public void doListOfEndpoints() throws XMLStreamException {
		TreeSet<Endpoint> set = new TreeSet<Endpoint>();
		
		set.add(ExampleData.buildEndpointCgi());
		set.add(ExampleData.buildEndpointHamd());
		set.add(ExampleData.buildEndpointCVdeath());
		
		String xml = XMLHelper.toXml(set,TreeSet.class);
//		System.out.println("\n"+xml+"\n");
		TreeSet<Endpoint> objFromXml = XMLHelper.fromXml(xml);
		
		assertEquals(set, objFromXml);
	}
	
	@Test
	public void doAdverseEvent() throws XMLStreamException {
		AdverseEvent ade = new AdverseEvent("name", Variable.Type.RATE);
		String xml = XMLHelper.toXml(ade, AdverseEvent.class);
//		System.out.println("\n"+xml+"\n");
		AdverseEvent objFromXml = XMLHelper.fromXml(xml);
		AssertEntityEquals.assertEntityEquals(ade, objFromXml);
	}
	
	@Test
	public void doDrug() throws XMLStreamException {
		Drug d = ExampleData.buildDrugParoxetine();
		String xml = XMLHelper.toXml(d, Drug.class);
//		System.out.println(xml);
		AssertEntityEquals.assertEntityEquals(d,(Drug) XMLHelper.fromXml(xml));
		
	}	
	
	@Test
	public void doArm() throws XMLStreamException {
		Arm arm = ExampleData.buildStudyAdditionalThreeArm().getArms().get(0);
		String xml = XMLHelper.toXml(arm, Arm.class);
//		System.out.println(xml);
		Arm parsedArm = XMLHelper.fromXml(xml);
		AssertEntityEquals.assertEntityEquals(arm, parsedArm);
	}	
	
	@Test
	public void doPopulationChars() throws XMLStreamException {
		CategoricalPopulationCharacteristic gender = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
		String xml = XMLHelper.toXml(gender, CategoricalPopulationCharacteristic.class);
//		System.out.println("\n"+xml+"\n");
		CategoricalPopulationCharacteristic objFromXml = XMLHelper.fromXml(xml);
		assertEquals(gender, objFromXml);
	}
	
	@Test
	public void doMap() throws XMLStreamException {
		CharacteristicsMap expectedMap = ExampleData.buildStudyChouinard().getCharacteristics();
		String xml = XMLHelper.toXml(expectedMap, CharacteristicsMap.class);
//		System.out.println("\n"+xml+"\n");
		CharacteristicsMap parsedMap = (CharacteristicsMap)XMLHelper.fromXml(xml);
		AssertEntityEquals.assertEntityEquals(expectedMap, parsedMap);
	}
	
	@Test
	public void doPubMedIdList() throws XMLStreamException {
		PubMedIdList expectedList = new PubMedIdList();
		expectedList.add(new PubMedId("12345"));
		expectedList.add(new PubMedId("5006"));
		String xml = XMLHelper.toXml(expectedList, PubMedIdList.class);
//		System.out.println("\n"+xml+"\n");
		PubMedIdList parsedList = (PubMedIdList)XMLHelper.fromXml(xml);
		assertEquals(expectedList, parsedList);
	}
	
	@Test
	public void doStudy() throws XMLStreamException {
		Study s = ExampleData.buildStudyChouinard();
		Note note = new Note(Source.MANUAL, "this is the test text");
		s.putNote(s.getArms().get(0), note);
		String xml = XMLHelper.toXml(s, Study.class);
//		System.out.println("\n"+xml+"\n");
		Study parsedStudy = new Study();
		parsedStudy = (Study)XMLHelper.fromXml(xml);
		AssertEntityEquals.assertEntityEquals(s, parsedStudy);
		assertEquals(s.getNote(s.getArms().get(0).toString()), parsedStudy.getNote(parsedStudy.getArms().get(0).toString()));
	}
	
	@Test
	public void doMetaAnalysis() throws XMLStreamException {
		NetworkMetaAnalysis analysis = ExampleData.buildNetworkMetaAnalysis();
		String xml = XMLHelper.toXml(analysis, NetworkMetaAnalysis.class);
//		System.out.println("\n"+xml+"\n");		
		NetworkMetaAnalysis importedAnalysis = (NetworkMetaAnalysis)XMLHelper.fromXml(xml);
		assertEntityEquals(analysis, importedAnalysis);
	}
	
	@Test
	public void doFrequencyMeasurement() throws XMLStreamException {
		FrequencyMeasurement measurement = new FrequencyMeasurement(ExampleData.buildGenderVariable());
		String xml = XMLHelper.toXml(measurement, FrequencyMeasurement.class);
		FrequencyMeasurement importedMeasurement = (FrequencyMeasurement)XMLHelper.fromXml(xml);
		assertEntityEquals(measurement, importedMeasurement);
	}
	
	@Test
	public void doDomain() throws XMLStreamException {
		DomainImpl origDomain = new DomainImpl();
		ExampleData.initDefaultData(origDomain);
		DomainData origData = origDomain.getDomainData();
		origData.addVariable(new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"}));
		origData.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysis()); 
		
		String xml = XMLHelper.toXml(origData, DomainData.class);
//		System.out.println("\n"+xml+"\n");
		DomainData loadedData = XMLHelper.fromXml(xml);

		DomainImpl domainFromXml = new DomainImpl();
		domainFromXml.setDomainData(loadedData);
		
		assertEquals(origDomain.getIndications(), loadedData.getIndications());
		AssertEntityEquals.assertDomainEquals(origDomain, domainFromXml);
	}
}
