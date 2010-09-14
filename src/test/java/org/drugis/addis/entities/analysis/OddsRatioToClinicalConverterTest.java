/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		MetaBenefitRiskAnalysis br = ExampleData.buildMockBenefitRiskAnalysis();
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
