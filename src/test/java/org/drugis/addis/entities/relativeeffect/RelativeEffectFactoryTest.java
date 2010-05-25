/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import static org.junit.Assert.*;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.LogOddsRatio;
import org.drugis.addis.entities.relativeeffect.LogRiskRatio;
import org.drugis.addis.entities.relativeeffect.MeanDifference;
import org.drugis.addis.entities.relativeeffect.OddsRatio;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;
import org.drugis.addis.entities.relativeeffect.RiskDifference;
import org.drugis.addis.entities.relativeeffect.RiskRatio;
import org.drugis.addis.entities.relativeeffect.StandardisedMeanDifference;
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
		
		RelativeEffect<?> expected = new StandardisedMeanDifference(
				(ContinuousMeasurement)s.getMeasurement(e, pSubj),
				(ContinuousMeasurement)s.getMeasurement(e, pBase));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						StandardisedMeanDifference.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetStandardizedMeanDifferenceRate() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointHamd(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				StandardisedMeanDifference.class);
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
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointHamd(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				MeanDifference.class);
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
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				OddsRatio.class);
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
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				RiskRatio.class);
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
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				RiskDifference.class);
	}
	
	@Test
	public void testGetLogRiskRatio() {
		Study s = ExampleData.buildStudyChouinard();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		Arm pBase = s.getArms().get(0);
		Arm pSubj = s.getArms().get(1);
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
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				LogRiskRatio.class);
	}
	
	@Test
	public void testGetLogOddsRatio() {
		Study s = ExampleData.buildStudyChouinard();
		Endpoint e = ExampleData.buildEndpointHamd();
		Drug base = ExampleData.buildDrugParoxetine();
		Drug subj = ExampleData.buildDrugFluoxetine();
		Arm pBase = s.getArms().get(0);
		Arm pSubj = s.getArms().get(1);
		// sanity check:
		assertEquals(base, pBase.getDrug());
		assertEquals(subj, pSubj.getDrug());
		
		RelativeEffect<?> expected = new LogOddsRatio(
				(RateMeasurement)s.getMeasurement(e, pBase),
				(RateMeasurement)s.getMeasurement(e, pSubj));
		
		RelativeEffect<?> actual =
				RelativeEffectFactory.buildRelativeEffect(s, e, base, subj,
						LogOddsRatio.class);
		
		assertRelativeEffectEqual(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetLogOddsRatioCont() {
		RelativeEffectFactory.buildRelativeEffect(
				ExampleData.buildStudyChouinard(),
				ExampleData.buildEndpointCgi(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugFluoxetine(),
				LogOddsRatio.class);
	}
	
	private static void assertRelativeEffectEqual(RelativeEffect<?> expected,
			RelativeEffect<?> actual) {
		assertEquals(expected.getBaseline(), actual.getBaseline());
		assertEquals(expected.getSubject(), actual.getSubject());
		assertEquals(expected.getClass(), actual.getClass());
	}
}
