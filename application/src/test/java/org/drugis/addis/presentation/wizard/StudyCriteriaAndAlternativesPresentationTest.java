package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;

public class StudyCriteriaAndAlternativesPresentationTest {

	private Study d_study1;
	private Indication d_indication;
	private StudyCriteriaAndAlternativesPresentation d_pm;
	private Study d_study2;

	@Before
	public void setUp() {
		d_indication = ExampleData.buildIndicationDepression();
		d_study1 = ExampleData.buildStudyChouinard().clone();
		d_study2 = ExampleData.buildStudyFava2002().clone();
		
		d_pm = new StudyCriteriaAndAlternativesPresentation(new ModifiableHolder<Indication>(d_indication), 
				new ModifiableHolder<AnalysisType>(AnalysisType.SMAA), new ArrayListModel<Study>(Arrays.asList(d_study1, d_study2)));
	}
	
	
	@Test
	public void testAlternativeSelectedModelKeepsChanges() {
		d_pm.getStudyModel().setValue(d_study1);
		
		Arm a = d_study1.getArms().get(0);
		ValueHolder<Boolean> origArm = d_pm.getAlternativeSelectedModel(a);
		assertFalse(origArm.getValue());
		d_pm.getAlternativeSelectedModel(a).setValue(true);
		assertEquals(d_pm.getAlternativeSelectedModel(a).getValue(), origArm.getValue());
	}
	
	@Test
	public void testNullMeasuredCriteriaNotEnabled() {
		d_study1.setMeasurement(ExampleData.buildEndpointHamd(), d_study1.getArms().get(0), null);
		d_study1.setMeasurement(ExampleData.buildEndpointCgi(), d_study1.getArms().get(1), null);

		d_pm.getStudyModel().setValue(d_study1);
		
		assertFalse(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointHamd()).getValue());
		assertFalse(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointCgi()).getValue());
		assertTrue(d_pm.getCriterionEnabledModel(ExampleData.buildAdverseEventConvulsion()).getValue());
	}
	
	@Test
	public void testIncompleteMeasurementsNotEnabled() {
		d_study1.getMeasurement(ExampleData.buildAdverseEventConvulsion(), d_study1.getArms().get(0)).setSampleSize(null);
		d_study1.getMeasurement(ExampleData.buildEndpointHamd(), d_study1.getArms().get(0)).setSampleSize(0);
		
		d_pm.getStudyModel().setValue(d_study1);

		assertFalse(d_pm.getCriterionEnabledModel(ExampleData.buildAdverseEventConvulsion()).getValue());
		assertFalse(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointHamd()).getValue());
		assertTrue(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointCgi()).getValue());
	}

	@Test
	public void testUndefinedRatiosNotEnabled() {
		BasicRateMeasurement meas0 = (BasicRateMeasurement) d_study1.getMeasurement(ExampleData.buildEndpointHamd(), d_study1.getArms().get(0));
		BasicRateMeasurement meas1 = (BasicRateMeasurement) d_study1.getMeasurement(ExampleData.buildEndpointHamd(), d_study1.getArms().get(1));
		meas0.setRate(0);
		meas1.setRate(0);

		d_pm.getStudyModel().setValue(d_study1);
		
		assertFalse(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointHamd()).getValue());
	}

	@Test
	public void testDefinedRatiosEnabled() {
		BasicRateMeasurement meas1 = (BasicRateMeasurement) d_study1.getMeasurement(ExampleData.buildEndpointHamd(), d_study1.getArms().get(1));
		meas1.setRate(0);

		d_pm.getStudyModel().setValue(d_study1);
		
		assertTrue(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointHamd()).getValue());
	}

	@Test
	public void testHundredPercentRatiosNotEnabled() {
		BasicRateMeasurement meas0 = (BasicRateMeasurement) d_study1.getMeasurement(ExampleData.buildEndpointHamd(), d_study1.getArms().get(0));
		BasicRateMeasurement meas1 = (BasicRateMeasurement) d_study1.getMeasurement(ExampleData.buildEndpointHamd(), d_study1.getArms().get(1));
		meas0.setRate(meas0.getSampleSize());
		meas1.setRate(meas1.getSampleSize());

		d_pm.getStudyModel().setValue(d_study1);
		
		assertFalse(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointHamd()).getValue());
	}

	@Test
	public void testNonHundredPercentRatiosEnabled() {
		BasicRateMeasurement meas0 = (BasicRateMeasurement) d_study1.getMeasurement(ExampleData.buildEndpointHamd(), d_study1.getArms().get(0));
		meas0.setRate(meas0.getSampleSize());

		d_pm.getStudyModel().setValue(d_study1);
		
		assertTrue(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointHamd()).getValue());
	}
	
	@Test
	public void testAtLeastTwoNonMissingEnabled() {
		d_study2.setMeasurement(ExampleData.buildAdverseEventConvulsion(), d_study2.getArms().get(0), new BasicRateMeasurement(5, 22));
		d_study2.getMeasurement(ExampleData.buildEndpointHamd(), d_study2.getArms().get(0)).setSampleSize(null);

		d_pm.getStudyModel().setValue(d_study2);

		assertFalse(d_pm.getCriterionEnabledModel(ExampleData.buildAdverseEventConvulsion()).getValue());
		assertTrue(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointHamd()).getValue());
	}
	
	@Test
	public void testAtLeastTwoNonMissingEnabledBLAAA() {
		d_study2.setMeasurement(ExampleData.buildAdverseEventConvulsion(), d_study2.getArms().get(0), new BasicRateMeasurement(5, 22));
		((BasicRateMeasurement)d_study2.getMeasurement(ExampleData.buildEndpointHamd(), d_study2.getArms().get(0))).setRate(null);

		d_pm.getStudyModel().setValue(d_study2);

		assertFalse(d_pm.getCriterionEnabledModel(ExampleData.buildAdverseEventConvulsion()).getValue());
		assertTrue(d_pm.getCriterionEnabledModel(ExampleData.buildEndpointHamd()).getValue());
	}
}
