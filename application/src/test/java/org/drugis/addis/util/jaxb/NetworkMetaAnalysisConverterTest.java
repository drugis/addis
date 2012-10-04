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

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.util.jaxb.JAXBConvertor;
import org.drugis.addis.util.jaxb.NetworkMetaAnalysisConverter;
import org.drugis.addis.util.jaxb.JAXBConvertor.ConversionException;
import org.drugis.addis.util.jaxb.JAXBConvertorTest.MetaAnalysisWithStudies;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class NetworkMetaAnalysisConverterTest {

	private JAXBConvertorTest d_jaxbConverterTest;
	private static final String TEST_DATA_WITH_RESULTS = JAXBConvertorTest.TEST_DATA_PATH + "testDataSavedResults.addis"; // note: saved results in Dizziness MA

	@Before 
	public void setUp() throws JAXBException { 
		d_jaxbConverterTest = new JAXBConvertorTest();
		d_jaxbConverterTest.setup();
	}

	@Test
	public void testConvertNetworkMetaAnalysis() throws Exception,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		String name = "CGI network meta-analysis";
		MetaAnalysisWithStudies ma = d_jaxbConverterTest.buildNetworkMetaAnalysis(name);

		List<Study> studies = new ArrayList<Study>();
		for (org.drugis.addis.entities.data.Study study : ma.d_studies) {
			Study studyEnt = JAXBConvertor.convertStudy(study, domain);
			domain.getStudies().add(studyEnt);
			studies.add(studyEnt);
		}

		TreatmentDefinition combi = TreatmentDefinition.createTrivial(Arrays.asList(
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugSertraline()));
		TreatmentDefinition parox = TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine());
		TreatmentDefinition sertr = TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline());
		SortedSet<TreatmentDefinition> alternatives = new TreeSet<TreatmentDefinition>();
		alternatives.add(combi);
		alternatives.add(parox);
		alternatives.add(sertr);
		Map<Study, Map<TreatmentDefinition, Arm>> armMap = new HashMap<Study, Map<TreatmentDefinition, Arm>>();
		Map<TreatmentDefinition, Arm> study1map = new HashMap<TreatmentDefinition, Arm>();
		study1map.put(combi, studies.get(0).getArms().get(0));
		study1map.put(sertr, studies.get(0).getArms().get(1));
		armMap.put(studies.get(0), study1map);
		Map<TreatmentDefinition, Arm> study2map = new HashMap<TreatmentDefinition, Arm>();
		study2map.put(parox, studies.get(1).getArms().get(0));
		study2map.put(sertr, studies.get(1).getArms().get(1));
		armMap.put(studies.get(1), study2map);
		Map<TreatmentDefinition, Arm> study3map = new HashMap<TreatmentDefinition, Arm>();
		study3map.put(sertr, studies.get(2).getArms().get(0));
		study3map.put(parox, studies.get(2).getArms().get(1));
		study3map.put(combi, studies.get(2).getArms().get(2));
		armMap.put(studies.get(2), study3map);

		Collections.sort(studies); // So the reading *by definition* puts the studies in their natural order
		NetworkMetaAnalysis expected = new NetworkMetaAnalysis(name,
				ExampleData.buildIndicationDepression(),
				ExampleData.buildEndpointCgi(), studies, alternatives, armMap);
		
		assertEntityEquals(expected,
				NetworkMetaAnalysisConverter.load(ma.d_nwma, domain));
		assertEquals(ma.d_nwma,
				NetworkMetaAnalysisConverter.save(expected));
	}

	@Test
	public void testRoundTrip() throws JAXBException, ConversionException, TransformerException, IOException, SAXException {
		d_jaxbConverterTest.doRoundTripTest(getTransformedSavedResultsData(), false);
	}
	
	private static InputStream getTransformedSavedResultsData() throws TransformerException, IOException {
		return JAXBConvertorTest.getTestData(TEST_DATA_WITH_RESULTS);
	}
		
	
}
