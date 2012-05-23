package org.drugis.addis.util.convertors;

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
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.util.JAXBConvertor;
import org.drugis.addis.util.JAXBConvertor.ConversionException;
import org.drugis.addis.util.JAXBConvertorTest;
import org.drugis.addis.util.JAXBConvertorTest.MetaAnalysisWithStudies;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class NetworkMetaAnalysisConverterTest {

	private JAXBConvertorTest d_jaxbConverterTest;
	private static final String TEST_DATA_WITH_RESULTS = "../testDataSavedResults.addis"; // note: saved results in Dizziness MA

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

		DrugSet combi = new DrugSet(Arrays.asList(
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugSertraline()));
		DrugSet parox = new DrugSet(ExampleData.buildDrugParoxetine());
		DrugSet sertr = new DrugSet(ExampleData.buildDrugSertraline());
		SortedSet<DrugSet> drugs = new TreeSet<DrugSet>();
		drugs.add(combi);
		drugs.add(parox);
		drugs.add(sertr);
		Map<Study, Map<DrugSet, Arm>> armMap = new HashMap<Study, Map<DrugSet, Arm>>();
		Map<DrugSet, Arm> study1map = new HashMap<DrugSet, Arm>();
		study1map.put(combi, studies.get(0).getArms().get(0));
		study1map.put(sertr, studies.get(0).getArms().get(1));
		armMap.put(studies.get(0), study1map);
		Map<DrugSet, Arm> study2map = new HashMap<DrugSet, Arm>();
		study2map.put(parox, studies.get(1).getArms().get(0));
		study2map.put(sertr, studies.get(1).getArms().get(1));
		armMap.put(studies.get(1), study2map);
		Map<DrugSet, Arm> study3map = new HashMap<DrugSet, Arm>();
		study3map.put(sertr, studies.get(2).getArms().get(0));
		study3map.put(parox, studies.get(2).getArms().get(1));
		study3map.put(combi, studies.get(2).getArms().get(2));
		armMap.put(studies.get(2), study3map);

		Collections.sort(studies); // So the reading *by definition* puts the studies in their natural order
		NetworkMetaAnalysis expected = new NetworkMetaAnalysis(name,
				ExampleData.buildIndicationDepression(),
				ExampleData.buildEndpointCgi(), studies, drugs, armMap);
		
		assertEntityEquals(expected,
				NetworkMetaAnalysisConverter.convertNetworkMetaAnalysis(ma.d_nwma, domain));
		assertEquals(ma.d_nwma,
				NetworkMetaAnalysisConverter.convertNetworkMetaAnalysis(expected));
	}

	@Test
	public void testRoundTrip() throws JAXBException, ConversionException, TransformerException, IOException, SAXException {
		d_jaxbConverterTest.doRoundTripTest(getTransformedSavedResultsData());
	}
	
	private static InputStream getTransformedSavedResultsData()
			throws TransformerException, IOException {
		return JAXBConvertorTest.getTestData(TEST_DATA_WITH_RESULTS);
	}
		
	
}
