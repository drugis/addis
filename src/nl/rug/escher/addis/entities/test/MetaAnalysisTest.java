package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

public class MetaAnalysisTest {
	private Domain d_domain;
	private MetaAnalysis d_analysis;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		TestData.initDefaultData(d_domain);
		d_analysis = new MetaAnalysis(TestData.buildEndpointHamd(), 
				new ArrayList<Study>(d_domain.getStudies()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValidateStudiesMeasureEndpoint() {
		Endpoint e = new Endpoint("e1");
		Endpoint other = new Endpoint("e2");
		BasicStudy s = new BasicStudy("X");
		s.addEndpoint(other);
		new MetaAnalysis(e, Collections.singletonList((Study)s));
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(TestData.buildEndpointHamd(), d_analysis.getEndpoint());
	}
	
	@Test
	public void testGetStudies() {
		assertEquals(d_domain.getStudies(), new HashSet<Study>(d_analysis.getStudies()));
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
		Study s = d_domain.getStudies().first();
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

	@Test
	public void testEquals() {
		Endpoint e1 = new Endpoint("E1");
		Endpoint e2 = new Endpoint("E2");
		BasicStudy s1 = new BasicStudy("Test");
		BasicStudy s2 = new BasicStudy("Study");
		BasicStudy s3 = new BasicStudy("X");
		s1.addEndpoint(e1);
		s2.addEndpoint(e1);
		s3.addEndpoint(e1);
		s1.addEndpoint(e2);
		s2.addEndpoint(e2);
		s3.addEndpoint(e2);
		
		List<Study> l1 = new ArrayList<Study>();
		l1.add(s1); l1.add(s2);
		assertEquals(new MetaAnalysis(e1, l1), new MetaAnalysis(e1, l1));
		assertEquals(
				new MetaAnalysis(e1, l1).hashCode(),
				new MetaAnalysis(e1, l1).hashCode());
		
		List<Study> l2 = new ArrayList<Study>();
		l2.add(s1); l2.add(s3);
		JUnitUtil.assertNotEquals(new MetaAnalysis(e1, l1), new MetaAnalysis(e1, l2));
		
		List<Study> l3 = new ArrayList<Study>();
		l3.add(s2); l3.add(s1);
		assertEquals(new MetaAnalysis(e1, l1), new MetaAnalysis(e1, l3));
		assertEquals(
				new MetaAnalysis(e1, l1).hashCode(),
				new MetaAnalysis(e1, l3).hashCode());
		
		JUnitUtil.assertNotEquals(new MetaAnalysis(e2, l1), new MetaAnalysis(e1, l1));
	}
}