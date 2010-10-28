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

package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.junit.Test;

public class RelativeEffectFactoryTest {
	@Test
	public void testGetStandardizedMeanDifference() {
		Study s = ExampleData.buildStudyChouinard();
		Endpoint e = ExampleData.buildEndpointCgi();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		Arm pBase = s.getArms().get(0);
		Arm pSubj = s.getArms().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new BasicStandardisedMeanDifference(
				(ContinuousMeasurement)s.getMeasurement(e, pBase),
				(ContinuousMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						BasicStandardisedMeanDifference.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetStandardizedMeanDifferenceRate() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointHamd(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				BasicStandardisedMeanDifference.class);
	}
	
	@Test
	public void testGetMeanDifference() {
		Study s = ExampleData.buildStudyChouinard();
		Endpoint e = ExampleData.buildEndpointCgi();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		Arm pBase = s.getArms().get(0);
		Arm pSubj = s.getArms().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new BasicMeanDifference(
				(ContinuousMeasurement)s.getMeasurement(e, pBase),
				(ContinuousMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						BasicMeanDifference.class, false);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetMeanDifferenceRate() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointHamd(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				BasicMeanDifference.class, false);
	}
	
	@Test
	public void testGetOddsRatio() {
		Study s = ExampleData.buildStudyChouinard();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		Arm pBase = s.getArms().get(0);
		Arm pSubj = s.getArms().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new BasicOddsRatio(
				(RateMeasurement)s.getMeasurement(e, pBase),
				(RateMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						BasicOddsRatio.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetOddsRatioCont() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				BasicOddsRatio.class);
	}
	
	@Test
	public void testGetRiskRatio() {
		Study s = ExampleData.buildStudyChouinard();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		Arm pBase = s.getArms().get(0);
		Arm pSubj = s.getArms().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new BasicRiskRatio(
				(RateMeasurement)s.getMeasurement(e, pBase),
				(RateMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						BasicRiskRatio.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetRiskRatioCont() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				BasicRiskRatio.class);
	}
	
	@Test
	public void testGetRiskDifference() {
		Study s = ExampleData.buildStudyChouinard();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		Arm pBase = s.getArms().get(0);
		Arm pSubj = s.getArms().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new BasicRiskDifference(
				(RateMeasurement)s.getMeasurement(e, pBase),
				(RateMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						BasicRiskDifference.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetRiskDifferenceCont() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				BasicRiskDifference.class);
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
