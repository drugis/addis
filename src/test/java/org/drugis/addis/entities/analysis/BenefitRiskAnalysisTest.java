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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.common.AlphabeticalComparator;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;



public class BenefitRiskAnalysisTest {
	private MetaBenefitRiskAnalysis d_BRAnalysis;

	@Before
	public void setup(){
		d_BRAnalysis = ExampleData.buildMockBenefitRiskAnalysis();
	}
	
	@Test
	public void testGetSetName() {
		JUnitUtil.testSetter(d_BRAnalysis, MetaBenefitRiskAnalysis.PROPERTY_NAME, 
				"testBenefitRiskAnalysis", "some new name");
	}
	
	@Test
	public void testGetSetIndication() {
		JUnitUtil.testSetter(d_BRAnalysis, MetaBenefitRiskAnalysis.PROPERTY_INDICATION, 
				ExampleData.buildIndicationDepression(), ExampleData.buildIndicationChronicHeartFailure());
	}
	
	@Test
	public void testGetSetOutcomeMeasures() {
		ArrayList<OutcomeMeasure> newList = new ArrayList<OutcomeMeasure>();
		newList.add(ExampleData.buildEndpointCVdeath());
		newList.add(ExampleData.buildAdverseEventConvulsion());
		Collections.sort(newList, new AlphabeticalComparator());
		JUnitUtil.testSetter(d_BRAnalysis, MetaBenefitRiskAnalysis.PROPERTY_OUTCOMEMEASURES, 
				d_BRAnalysis.getOutcomeMeasures(), newList);
	}
	
	@Test
	public void testGetSetDrugs() {
		ArrayList<Drug> newList = new ArrayList<Drug>();
		newList.add(ExampleData.buildDrugViagra());
		newList.add(ExampleData.buildDrugCandesartan());

		Collections.sort(newList, new AlphabeticalComparator());
		//JUnitUtil.testSetter(d_BRAnalysis, BenefitRiskAnalysis.PROPERTY_DRUGS, 
		//		d_BRAnalysis.getDrugs(), newList);
		d_BRAnalysis.setDrugs(newList);
		newList.add(ExampleData.buildDrugParoxetine());
		JUnitUtil.assertAllAndOnly(newList, d_BRAnalysis.getDrugs());
	}
	
	@Test
	public void testGetSetBaseLine() {
		JUnitUtil.testSetter(d_BRAnalysis, MetaBenefitRiskAnalysis.PROPERTY_BASELINE, 
				d_BRAnalysis.getBaseline(), ExampleData.buildDrugViagra());
	}
	
	@Test
	public void testGetSetMetaAnalyses() {
		ArrayList<MetaAnalysis> newList = new ArrayList<MetaAnalysis>();
		newList.add(ExampleData.buildNetworkMetaAnalysis());
		newList.add(ExampleData.buildNetworkMetaAnalysisAlternative());
		Collections.sort(newList,new AlphabeticalComparator());
		JUnitUtil.testSetter(d_BRAnalysis, MetaBenefitRiskAnalysis.PROPERTY_METAANALYSES, 
				d_BRAnalysis.getMetaAnalyses(), newList);
	}
	
	@Test
	public void testEquals(){
		assertFalse(d_BRAnalysis.equals("nope, no meta Analysis"));
		MetaBenefitRiskAnalysis otherBRAnalysis = ExampleData.buildMockBenefitRiskAnalysis();
		assertTrue(d_BRAnalysis.equals(otherBRAnalysis));
		otherBRAnalysis.setName("some new name");
		assertFalse(d_BRAnalysis.equals(otherBRAnalysis));
		assertTrue(d_BRAnalysis.equals(d_BRAnalysis));
	}
	
