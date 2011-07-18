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
import org.drugis.addis.entities.analysis.DrugSet;
import org.junit.Before;
import org.junit.Test;

public class CorrectedOddsRatioTest {
	private Drug d_fluox;
	private Drug d_sertra;
	
	private Indication d_ind;
	private Endpoint d_ep;
	
	private Study d_bennie, d_boyer, d_fava, d_newhouse, d_sechter;
	
	private BasicOddsRatio d_ratioBennie, d_ratioBoyer, d_ratioFava, d_ratioNewhouse, d_ratioSechter;

	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertra = new Drug("Sertraline","02");
		d_ep = new Endpoint("ep", Variable.Type.RATE);
		
		d_bennie = createStudy("Bennie 1995",0,144,73,142);
		d_boyer = createStudy("Boyer 1998", 50,120, 0,122);
		d_fava = createStudy("Fava 2002", 0, 92, 70, 96);
		d_newhouse = createStudy("Newhouse 2000", 50,119, 0,117);
		d_sechter = createStudy("Sechter 1999", 70,120, 86,118);
		d_ratioBennie = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_bennie, d_ep, new DrugSet(d_fluox), new DrugSet(d_sertra), BasicOddsRatio.class, true);
		d_ratioBoyer = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_boyer, d_ep, new DrugSet(d_fluox), new DrugSet(d_sertra), BasicOddsRatio.class, true);
		d_ratioFava = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_fava, d_ep, new DrugSet(d_fluox), new DrugSet(d_sertra), BasicOddsRatio.class, true);
		d_ratioNewhouse = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_newhouse, d_ep, new DrugSet(d_fluox), new DrugSet(d_sertra), BasicOddsRatio.class, true);
		d_ratioSechter = (BasicOddsRatio) RelativeEffectFactory.buildRelativeEffect(d_sechter, d_ep, new DrugSet(d_fluox), new DrugSet(d_sertra), BasicOddsRatio.class, true);

	}

	@Test
	public void testMeans() {
		assertEquals(5.722385342, d_ratioBennie.getMu(), 0.000001);
		assertEquals(-5.167618837007818, d_ratioBoyer.getMu(), 0.000001);
		assertEquals(6.198823801904371, d_ratioFava.getMu(), 0.000001);
		assertEquals(-5.140232097854726, d_ratioNewhouse.getMu(), 0.000001);
		assertEquals(0.645264951065233, d_ratioSechter.getMu(), 0.000001);
	}
	
	@Test
	public void testError() {
		// c=0.5, n2 = 145, a = 73.5, n1 = 143 -> b = 69.5, d = 144.5
		double expected = Math.sqrt(1.0/73.5 + 1.0/69.5 + 1.0/0.5 + 1.0/144.5);
		assertEquals(expected, d_ratioBennie.getError(), 0.001);
	}
	
	@Test
	public void testZeroBaselineRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(0, 100);
		RateMeasurement subj = new BasicRateMeasurement(50, 100);
		CorrectedBasicOddsRatio or = new CorrectedBasicOddsRatio(base, subj);
		assertTrue(or.isDefined());
	}
	
	@Test
	public void testZeroRateBaselineAndSubjectShouldNotBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(0, 100);
		RateMeasurement subj = new BasicRateMeasurement(0, 100);
		CorrectedBasicOddsRatio or = new CorrectedBasicOddsRatio(base, subj);
		assertFalse(or.isDefined());
	}
	
	@Test
	public void testZeroSubjectRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(50, 100);
		RateMeasurement subj = new BasicRateMeasurement(0, 100);
		CorrectedBasicOddsRatio or = new CorrectedBasicOddsRatio(base, subj);
		assertTrue(or.isDefined());
	}

	@Test
	public void testFullBaselineRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(100, 100);
		RateMeasurement subj = new BasicRateMeasurement(50, 100);
		CorrectedBasicOddsRatio or = new CorrectedBasicOddsRatio(base, subj);
		assertTrue(or.isDefined());
	}
	
	@Test
	public void testFullRateBaselineAndSubjectShouldNotBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(100, 100);
		RateMeasurement subj = new BasicRateMeasurement(100, 100);
		CorrectedBasicOddsRatio or = new CorrectedBasicOddsRatio(base, subj);
		assertFalse(or.isDefined());
	}
	
	@Test
	public void testFullSubjectRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(50, 100);
		RateMeasurement subj = new BasicRateMeasurement(100, 100);
		CorrectedBasicOddsRatio or = new CorrectedBasicOddsRatio(base, subj);
		assertTrue(or.isDefined());
	}
	
	@Test
	public void testUndefinedShouldResultInNaN() {
		RateMeasurement rmA1 = new BasicRateMeasurement(0, 100);
		RateMeasurement rmC1 = new BasicRateMeasurement(0, 100);
		BasicOddsRatio or = new CorrectedBasicOddsRatio(rmA1, rmC1);
		assertEquals(Double.NaN, or.getError(), 0.001);
		assertEquals(Double.NaN, or.getMu(), 0.001);
		assertEquals(Double.NaN, or.getConfidenceInterval().getPointEstimate(), 0.001);
	}

	@Test
	public void testDefinedShouldNotResultInNaN() {
		RateMeasurement rmA1 = new BasicRateMeasurement(0, 100);
		RateMeasurement rmC1 = new BasicRateMeasurement(50, 100);
		BasicOddsRatio or = new CorrectedBasicOddsRatio(rmA1, rmC1);
		assertFalse(or.getError() == Double.NaN);
		assertFalse(or.getMu() == Double.NaN);
		assertFalse(Double.NaN == or.getConfidenceInterval().getPointEstimate()); 
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
