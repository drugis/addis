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

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.util.XMLHelper;
import org.junit.Before;
import org.junit.Test;



public class MetaBenefitRiskAnalysisTest {
	private MetaBenefitRiskAnalysis d_BRAnalysis;

	@Before
	public void setup(){
		d_BRAnalysis = ExampleData.buildBenefitRiskAnalysis();
	}

	@Test
	public void testEquals(){
		assertFalse(d_BRAnalysis.equals("nope, no meta Analysis"));
		MetaBenefitRiskAnalysis otherBRAnalysis = ExampleData.buildBenefitRiskAnalysis();
		assertTrue(d_BRAnalysis.equals(otherBRAnalysis));
		otherBRAnalysis.setName("some new name");
		assertFalse(d_BRAnalysis.equals(otherBRAnalysis));
		assertTrue(d_BRAnalysis.equals(d_BRAnalysis));
	}
	
	@Test 
	public void testCompareTo(){
		assertTrue(d_BRAnalysis.compareTo(null) > 0);
		assertEquals(0, d_BRAnalysis.compareTo(d_BRAnalysis));
		MetaBenefitRiskAnalysis otherBRAnalysis = ExampleData.buildBenefitRiskAnalysis();
		assertEquals(0, d_BRAnalysis.compareTo(otherBRAnalysis));
		otherBRAnalysis.setName("some new name");
		assertTrue(d_BRAnalysis.compareTo(otherBRAnalysis) > 0);
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
	
	@Test
	public void testXML() throws XMLStreamException {
		String xml = XMLHelper.toXml(d_BRAnalysis, MetaBenefitRiskAnalysis.class);
		MetaBenefitRiskAnalysis importedAnalysis = (MetaBenefitRiskAnalysis)XMLHelper.fromXml(xml);
		assertEntityEquals(d_BRAnalysis, importedAnalysis);
	}
	
	@Test
	public void testLegacyXML() throws XMLStreamException {
		InputStream xmlStream = getClass().getResourceAsStream("legacyBR.xml");
		assertNotNull(xmlStream);
		MetaBenefitRiskAnalysis importedAnalysis = 
			(MetaBenefitRiskAnalysis)XMLHelper.fromXml(xmlStream);
		assertEntityEquals(d_BRAnalysis, importedAnalysis);
	}
}
