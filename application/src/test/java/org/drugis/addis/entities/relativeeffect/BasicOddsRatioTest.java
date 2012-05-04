/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class BasicOddsRatioTest extends RelativeEffectTestBase {
	private BasicOddsRatio d_ratioBennie, d_ratioBoyer, d_ratioFava, d_ratioNewhouse, d_ratioSechter;

	@Before
	public void setUp() {
		d_bennie = createRateStudy("Bennie 1995",63,144,73,142);
		d_boyer = createRateStudy("Boyer 1998", 61,120, 63,122);
		d_fava = createRateStudy("Fava 2002", 57, 92, 70, 96);
		d_newhouse = createRateStudy("Newhouse 2000", 84,119, 85,117);
		d_sechter = createRateStudy("Sechter 1999", 76,120, 86,118);
				
		
		d_ratioBennie = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_bennie, d_rateEndpoint, new DrugSet(d_fluox), new DrugSet(d_sertr), BasicOddsRatio.class);
		d_ratioBoyer = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_boyer, d_rateEndpoint, new DrugSet(d_fluox), new DrugSet(d_sertr), BasicOddsRatio.class);
		d_ratioFava = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_fava, d_rateEndpoint, new DrugSet(d_fluox), new DrugSet(d_sertr), BasicOddsRatio.class);
		d_ratioNewhouse = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_newhouse, d_rateEndpoint, new DrugSet(d_fluox), new DrugSet(d_sertr), BasicOddsRatio.class);
		d_ratioSechter = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_sechter, d_rateEndpoint, new DrugSet(d_fluox), new DrugSet(d_sertr), BasicOddsRatio.class);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(1.36, (d_ratioBennie.getConfidenceInterval().getPointEstimate()), 0.01);
		assertEquals(1.03, (d_ratioBoyer.getConfidenceInterval().getPointEstimate()), 0.01); 
		assertEquals(1.65, (d_ratioFava.getConfidenceInterval().getPointEstimate()), 0.01);
		assertEquals(1.11, (d_ratioNewhouse.getConfidenceInterval().getPointEstimate()), 0.01);
		assertEquals(1.56, (d_ratioSechter.getConfidenceInterval().getPointEstimate()), 0.01); 
	}
	
	@Test
	public void testGetError() {
		double expected = Math.sqrt(1/63D + 1/73D + 1/(144D-63D) + 1/(142D-73D));
		assertEquals(expected, d_ratioBennie.getError(), 0.001);
	}
	
	@Test
	public void testGetConfidenceIntervalBennie() {
		Interval<Double> ival = d_ratioBennie.getConfidenceInterval();
		assertEquals(0.85, (ival.getLowerBound()), 0.01);
		assertEquals(2.17, (ival.getUpperBound()), 0.01);
	}
	
	@Test
	public void testGetConfidenceIntervalBoyer() {
		Interval<Double> ival = d_ratioBoyer.getConfidenceInterval();
		assertEquals(0.62, (ival.getLowerBound()), 0.01); 
		assertEquals(1.71, (ival.getUpperBound()), 0.01); 
	}
	
	@Test
	public void testGetConfidenceIntervalFava() {
		Interval<Double> ival = d_ratioFava.getConfidenceInterval();
		assertEquals(0.89, (ival.getLowerBound()), 0.01); 
		assertEquals(3.06, (ival.getUpperBound()), 0.015); 
	}
	
	@Test
	public void testGetConfidenceIntervalNewhouse() {
		Interval<Double> ival = d_ratioNewhouse.getConfidenceInterval();
		assertEquals(0.63, (ival.getLowerBound()), 0.01); 
		assertEquals(1.95, (ival.getUpperBound()), 0.01); 
	}
	
	@Test
	public void testGetConfidenceIntervalSechter() {
		Interval<Double> ival = d_ratioSechter.getConfidenceInterval();
		assertEquals(0.90, (ival.getLowerBound()), 0.01); 
		assertEquals(2.70, (ival.getUpperBound()), 0.01); 
	}
	
	@Test public void testGetDistribution() {
		Distribution distribution = d_ratioSechter.getDistribution();
		assertEquals(0.90, distribution.getQuantile(0.025), 0.01);
		assertEquals(2.70, distribution.getQuantile(0.975), 0.01);
	}
	
	@Test
	public void testZeroBaselineRateShouldBeUndefined() {
		RateMeasurement base = new BasicRateMeasurement(0, 100);
		RateMeasurement subj = new BasicRateMeasurement(50, 100);
		BasicOddsRatio or = new BasicOddsRatio(base, subj);
		assertFalse(or.isDefined());
	}

	@Test
	public void testFullSubjectRateShouldBeUndefined() {
		RateMeasurement base = new BasicRateMeasurement(50, 100);
		RateMeasurement subj = new BasicRateMeasurement(100, 100);
		BasicOddsRatio or = new BasicOddsRatio(base, subj);
		assertFalse(or.isDefined());
	}

	@Test
	public void testZeroSubjectRateShouldBeUndefined() { // although we can calculate a point-estimate, we can't get a CI.
		RateMeasurement base = new BasicRateMeasurement(50, 100);
		RateMeasurement subj = new BasicRateMeasurement(0, 100);
		BasicOddsRatio or = new BasicOddsRatio(base, subj);
		assertFalse(or.isDefined());
	}

	@Test
	public void testFullBaselineRateShouldBeUndefined() { // although we can calculate a point-estimate, we can't get a CI.
		RateMeasurement base = new BasicRateMeasurement(100, 100);
		RateMeasurement subj = new BasicRateMeasurement(50, 100);
		BasicOddsRatio or = new BasicOddsRatio(base, subj);
		assertFalse(or.isDefined());
	}

	@Test
	public void testUndefinedShouldResultInNaN() {
		RateMeasurement rmA1 = new BasicRateMeasurement(0, 100);
		RateMeasurement rmC1 = new BasicRateMeasurement(50, 100);
		BasicOddsRatio or = new BasicOddsRatio(rmA1, rmC1);
		assertEquals(Double.NaN, or.getError(), 0.001);
		assertEquals(Double.NaN, or.getConfidenceInterval().getPointEstimate(), 0.001);
	}

}
