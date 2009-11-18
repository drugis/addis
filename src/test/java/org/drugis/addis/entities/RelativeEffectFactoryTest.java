package org.drugis.addis.entities;

import static org.junit.Assert.*;

import org.drugis.addis.ExampleData;
import org.junit.Test;

public class RelativeEffectFactoryTest {
	@Test
	public void testGetStandardizedMeanDifference() {
		Study s = ExampleData.buildDefaultStudy1();
		Endpoint e = ExampleData.buildEndpointCgi();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		PatientGroup pBase = s.getPatientGroups().get(0);
		PatientGroup pSubj = s.getPatientGroups().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new StandardisedMeanDifference(
				(ContinuousMeasurement)s.getMeasurement(e, pBase),
				(ContinuousMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						StandardisedMeanDifference.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetStandardizedMeanDifferenceRate() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildDefaultStudy1(),
				ExampleData.buildEndpointHamd(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				StandardisedMeanDifference.class);
	}
	
	@Test
	public void testGetMeanDifference() {
		Study s = ExampleData.buildDefaultStudy1();
		Endpoint e = ExampleData.buildEndpointCgi();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		PatientGroup pBase = s.getPatientGroups().get(0);
		PatientGroup pSubj = s.getPatientGroups().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new MeanDifference(
				(ContinuousMeasurement)s.getMeasurement(e, pBase),
				(ContinuousMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						MeanDifference.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetMeanDifferenceRate() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildDefaultStudy1(),
				ExampleData.buildEndpointHamd(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				MeanDifference.class);
	}
	
	@Test
	public void testGetOddsRatio() {
		Study s = ExampleData.buildDefaultStudy1();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		PatientGroup pBase = s.getPatientGroups().get(0);
		PatientGroup pSubj = s.getPatientGroups().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new OddsRatio(
				(RateMeasurement)s.getMeasurement(e, pBase),
				(RateMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						OddsRatio.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetOddsRatioCont() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildDefaultStudy1(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				OddsRatio.class);
	}
	
	@Test
	public void testGetRiskRatio() {
		Study s = ExampleData.buildDefaultStudy1();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		PatientGroup pBase = s.getPatientGroups().get(0);
		PatientGroup pSubj = s.getPatientGroups().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new RiskRatio(
				(RateMeasurement)s.getMeasurement(e, pBase),
				(RateMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						RiskRatio.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetRiskRatioCont() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildDefaultStudy1(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				RiskRatio.class);
	}
	
	@Test
	public void testGetRiskDifference() {
		Study s = ExampleData.buildDefaultStudy1();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		PatientGroup pBase = s.getPatientGroups().get(0);
		PatientGroup pSubj = s.getPatientGroups().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new RiskDifference(
				(RateMeasurement)s.getMeasurement(e, pBase),
				(RateMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						RiskDifference.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetRiskDifferenceCont() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildDefaultStudy1(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				RiskDifference.class);
	}
	
	@Test
	public void testGetLogRiskRatio() {
		Study s = ExampleData.buildDefaultStudy1();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		PatientGroup pBase = s.getPatientGroups().get(0);
		PatientGroup pSubj = s.getPatientGroups().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new LogRiskRatio(
				(RateMeasurement)s.getMeasurement(e, pBase),
				(RateMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						LogRiskRatio.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetLogRiskRatioCont() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildDefaultStudy1(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				RiskDifference.class);
	}
	
	private static void assertRelativeEffectEqual(RelativeEffect<?> expected,
			RelativeEffect<?> actual) {
		assertEquals(expected.getBaseline(), actual.getBaseline());
		assertEquals(expected.getSubject(), actual.getSubject());
		assertEquals(expected.getClass(), actual.getClass());
	}
}
