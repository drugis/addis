package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.ExampleData;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class OddsRatioToClinicalConverterTest {

	private static final double EPSILON = 0.0000000001;
	private OddsRatioToClinicalConverter d_orc;
	private double d_baselineOdds;

	@Before
	public void setUp(){
		BenefitRiskAnalysis br = ExampleData.buildMockBenefitRiskAnalysis();
		d_orc = new OddsRatioToClinicalConverter(br, ExampleData.buildEndpointHamd());
		d_baselineOdds = br.getBaselineDistribution(ExampleData.buildEndpointHamd()).getQuantile(0.5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorShouldFailOnContinuous() {
		new OddsRatioToClinicalConverter(ExampleData.realBuildContinuousMockBenefitRisk(), ExampleData.buildEndpointCgi());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorShouldFailOnMissingOutcome() {
		new OddsRatioToClinicalConverter(ExampleData.buildMockBenefitRiskAnalysis(), ExampleData.buildEndpointCVdeath());
	}
	
	@Test
	public void testGetOddsRatio() {
		OddsRatioToClinicalConverter orc = new OddsRatioToClinicalConverter(ExampleData.buildMockBenefitRiskAnalysis(), ExampleData.buildEndpointHamd());
		Interval<Double> expected = new Interval<Double>(0.95,1.25);
		assertEquals(expected, orc.getOddsRatio(expected));
	}
	
	@Test
	public void testGetRisk() {
		Interval<Double> oddsRatio = new Interval<Double>(0.95, 1.25);
		Interval<Double> odds = new Interval<Double>(oddsRatio.getLowerBound() * d_baselineOdds, oddsRatio.getUpperBound() * d_baselineOdds);
		Interval<Double> expected = new Interval<Double>(odds.getLowerBound() / (1 + odds.getLowerBound()), odds.getUpperBound() / (1 + odds.getUpperBound()));
		assertEquals(expected.getLowerBound(), d_orc.getRisk(oddsRatio).getLowerBound(), EPSILON);
		assertEquals(expected.getUpperBound(), d_orc.getRisk(oddsRatio).getUpperBound(), EPSILON);
	}
	
	@Test
	public void testGetRiskDifference() {
		Interval<Double> oddsRatio = new Interval<Double>(0.95, 1.25);
		Interval<Double> odds = new Interval<Double>(oddsRatio.getLowerBound() * d_baselineOdds, oddsRatio.getUpperBound() * d_baselineOdds);
		double expected = odds.getUpperBound() / (1 + odds.getUpperBound()) - odds.getLowerBound() / (1 + odds.getLowerBound());
		assertEquals(expected, d_orc.getRiskDifference(oddsRatio), EPSILON);
	}
	
	@Test
	public void testGetNumberNeededToTreat() {
		Interval<Double> oddsRatio = new Interval<Double>(0.95, 1.25);
		Interval<Double> odds = new Interval<Double>(oddsRatio.getLowerBound() * d_baselineOdds, oddsRatio.getUpperBound() * d_baselineOdds);
		double riskDifference = odds.getUpperBound() / (1 + odds.getUpperBound()) - odds.getLowerBound() / (1 + odds.getLowerBound());
		double expected = 1d / riskDifference;
		assertEquals(expected, d_orc.getNumberNeededToTreat(oddsRatio), EPSILON);
	}
	
}
