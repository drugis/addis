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

package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.LogitGaussian;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.junit.Before;
import org.junit.Test;



public class MetaBenefitRiskAnalysisTest {
	private MetaBenefitRiskAnalysis d_BRAnalysis;

	@Before
	public void setup(){
		d_BRAnalysis = ExampleData.buildMetaBenefitRiskAnalysis();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testEquals(){
		assertFalse(d_BRAnalysis.equals("nope, no meta Analysis"));
		BenefitRiskAnalysis otherBRAnalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		assertTrue(d_BRAnalysis.equals(otherBRAnalysis));
		otherBRAnalysis = ExampleData.buildStudyBenefitRiskAnalysis();
		assertFalse(d_BRAnalysis.equals(otherBRAnalysis));
		assertTrue(d_BRAnalysis.equals(d_BRAnalysis));
	}

	@SuppressWarnings("rawtypes")
	@Test 
	public void testCompareTo(){
		assertTrue(d_BRAnalysis.compareTo(null) > 0);
		assertEquals(0, d_BRAnalysis.compareTo(d_BRAnalysis));
		BenefitRiskAnalysis otherBRAnalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		assertEquals(0, d_BRAnalysis.compareTo(otherBRAnalysis));
		otherBRAnalysis = ExampleData.buildStudyBenefitRiskAnalysis();
		assertTrue(d_BRAnalysis.compareTo(otherBRAnalysis) > 0);
	}
	
	@Test
	public void testToString() {
		assertEquals(d_BRAnalysis.getName(), d_BRAnalysis.toString());
	}
	
	@Test
	public void testGetDistribution() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		
		GaussianBase actualDist = d_BRAnalysis.getRelativeEffectDistribution(om, new DrugSet(ExampleData.buildDrugFluoxetine()));
		
		final MetaAnalysis ma = ExampleData.buildMetaAnalysisHamd();
		MultivariateNormalSummary summary = ma.getRelativeEffectsSummary();
		LogGaussian expected = new LogGaussian(summary.getMeanVector()[0], Math.sqrt(summary.getCovarianceMatrix()[0][0])); 
		assertEquals(new DrugSet(ExampleData.buildDrugParoxetine()), ma.getIncludedDrugs().get(0));

		assertEquals(expected.getQuantile(0.50), actualDist.getQuantile(0.50), 0.00001);
		assertEquals(expected.getQuantile(0.025), actualDist.getQuantile(0.025), 0.00001);
		assertEquals(expected.getQuantile(0.975), actualDist.getQuantile(0.975), 0.00001);
	}
	
	@Test
	public void testGetMeasurement() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		Drug fluox = ExampleData.buildDrugFluoxetine();
		Drug parox = ExampleData.buildDrugParoxetine();
		
		LogGaussian baseline = (LogGaussian)d_BRAnalysis.getBaselineDistribution(om);
		LogGaussian relative = (LogGaussian)d_BRAnalysis.getRelativeEffectDistribution(om, new DrugSet(fluox));
		double expectedMu = baseline.getMu() + relative.getMu();
		double expectedSigma = Math.sqrt(Math.pow(baseline.getSigma(), 2) + Math.pow(relative.getSigma(), 2));

		LogitGaussian absoluteF = (LogitGaussian)d_BRAnalysis.getMeasurement(om, new DrugSet(fluox));
		assertEquals(expectedMu, absoluteF.getMu(), 0.0000001);
		assertEquals(expectedSigma, absoluteF.getSigma(), 0.0000001);

		LogitGaussian absoluteP = (LogitGaussian)d_BRAnalysis.getMeasurement(om, new DrugSet(parox));
		assertEquals(baseline.getMu(), absoluteP.getMu(), 0.0000001);
		assertEquals(baseline.getSigma(), absoluteP.getSigma(), 0.0001);
	}
	
	@Test
	public void testGetMeasurementContinuous() {
		OutcomeMeasure om = ExampleData.buildEndpointCgi();
		Drug fluox = ExampleData.buildDrugFluoxetine();
		Drug parox = ExampleData.buildDrugParoxetine();
		MetaBenefitRiskAnalysis br = ExampleData.realBuildContinuousMockBenefitRisk();
		
		Gaussian baseline = (Gaussian)br.getBaselineDistribution(om);
		Gaussian relative = (Gaussian)br.getRelativeEffectDistribution(om, new DrugSet(parox));
		double expectedMu = baseline.getMu() + relative.getMu();
		double expectedSigma = Math.sqrt(Math.pow(baseline.getSigma(), 2) + Math.pow(relative.getSigma(), 2));

		Gaussian absoluteP = (Gaussian)br.getMeasurement(om, new DrugSet(parox));
		assertEquals(expectedMu, absoluteP.getMu(), 0.0000001);
		assertEquals(expectedSigma, absoluteP.getSigma(), 0.0000001);
		
		Gaussian absoluteF = (Gaussian)br.getMeasurement(om, new DrugSet(fluox));
		assertEquals(baseline.getMu(), absoluteF.getMu(), 0.0000001);
		assertEquals(baseline.getSigma(), absoluteF.getSigma(), 0.0001);
	}
	
	@Test
	public void testLOBrianAnalysisException() throws IllegalArgumentException {
		Indication indication = ExampleData.buildIndicationDepression();
		
		List<OutcomeMeasure> outcomeMeasureList = new ArrayList<OutcomeMeasure>();
		outcomeMeasureList.add(ExampleData.buildEndpointHamd());
		outcomeMeasureList.add(ExampleData.buildAdverseEventConvulsion());
		
		List<MetaAnalysis> metaAnalysisList = new ArrayList<MetaAnalysis>();
		metaAnalysisList.add(ExampleData.buildMetaAnalysisHamd());
		metaAnalysisList.add(ExampleData.buildMetaAnalysisConv());
		
		Drug parox = ExampleData.buildDrugParoxetine();
		List<DrugSet> drugList = new ArrayList<DrugSet>();
		drugList.add(new DrugSet(ExampleData.buildDrugFluoxetine()));
		drugList.add(new DrugSet(ExampleData.buildDrugSertraline()));
		
		boolean caught = false;
		try {
			new MetaBenefitRiskAnalysis("testBenefitRiskAnalysis", indication, 
					metaAnalysisList, new DrugSet(parox), drugList, AnalysisType.LyndOBrien);	
		} catch(IllegalArgumentException a)
		{caught = true;}
		assertTrue(caught);
	}
}
