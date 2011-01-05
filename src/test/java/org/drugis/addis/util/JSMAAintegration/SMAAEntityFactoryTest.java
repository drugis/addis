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

package org.drugis.addis.util.JSMAAintegration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.relativeeffect.Beta;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.TransformedStudentT;
import org.junit.Before;
import org.junit.Test;

import fi.smaa.jsmaa.model.BetaMeasurement;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;

public class SMAAEntityFactoryTest {
	private SMAAEntityFactory<Drug> d_smaaFactory;
	private MetaBenefitRiskAnalysis d_brAnalysis;
	
	private SMAAEntityFactory<Arm> d_smaaFactoryArm;
	private StudyBenefitRiskAnalysis d_brAnalysisStudy;

	@Before
	public void setup() {
		d_brAnalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		d_smaaFactory = new SMAAEntityFactory<Drug>();
		
		d_smaaFactoryArm = new SMAAEntityFactory<Arm>();
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointCgi());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		List<Arm> alternatives = ExampleData.buildStudyChouinard().getArms();
		d_brAnalysisStudy = new StudyBenefitRiskAnalysis("Study Analysis", ExampleData.buildIndicationDepression(),
				ExampleData.buildStudyChouinard(), criteria, alternatives, AnalysisType.SMAA);
	}
	
	@Test
	public void testCreateCardinalMeasurementRate() {
		
		GaussianBase relativeEffect = d_brAnalysis.getRelativeEffectDistribution(ExampleData.buildDrugFluoxetine(), ExampleData.buildEndpointHamd());
		CardinalMeasurement actual = SMAAEntityFactory.createCardinalMeasurement(relativeEffect);
		assertTrue(!((LogNormalMeasurement) actual).getMean().isNaN());
		assertTrue(actual instanceof LogNormalMeasurement);
		assertEquals(Math.log(relativeEffect.getQuantile(0.50)),((LogNormalMeasurement) actual).getMean(), 0.0001);
		assertEquals(relativeEffect.getSigma(),((LogNormalMeasurement) actual).getStDev(), 0.0001);
	}
	
	
	@Test
	public void testCreateSmaaModel() {
		SMAAModel smaaModel = d_smaaFactory.createSmaaModel(d_brAnalysis);
		for(OutcomeMeasure om : d_brAnalysis.getCriteria()){
			for(Drug d : d_brAnalysis.getDrugs()){
				if (d.equals(d_brAnalysis.getBaseline()))
					continue;
				fi.smaa.jsmaa.model.Measurement actualMeasurement = smaaModel.getMeasurement(d_smaaFactory.getCriterion(om), d_smaaFactory.getAlternative(d));
				GaussianBase expDistribution = d_brAnalysis.getAbsoluteEffectDistribution(d, om);
				assertEquals(expDistribution.getMu(), ((LogitNormalMeasurement) actualMeasurement).getMean(), 0.0001);
				assertEquals(expDistribution.getSigma(), ((LogitNormalMeasurement) actualMeasurement).getStDev(), 0.0001);
			}
		}
	}
	
	@Test 
	public void testGetOutcomeMeasure() {
		d_smaaFactory.createSmaaModel(d_brAnalysis);
		for (OutcomeMeasure om : d_brAnalysis.getCriteria()) {
			CardinalCriterion crit = d_smaaFactory.getCriterion(om);
			assertEquals(om, d_smaaFactory.getOutcomeMeasure(crit));
		}
	}
	
	@Test
	public void testCreateSmaaModelStudy() {
		SMAAModel smaaModel = d_smaaFactoryArm.createSmaaModel(d_brAnalysisStudy);
		for(OutcomeMeasure om : d_brAnalysisStudy.getCriteria()){
			for(Arm d : d_brAnalysisStudy.getAlternatives()){
				fi.smaa.jsmaa.model.Measurement actualMeasurement = 
					smaaModel.getMeasurement(d_smaaFactoryArm.getCriterion(om), d_smaaFactoryArm.getAlternative(d));
				Distribution expDistribution = d_brAnalysisStudy.getMeasurement(d, om);
				if (om.equals(ExampleData.buildEndpointCgi())) {
					TransformedStudentT expected = (TransformedStudentT)expDistribution;
					GaussianMeasurement actual = (GaussianMeasurement)actualMeasurement;
					assertEquals(expected.getMu(), actual.getMean(), 0.0000001);
					assertEquals(expected.getSigma(), actual.getStDev(), 0.0000001);
				}
				if (om.equals(ExampleData.buildAdverseEventConvulsion())) {
					Beta expected = (Beta)expDistribution;
					BetaMeasurement actual = (BetaMeasurement)actualMeasurement;
					assertEquals(expected.getAlpha(), actual.getAlpha(), 0.0000001);
					assertEquals(expected.getBeta(), actual.getBeta(), 0.0000001);
				}
			}
		}
	}
}
