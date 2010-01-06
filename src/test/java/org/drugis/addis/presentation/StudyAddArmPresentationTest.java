package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class StudyAddArmPresentationTest {
	private static final AbstractDose INITIAL_DOSE = new FlexibleDose(new Interval<Double>(0.0, 0.0), SIUnit.MILLIGRAMS_A_DAY);
	private StudyAddArmPresentation d_pm;
	private PresentationModelFactory d_pmf;
	private Domain d_domain;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pmf = new PresentationModelFactory(d_domain);
		d_pm = new StudyAddArmPresentation(ExampleData.buildStudyChouinard(), d_pmf);
	}
	
	@Test
	public void testGetArm() {
		Arm pg = d_pm.getArm();
		assertNotNull(pg);
		assertEquals(new Integer(0), pg.getSize());
		assertEquals(null, pg.getDrug());
		assertEquals(INITIAL_DOSE, pg.getDose());
	}
	
	@Test
	public void testGetDoseModel() {
		assertEquals(INITIAL_DOSE, d_pm.getArm().getDose());
		double newValue = 25.4;
		d_pm.getDoseModel().getMaxModel().setValue(newValue);
		assertEquals(d_pm.getDoseModel().getMaxModel().doubleValue(), newValue, 0.001);
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
		assertEquals(Collections.singletonList(ExampleData.buildEndpointHamd()), d_pm.getOutcomeMeasures(Endpoint.Type.RATE));
	}
	
	@Test
	public void testGetContinuousEndpoints() {
		assertEquals(Collections.singletonList(ExampleData.buildEndpointCgi()), d_pm.getOutcomeMeasures(Endpoint.Type.CONTINUOUS));
	}
	
	@Test
	public void testHasRateEndpoints() {
		assertTrue(d_pm.hasEndpoints(Endpoint.Type.RATE));
		StudyAddArmPresentation p = new StudyAddArmPresentation(
				ExampleData.buildStudyChouinardNoHamd(), d_pmf);
		assertFalse(p.hasEndpoints(Endpoint.Type.RATE));
	}
	
	@Test
	public void testHasContinuousEndpoints() {
		assertTrue(d_pm.hasEndpoints(Endpoint.Type.CONTINUOUS));
		StudyAddArmPresentation p = new StudyAddArmPresentation(
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
	public void testSetSampleSizeNotOverrideGroupSize() {
		BasicRateMeasurement m =
			(BasicRateMeasurement) d_pm.getMeasurementModel(ExampleData.buildEndpointHamd()).getBean();
		m.setSampleSize(50);
		assertEquals(new Integer(0), d_pm.getSizeModel().getValue());
		d_pm.getSizeModel().setValue(100);
		assertEquals(new Integer(100), m.getSampleSize());
	}
	
	@Test
	public void testAddToStudy() {
		Study study = new Study("Some Study", new Indication(0L, ""));
		study.addOutcomeMeasure(ExampleData.buildEndpointHamd());
		StudyAddArmPresentation pres = new StudyAddArmPresentation(study, d_pmf);
		pres.getDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		pres.addToStudy();
		assertEquals(1, study.getArms().size());
		assertEquals(pres.getArm(), study.getArms().get(0));
		assertNotNull(study.getMeasurement(ExampleData.buildEndpointHamd(), pres.getArm()));
	}
}
