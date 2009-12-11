package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class StudyAddPatientGroupPresentationTest {
	private static final Dose INITIAL_DOSE = new Dose(new Interval<Double>(0.0, 0.0), SIUnit.MILLIGRAMS_A_DAY);
	private StudyAddPatientGroupPresentation d_pm;
	private PresentationModelFactory d_pmf;
	private Domain d_domain;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pmf = new PresentationModelFactory(d_domain);
		d_pm = new StudyAddPatientGroupPresentation(ExampleData.buildStudyChouinard(), d_pmf);
	}
	
	@Test
	public void testGetPatientGroup() {
		BasicPatientGroup pg = d_pm.getPatientGroup();
		assertNotNull(pg);
		assertEquals(new Integer(0), pg.getSize());
		assertEquals(null, pg.getDrug());
		assertEquals(INITIAL_DOSE, pg.getDose());
	}
	
	@Test
	public void testGetDoseModel() {
		assertEquals(INITIAL_DOSE, d_pm.getDoseModel().getBean());
		double newValue = 25.4;
		d_pm.getDoseModel().setValue(Dose.PROPERTY_MAX_DOSE, newValue);
		assertEquals(d_pm.getDoseModel().getValue(Dose.PROPERTY_MAX_DOSE), new Double(newValue));
	}
	
	@Test
	public void testGetDrugModel() {
		assertNull(d_pm.getDrugModel().getValue());
		d_pm.getDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		assertEquals(ExampleData.buildDrugFluoxetine(), d_pm.getDrugModel().getValue());
	}
	
	@Test
	public void testGetSizeModel() {
		assertEquals(new Integer(0), d_pm.getSizeModel().getValue());
		Integer newValue = 12;
		d_pm.getSizeModel().setValue(newValue);
		assertEquals(newValue, d_pm.getSizeModel().getValue());
	}
	
	@Test
	public void testGetRateEndpoints() {
		assertEquals(Collections.singletonList(ExampleData.buildEndpointHamd()), d_pm.getEndpoints(Endpoint.Type.RATE));
	}
	
	@Test
	public void testGetContinuousEndpoints() {
		assertEquals(Collections.singletonList(ExampleData.buildEndpointCgi()), d_pm.getEndpoints(Endpoint.Type.CONTINUOUS));
	}
	
	@Test
	public void testHasRateEndpoints() {
		assertTrue(d_pm.hasEndpoints(Endpoint.Type.RATE));
		StudyAddPatientGroupPresentation p = new StudyAddPatientGroupPresentation(
				ExampleData.buildStudyChouinardNoHamd(), d_pmf);
		assertFalse(p.hasEndpoints(Endpoint.Type.RATE));
	}
	
	@Test
	public void testHasContinuousEndpoints() {
		assertTrue(d_pm.hasEndpoints(Endpoint.Type.CONTINUOUS));
		StudyAddPatientGroupPresentation p = new StudyAddPatientGroupPresentation(
				ExampleData.buildStudyMcMurray(), d_pmf);
		assertFalse(p.hasEndpoints(Endpoint.Type.CONTINUOUS));
	}
	
	@Test
	public void testGetMeasurementModel() {
		assertTrue(d_pm.getMeasurementModel(ExampleData.buildEndpointCgi()).getBean()
				instanceof BasicContinuousMeasurement);
		assertTrue(d_pm.getMeasurementModel(ExampleData.buildEndpointHamd()).getBean()
				instanceof BasicRateMeasurement);
	}
	
	@Test
	public void testSetGroupSizeSetsRateMeasurementSampleSize() {
		BasicRateMeasurement m =
			(BasicRateMeasurement) d_pm.getMeasurementModel(ExampleData.buildEndpointHamd()).getBean();
		assertEquals(new Integer(0), m.getSampleSize());
		d_pm.getSizeModel().setValue(100);
		assertEquals(new Integer(100), m.getSampleSize());
	}
	
	@Test
	public void testSetGroupSizeNotOverrideSampleSize() {
		BasicRateMeasurement m =
			(BasicRateMeasurement) d_pm.getMeasurementModel(ExampleData.buildEndpointHamd()).getBean();
		m.setSampleSize(50);
		assertEquals(new Integer(50), m.getSampleSize());
		d_pm.getSizeModel().setValue(100);
		assertEquals(new Integer(50), m.getSampleSize());
	}
	
	@Test
	public void testAddToStudy() {
		BasicStudy study = new BasicStudy("Some Study", new Indication(0L, ""));
		study.addEndpoint(ExampleData.buildEndpointHamd());
		StudyAddPatientGroupPresentation pres = new StudyAddPatientGroupPresentation(study, d_pmf);
		pres.getDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		pres.addToStudy();
		assertEquals(1, study.getPatientGroups().size());
		assertEquals(pres.getPatientGroup(), study.getPatientGroups().get(0));
		assertNotNull(study.getMeasurement(ExampleData.buildEndpointHamd(), pres.getPatientGroup()));
	}
}