	@Test 
	public void testCompareTo(){
		assertEquals(1, d_BRAnalysis.compareTo(null));
		assertEquals(0, d_BRAnalysis.compareTo(d_BRAnalysis));
		MetaBenefitRiskAnalysis otherBRAnalysis = ExampleData.buildMockBenefitRiskAnalysis();
		assertEquals(0, d_BRAnalysis.compareTo(otherBRAnalysis));
		otherBRAnalysis.setName("some new name");
		assertNotSame(0, d_BRAnalysis.compareTo(otherBRAnalysis));
	}
	
	@Test
	public void testToString() {
		assertEquals(d_BRAnalysis.getName(), d_BRAnalysis.toString());
	}
	
	@Test
	public void testGetDistribution() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		
		Drug fluox = ExampleData.buildDrugFluoxetine();
		GaussianBase actualDist = d_BRAnalysis.getRelativeEffectDistribution(fluox, om);
		
		RelativeEffect<? extends Measurement> expected = ExampleData.buildMetaAnalysisHamd().getRelativeEffect(
				ExampleData.buildDrugParoxetine(), fluox, BasicOddsRatio.class);
		assertNotNull(actualDist);
		assertNotNull(expected);
		assertEquals(expected.getConfidenceInterval().getPointEstimate(), actualDist.getQuantile(0.50), 0.00001);
		assertEquals(expected.getConfidenceInterval().getLowerBound(), actualDist.getQuantile(0.025), 0.00001);
		assertEquals(expected.getConfidenceInterval().getUpperBound(), actualDist.getQuantile(0.975), 0.00001);
	}
	
	@Test
	public void testGetAbsoluteEffect() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		Drug fluox = ExampleData.buildDrugFluoxetine();
		Drug parox = ExampleData.buildDrugParoxetine();
		
		LogGaussian baseline = (LogGaussian)d_BRAnalysis.getBaselineDistribution(om);
		LogGaussian relative = (LogGaussian)d_BRAnalysis.getRelativeEffectDistribution(fluox, om);
		double expectedMu = baseline.getMu() + relative.getMu();
		double expectedSigma = Math.sqrt(Math.pow(baseline.getSigma(), 2) + Math.pow(relative.getSigma(), 2));

		LogGaussian absoluteF = (LogGaussian)d_BRAnalysis.getAbsoluteEffectDistribution(fluox, om);
		assertEquals(expectedMu, absoluteF.getMu(), 0.0000001);
		assertEquals(expectedSigma, absoluteF.getSigma(), 0.0000001);

		LogGaussian absoluteP = (LogGaussian)d_BRAnalysis.getAbsoluteEffectDistribution(parox, om);
		assertEquals(baseline.getMu(), absoluteP.getMu(), 0.0000001);
		assertEquals(baseline.getSigma(), absoluteP.getSigma(), 0.0001);
	}
	
	@Test
	public void testGetAbsoluteEffectContinuous() {
		OutcomeMeasure om = ExampleData.buildEndpointCgi();
		Drug fluox = ExampleData.buildDrugFluoxetine();
		Drug parox = ExampleData.buildDrugParoxetine();
		MetaBenefitRiskAnalysis br = ExampleData.realBuildContinuousMockBenefitRisk();
		
		Gaussian baseline = (Gaussian)br.getBaselineDistribution(om);
		Gaussian relative = (Gaussian)br.getRelativeEffectDistribution(parox, om);
		double expectedMu = baseline.getMu() + relative.getMu();
		double expectedSigma = Math.sqrt(Math.pow(baseline.getSigma(), 2) + Math.pow(relative.getSigma(), 2));

		Gaussian absoluteP = (Gaussian)br.getAbsoluteEffectDistribution(parox, om);
		assertEquals(expectedMu, absoluteP.getMu(), 0.0000001);
		assertEquals(expectedSigma, absoluteP.getSigma(), 0.0000001);
		
		Gaussian absoluteF = (Gaussian)br.getAbsoluteEffectDistribution(fluox, om);
		assertEquals(baseline.getMu(), absoluteF.getMu(), 0.0000001);
		assertEquals(baseline.getSigma(), absoluteF.getSigma(), 0.0001);
	}
}
