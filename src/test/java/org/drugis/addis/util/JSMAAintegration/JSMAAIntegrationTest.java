package org.drugis.addis.util.JSMAAintegration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.junit.Before;
import org.junit.Test;

import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;

public class JSMAAIntegrationTest {
	private SMAAEntityFactory d_SMAAFactory;
	private BenefitRiskAnalysis d_BRAnalysis;

	@Before
	public void setup() {
		d_BRAnalysis = ExampleData.buildBenefitRiskAnalysis();
		d_SMAAFactory = new SMAAEntityFactory();
	}
	
	@Test
	public void testCreateCardinalMeasurementRate() {
		
		RelativeEffect<? extends Measurement> relativeEffect = d_BRAnalysis.getRelativeEffect(ExampleData.buildDrugParoxetine(), ExampleData.buildEndpointHamd());
		CardinalMeasurement actual = SMAAEntityFactory.createCardinalMeasurement(relativeEffect.getDistribution());
		assertTrue(!((LogNormalMeasurement) actual).getMean().isNaN());
		assertTrue(actual instanceof LogNormalMeasurement);
		assertEquals(Math.log(relativeEffect.getConfidenceInterval().getPointEstimate()),((LogNormalMeasurement) actual).getMean(), 0.0001);
		assertEquals(((LogGaussian)relativeEffect.getDistribution()).getSigma(),((LogNormalMeasurement) actual).getStDev(), 0.0001);
	}
	
	
	@Test
	public void testCreateSmaaModel() {
		SMAAModel smaaModel = d_SMAAFactory.createSmaaModel(d_BRAnalysis);
		for(OutcomeMeasure om : d_BRAnalysis.getOutcomeMeasures()){
			for(Drug d : d_BRAnalysis.getDrugs()){
				fi.smaa.jsmaa.model.Measurement actualMeasurement = smaaModel.getMeasurement(d_SMAAFactory.getCriterion(om), d_SMAAFactory.getAlternative(d));
				RelativeEffect<? extends Measurement> expRelativeEffect = d_BRAnalysis.getRelativeEffect(d, om);
				assertEquals(Math.log(expRelativeEffect.getConfidenceInterval().getPointEstimate()), ((LogNormalMeasurement) actualMeasurement).getMean(), 0.0001);
			}
		}
	}
}
