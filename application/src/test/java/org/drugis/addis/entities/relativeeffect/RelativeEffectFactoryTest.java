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

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.junit.Before;
import org.junit.Test;

public class RelativeEffectFactoryTest {
	
	private Study d_s;
	private Endpoint d_eCont;
	private TreatmentDefinition d_fluox;
	private TreatmentDefinition d_parox;
	private Arm d_pBase;
	private Arm d_pSubj;
	private Endpoint d_eRate;

	@Before
	public void setUp() {
		d_s = ExampleData.buildStudyChouinard();
		d_eCont = ExampleData.buildEndpointCgi();
		d_eRate = ExampleData.buildEndpointHamd();
		d_parox = TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine());
		d_fluox = TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine());
		d_pBase = d_s.getArms().get(0);
		d_pSubj = d_s.getArms().get(1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetStandardizedMeanDifferenceRate() {
		RelativeEffectFactory.buildRelativeEffect(d_s, d_eRate, d_parox, d_fluox, BasicStandardisedMeanDifference.class);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMeanDifferenceRate() {
		RelativeEffectFactory.buildRelativeEffect(d_s, d_eRate, d_parox, d_fluox, BasicMeanDifference.class, false);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetOddsRatioCont() {
		RelativeEffectFactory.buildRelativeEffect(d_s, d_eCont, d_parox, d_fluox, BasicOddsRatio.class);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetRiskRatioCont() {
		RelativeEffectFactory.buildRelativeEffect(d_s, d_eCont, d_parox, d_fluox, BasicRiskRatio.class);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetRiskDifferenceCont() {
		RelativeEffectFactory.buildRelativeEffect(d_s, d_eCont, d_parox, d_fluox, BasicRiskDifference.class);
	}
	
	@Test
	public void testGetMeanDifference() {
		RelativeEffect<?> expected = new BasicMeanDifference(
				(ContinuousMeasurement)d_s.getMeasurement(d_eCont, d_pBase),
				(ContinuousMeasurement)d_s.getMeasurement(d_eCont, d_pSubj));
		
		RelativeEffect<?> actual = RelativeEffectFactory.buildRelativeEffect(d_s, d_eCont, d_parox, d_fluox, BasicMeanDifference.class, false);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test
	public void testGetStandardizedMeanDifference() {
		// Sanity check
		assertEquals(d_parox, d_s.getTreatmentDefinition(d_pBase));
		assertEquals(d_fluox, d_s.getTreatmentDefinition(d_pSubj));
		
		RelativeEffect<?> expected = new BasicStandardisedMeanDifference(
				(ContinuousMeasurement)d_s.getMeasurement(d_eCont, d_pBase),
				(ContinuousMeasurement)d_s.getMeasurement(d_eCont, d_pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(d_s, d_eCont, d_parox, d_fluox, BasicStandardisedMeanDifference.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test
	public void testGetOddsRatio() {
		RelativeEffect<?> expected = new BasicOddsRatio(
				(RateMeasurement)d_s.getMeasurement(d_eRate, d_pBase),
				(RateMeasurement)d_s.getMeasurement(d_eRate, d_pSubj));
		
		RelativeEffect<?> actual = RelativeEffectFactory.buildRelativeEffect(d_s, d_eRate, d_parox, d_fluox, BasicOddsRatio.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	
	@Test
	public void testGetRiskRatio() {
		RelativeEffect<?> expected = new BasicRiskRatio(
				(RateMeasurement)d_s.getMeasurement(d_eRate, d_pBase),
				(RateMeasurement)d_s.getMeasurement(d_eRate, d_pSubj));
		
		RelativeEffect<?> actual = RelativeEffectFactory.buildRelativeEffect(d_s, d_eRate, d_parox, d_fluox, BasicRiskRatio.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test
	public void testGetRiskDifference() {
		RelativeEffect<?> expected = new BasicRiskDifference(
				(RateMeasurement)d_s.getMeasurement(d_eRate, d_pBase),
				(RateMeasurement)d_s.getMeasurement(d_eRate, d_pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(d_s, d_eRate, d_parox, d_fluox, BasicRiskDifference.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	private static void assertRelativeEffectEqual(RelativeEffect<?> expected,
			RelativeEffect<?> actual) {
		assertEquals(expected.getClass(), actual.getClass());
		assertEquals(expected.getConfidenceInterval(), actual.getConfidenceInterval());
		
		if (expected instanceof BasicRelativeEffect<?>) {
			BasicRelativeEffect<?> e = (BasicRelativeEffect<?>) expected;
			BasicRelativeEffect<?> a = (BasicRelativeEffect<?>) actual;
			assertEquals(e.getBaseline(), a.getBaseline());
			assertEquals(e.getSubject(), a.getSubject()); 
		}
	}
}
