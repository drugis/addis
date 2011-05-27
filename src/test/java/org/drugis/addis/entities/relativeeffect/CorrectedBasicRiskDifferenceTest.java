/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import static org.junit.Assert.assertTrue;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.junit.Before;
import org.junit.Test;

public class CorrectedBasicRiskDifferenceTest {

	private Drug d_fluox;
	private Drug d_sertra;
	
	private Indication d_ind;
	private Endpoint d_ep;
	
	private Study d_bennie;
	
	private BasicRiskDifference d_riskDifferenceBennie;
	
	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertra = new Drug("Sertraline","02");
		d_ep = new Endpoint("ep", Variable.Type.RATE);
		
		d_bennie = createStudy("Bennie 1995",0,144,73,142);
		d_riskDifferenceBennie = (BasicRiskDifference) RelativeEffectFactory.buildRelativeEffect(d_bennie, d_ep, d_fluox, d_sertra, BasicRiskDifference.class, true);
	}

	@Test
	public void testZeroBaselineRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(0, 100);
		RateMeasurement subj = new BasicRateMeasurement(50, 100);
		CorrectedBasicRiskDifference or = new CorrectedBasicRiskDifference(base, subj);
		assertTrue(or.isDefined());
	}
	
	@Test
	public void testZeroRateBaselineAndSubjectShouldNotBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(0, 100);
		RateMeasurement subj = new BasicRateMeasurement(0, 100);
		CorrectedBasicRiskDifference or = new CorrectedBasicRiskDifference(base, subj);
		assertFalse(or.isDefined());
	}
	
	@Test
	public void testZeroSubjectRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(50, 100);
		RateMeasurement subj = new BasicRateMeasurement(0, 100);
		CorrectedBasicRiskDifference or = new CorrectedBasicRiskDifference(base, subj);
		assertTrue(or.isDefined());
	}

	@Test
	public void testFullBaselineRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(100, 100);
		RateMeasurement subj = new BasicRateMeasurement(50, 100);
		CorrectedBasicRiskDifference or = new CorrectedBasicRiskDifference(base, subj);
		assertTrue(or.isDefined());
	}
	
	@Test
	public void testFullRateBaselineAndSubjectShouldNotBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(100, 100);
		RateMeasurement subj = new BasicRateMeasurement(100, 100);
		CorrectedBasicRiskDifference or = new CorrectedBasicRiskDifference(base, subj);
		assertFalse(or.isDefined());
	}
	
	@Test
	public void testFullSubjectRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(50, 100);
		RateMeasurement subj = new BasicRateMeasurement(100, 100);
		CorrectedBasicRiskDifference or = new CorrectedBasicRiskDifference(base, subj);
		assertTrue(or.isDefined());
	}
	
	@Test
	public void testUndefinedShouldResultInNaN() {
		RateMeasurement rmA1 = new BasicRateMeasurement(0, 100);
		RateMeasurement rmC1 = new BasicRateMeasurement(0, 100);
		BasicRiskDifference or = new CorrectedBasicRiskDifference(rmA1, rmC1);
		assertEquals(Double.NaN, or.getError(), 0.001);
		assertEquals(Double.NaN, or.getMu(), 0.001);
		assertEquals(Double.NaN, or.getConfidenceInterval().getPointEstimate(), 0.001);
	}

	@Test
	public void testDefinedShouldNotResultInNaN() {
		RateMeasurement rmA1 = new BasicRateMeasurement(0, 100);
		RateMeasurement rmC1 = new BasicRateMeasurement(50, 100);
		BasicRiskDifference or = new CorrectedBasicRiskDifference(rmA1, rmC1);
		assertFalse(or.getError() == Double.NaN);
		assertFalse(or.getMu() == Double.NaN);
		assertFalse(Double.NaN == or.getConfidenceInterval().getPointEstimate()); 
	}

	@Test
	public void testError() {
		// c=0.5, n2 = 145, a = 73.5, n1 = 143 -> b = 69.5, d = 144.5
		double expectedVal = Math.sqrt(73.5 * 69.5 / Math.pow(143, 3) + 0.5 * 144.5 / Math.pow(145, 3));
		assertEquals(expectedVal, d_riskDifferenceBennie.getError(), 0.000001);
	}
	
	@Test
	public void testMu() {
		// c=0.5, n2 = 145, a = 73.5, n1 = 143 -> b = 69.5, d = 144.5
		double expected = (73.5/143 - 0.5/145);
		assertEquals(expected, d_riskDifferenceBennie.getMu(), 0.00001);
	}
	
	
	private Study createStudy(String studyName, int fluoxResp, int fluoxSize, int sertraResp, int sertraSize)
	{
		Study s = new Study(studyName, d_ind);
		s.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(d_ep));
		Arm g_fluox = s.createAndAddArm("fluox", fluoxSize, d_fluox, new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY));
		Arm g_parox = s.createAndAddArm("sertr", sertraSize, d_sertra, new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY));		
		
		s.getArms().add(g_parox);
		s.getArms().add(g_fluox);
		
		BasicRateMeasurement m_parox = (BasicRateMeasurement) d_ep.buildMeasurement(g_parox);
		BasicRateMeasurement m_fluox = (BasicRateMeasurement) d_ep.buildMeasurement(g_fluox);
		
		m_parox.setRate(sertraResp);
		m_fluox.setRate(fluoxResp);
		
		s.setMeasurement(d_ep, g_parox, m_parox);
		s.setMeasurement(d_ep, g_fluox, m_fluox);		
		
		return s;
	}
}
