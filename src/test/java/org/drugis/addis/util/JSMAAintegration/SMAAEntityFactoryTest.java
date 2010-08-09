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

package org.drugis.addis.util.JSMAAintegration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.junit.Before;
import org.junit.Test;

import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;

public class SMAAEntityFactoryTest {
	private SMAAEntityFactory d_SMAAFactory;
	private BenefitRiskAnalysis d_BRAnalysis;

	@Before
	public void setup() {
		d_BRAnalysis = ExampleData.buildMockBenefitRiskAnalysis();
		d_SMAAFactory = new SMAAEntityFactory();
	}
	
	@Test
	public void testCreateCardinalMeasurementRate() {
		
		GaussianBase relativeEffect = d_BRAnalysis.getRelativeEffectDistribution(ExampleData.buildDrugFluoxetine(), ExampleData.buildEndpointHamd());
		CardinalMeasurement actual = SMAAEntityFactory.createCardinalMeasurement(relativeEffect);
		assertTrue(!((LogNormalMeasurement) actual).getMean().isNaN());
		assertTrue(actual instanceof LogNormalMeasurement);
		assertEquals(Math.log(relativeEffect.getQuantile(0.50)),((LogNormalMeasurement) actual).getMean(), 0.0001);
		assertEquals(relativeEffect.getSigma(),((LogNormalMeasurement) actual).getStDev(), 0.0001);
	}
	
	
	@Test
	public void testCreateSmaaModel() {
		SMAAModel smaaModel = d_SMAAFactory.createSmaaModel(d_BRAnalysis);
		for(OutcomeMeasure om : d_BRAnalysis.getOutcomeMeasures()){
			for(Drug d : d_BRAnalysis.getDrugs()){
				if (d.equals(d_BRAnalysis.getBaseline()))
					continue;
				fi.smaa.jsmaa.model.Measurement actualMeasurement = smaaModel.getMeasurement(d_SMAAFactory.getCriterion(om), d_SMAAFactory.getAlternative(d));
				GaussianBase expDistribution = d_BRAnalysis.getRelativeEffectDistribution(d, om);
				assertEquals(Math.log(expDistribution.getQuantile(0.50)), ((LogNormalMeasurement) actualMeasurement).getMean(), 0.0001);
			}
		}
	}
	
	@Test 
	public void testGetOutcomeMeasure() {
		d_SMAAFactory.createSmaaModel(d_BRAnalysis);
		for (OutcomeMeasure om : d_BRAnalysis.getOutcomeMeasures()) {
			CardinalCriterion crit = d_SMAAFactory.getCriterion(om);
			assertEquals(om, d_SMAAFactory.getOutcomeMeasure(crit));
		}
	}
}
