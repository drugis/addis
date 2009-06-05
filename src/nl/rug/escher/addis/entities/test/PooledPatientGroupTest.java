package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.MetaStudy;
import nl.rug.escher.addis.entities.PooledPatientGroup;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.addis.entities.UnknownDose;

import org.junit.Before;
import org.junit.Test;

public class PooledPatientGroupTest {
	
	private PooledPatientGroup d_pg;
	private MetaAnalysis d_analysis;
	private MetaStudy d_study;
	
	@Before
	public void setUp() {
		Domain d_domain = new DomainImpl();
		TestData.initDefaultData(d_domain);
		d_analysis = new MetaAnalysis(TestData.buildEndpointHamd(), 
				new ArrayList<Study>(d_domain.getStudies()));		
		d_study = new MetaStudy("s", d_analysis);
		d_pg = new PooledPatientGroup(d_study, TestData.buildDrugFluoxetine());
	}
	
	@Test
	public void testGetDose() {
		assertEquals(new UnknownDose(), d_pg.getDose());
	}
	
	@Test
	public void testGetDrug() {
		assertEquals(TestData.buildDrugFluoxetine(), d_pg.getDrug());
	}
	
	@Test
	public void testGetSize() {
		assertEquals(d_analysis.getPooledMeasurement(d_pg.getDrug()).getSampleSize(),
				d_pg.getSize());
	}

	@Test
	public void testGetStudy() {
		assertEquals(d_study, d_pg.getStudy());
	}
	
	@Test
	public void testGetMeasurements() {
		List<Measurement> l = Collections.singletonList(d_analysis.getPooledMeasurement(d_pg.getDrug()));
		assertEquals(l, d_pg.getMeasurements());
	}	

	@Test
	public void testGetMeasurementEndpoint() {
		assertEquals(d_analysis.getPooledMeasurement(d_pg.getDrug()), d_pg.getMeasurement(d_analysis.getEndpoint()));
	}
	
	@Test
	public void testGetMeasurementFailsOnIncorrectEndpoint() {
		assertNull(d_pg.getMeasurement(new Endpoint("aaagh")));
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("META " + d_pg.getDrug().toString(), d_pg.getLabel());
	}
}
