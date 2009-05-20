package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.Study;

import org.junit.Before;
import org.junit.Test;

public class MetaAnalysisTest {
	private Domain d_domain;
	private MetaAnalysis d_analysis;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		TestData.initDefaultData(d_domain);
		d_analysis = new MetaAnalysis(TestData.buildEndpointHamd(), d_domain.getStudies());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValidateStudiesMeasureEndpoint() {
		Endpoint e = new Endpoint("e");
		Endpoint other = new Endpoint("other");
		Study s = new Study(other);
		new MetaAnalysis(e, Collections.singletonList(s));
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(TestData.buildEndpointHamd(), d_analysis.getEndpoint());
	}
	
	@Test
	public void testGetStudies() {
		assertEquals(d_domain.getStudies(), d_analysis.getStudies());
	}
	
	@Test
	public void testGetDrugs() {
		List<Drug> expect = Arrays.asList(new Drug[] {
				TestData.buildDrugFluoxetine(), TestData.buildDrugParoxetine()});
		assertEquals(expect.size(), d_analysis.getDrugs().size());
		assertTrue(d_analysis.getDrugs().containsAll(expect));
	}
	
	@Test
	public void testGetMeasurement() {
		Study s = d_domain.getStudies().get(0);
		PatientGroup g = s.getPatientGroups().get(1);
		assertEquals(g.getMeasurement(d_analysis.getEndpoint()),
				d_analysis.getMeasurement(s, g.getDrug()));
	}
	
	@Test
	public void testGetPooledMeasurement() {
		Drug d = TestData.buildDrugFluoxetine();
		int rate = 26 + 67;
		int size = 41 + 101;
		
		RateMeasurement m = (RateMeasurement)d_analysis.getPooledMeasurement(d);
		assertEquals(new Integer(rate), m.getRate());
		assertEquals(new Integer(size), m.getSampleSize());
	}
}
