/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.drugis.addis.entities.relativeeffect.Beta;
import org.drugis.addis.entities.relativeeffect.TransformedStudentT;
import org.drugis.addis.entities.relativeeffect.TransformedStudentTBase;
import org.junit.Before;
import org.junit.Test;

public class StudyBenefitRiskAnalysisTest {
	private static final String NAME = "Je Moeder";
	private StudyBenefitRiskAnalysis d_analysis;
	private Arm d_baseline;
	private Arm d_subject;

	@Before
	public void setUp() {
		Indication indication = ExampleData.buildIndicationDepression();
		Study study = ExampleData.buildStudyChouinard();
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		criteria.add(ExampleData.buildEndpointCgi());
		List<Arm> alternatives = study.getArms();
		d_analysis = new StudyBenefitRiskAnalysis(NAME, indication, study, criteria, alternatives, AnalysisType.SMAA);
		d_baseline = alternatives.get(0);
		d_subject = alternatives.get(1);
	}
	
	@Test
	public void testInitialization() {
		assertEquals(NAME, d_analysis.getName());
		assertEquals(ExampleData.buildStudyChouinard(), d_analysis.getStudy());
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointCgi());
		criteria.add(ExampleData.buildEndpointHamd());
		assertEquals(criteria, d_analysis.getCriteria());
		assertEquals(ExampleData.buildStudyChouinard().getArms(), d_analysis.getAlternatives());
		assertEquals(d_analysis.getAlternatives().get(0), d_analysis.getBaseline());
		
		StudyBenefitRiskAnalysis analysis = buildOtherBaseline();
		assertEquals(d_analysis.getAlternatives().get(1), analysis.getBaseline());
	}

	private StudyBenefitRiskAnalysis buildOtherBaseline() {
		return new StudyBenefitRiskAnalysis(d_analysis.getName(), d_analysis.getIndication(), d_analysis.getStudy(), 
				d_analysis.getCriteria(), d_analysis.getAlternatives().get(1), d_analysis.getAlternatives(), d_analysis.getAnalysisType());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadInitialization() {
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		criteria.add(ExampleData.buildAdverseEventSexualDysfunction());
		new StudyBenefitRiskAnalysis("Test SBA", ExampleData.buildIndicationDepression(), 
				ExampleData.buildStudyFava2002(), criteria, ExampleData.buildStudyFava2002().getArms(), AnalysisType.SMAA);	
	}
	
	@Test
	public void testDependencies() {
		Set<Entity> expected = new HashSet<Entity>();
		expected.add(d_analysis.getStudy());
		expected.addAll(d_analysis.getCriteria());
		expected.add(d_analysis.getIndication());
		expected.addAll(d_analysis.getStudy().getDependencies());
		assertEquals(expected , d_analysis.getDependencies());
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(d_analysis.compareTo(null) > 0);
		assertEquals(0, d_analysis.compareTo(d_analysis));
		MetaBenefitRiskAnalysis otherBRAnalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		assertTrue(d_analysis.compareTo(otherBRAnalysis) < 0);
		assertTrue(otherBRAnalysis.compareTo(d_analysis) > 0);
	}
	
	@Test
	public void testDeepEquals() {
		assertFalse(d_analysis.deepEquals(buildOtherBaseline()));
	}
	
	@Test
	public void testGetMeasurementForRateOutcome() {
		Study study = d_analysis.getStudy();
		Arm arm = study.getArms().get(0);
		Endpoint endpoint = ExampleData.buildEndpointHamd();
		RateMeasurement measurement = (RateMeasurement) study.getMeasurement(endpoint, arm);
		Beta expected = new Beta(1 + measurement.getRate(), 1 + measurement.getSampleSize() - measurement.getRate());
		assertEquals(expected, d_analysis.getMeasurement(endpoint, arm));
	}

	@Test
	public void testGetMeasurementForContinuousOutcome() {
		Indication indication = ExampleData.buildIndicationDepression();
		Study study = ExampleData.buildStudyChouinard();
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		Endpoint endpoint = ExampleData.buildEndpointCgi();
		criteria.add(endpoint);
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		List<Arm> alternatives = study.getArms();
		d_analysis = new StudyBenefitRiskAnalysis(NAME, indication, study, criteria, alternatives, AnalysisType.SMAA);
		
		Arm arm = study.getArms().get(1);
		ContinuousMeasurement measurement = (ContinuousMeasurement) study.getMeasurement(endpoint, arm);
		TransformedStudentTBase expected = new TransformedStudentT(measurement.getMean(), measurement.getStdDev(),
				measurement.getSampleSize() - 1);
		assertEquals(expected, d_analysis.getMeasurement(endpoint, arm));
	}
	
	@Test
	public void testLyndOBrienException() throws IllegalArgumentException {
		Indication indication = ExampleData.buildIndicationDepression();
		Study study = ExampleData.buildStudyChouinard();
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		List<Arm> alternatives = new ArrayList<Arm>();
		alternatives.add(study.getArms().get(0));
		boolean caught = false;
		try {
			new StudyBenefitRiskAnalysis(NAME, indication, study, criteria, alternatives, AnalysisType.LyndOBrien);
		} catch(IllegalArgumentException e)
		{ caught = true;}
		assertTrue(caught);
	}
	
	
	@Test
	public void testRelativeEffectDistribution() {
		OutcomeMeasure v = d_analysis.getCriteria().get(1);
		BasicOddsRatio ratio = new BasicOddsRatio((RateMeasurement) d_analysis.getStudy().getMeasurement(v, d_baseline), 
				(RateMeasurement) d_analysis.getStudy().getMeasurement(v, d_subject));
		assertEquals(ratio.getDistribution(), d_analysis.getRelativeEffectDistribution(v, d_subject));
		
		OutcomeMeasure v2 = d_analysis.getCriteria().get(0);
		BasicStandardisedMeanDifference diff = new BasicStandardisedMeanDifference(
				(ContinuousMeasurement) d_analysis.getStudy().getMeasurement(v2, d_baseline), 
				(ContinuousMeasurement) d_analysis.getStudy().getMeasurement(v2, d_subject));
		assertEquals(diff.getDistribution(), d_analysis.getRelativeEffectDistribution(v2, d_subject));
	}
	
	@Test
	public void testCorrectedRelativeEffectDistribution() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		((BasicRateMeasurement) d_analysis.getStudy().getMeasurement(om, d_baseline)).setRate(0);
		
		BasicOddsRatio ratio = new BasicOddsRatio((RateMeasurement) d_analysis.getStudy().getMeasurement(om, d_baseline), 
				(RateMeasurement) d_analysis.getStudy().getMeasurement(om, d_subject));
		assertEquals(ratio.getCorrected().getDistribution(), d_analysis.getRelativeEffectDistribution(om, d_subject));
	}
	
	@Test
	public void testCorrectedRelativeEffectDistribution2() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		BasicRateMeasurement m = ((BasicRateMeasurement) d_analysis.getStudy().getMeasurement(om, d_subject));
		m.setRate(m.getSampleSize());
		
		BasicOddsRatio ratio = new BasicOddsRatio((RateMeasurement) d_analysis.getStudy().getMeasurement(om, d_baseline), 
				(RateMeasurement) d_analysis.getStudy().getMeasurement(om, d_subject));
		assertEquals(ratio.getCorrected().getDistribution(), d_analysis.getRelativeEffectDistribution(om, d_subject));
	}
	
	@Test
	public void testNullIfRelativeEffectDistributionUncorrectable() {
		Indication indication = ExampleData.buildIndicationDepression();
		Study study = ExampleData.buildStudyFava2002().clone();
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		AdverseEvent om = ExampleData.buildAdverseEventConvulsion();
		criteria.add(om);
		List<Arm> alternatives = study.getArms();
		
		for (Arm a : alternatives) {
			study.setMeasurement(study.findStudyOutcomeMeasure(om), a, new BasicRateMeasurement(0, 100));
		}
		((BasicRateMeasurement) study.getMeasurement(om, alternatives.get(0))).setRate(5);
		
		StudyBenefitRiskAnalysis analysis = new StudyBenefitRiskAnalysis(NAME, indication, study, criteria, alternatives, AnalysisType.SMAA);

		assertNotNull(analysis.getRelativeEffectDistribution(om, alternatives.get(1)));
		assertNotNull(analysis.getRelativeEffectDistribution(om, alternatives.get(2)));

		StudyBenefitRiskAnalysis analysis2 = new StudyBenefitRiskAnalysis(NAME, indication, study, criteria, alternatives.get(1), alternatives, AnalysisType.SMAA);
		assertNull(analysis2.getRelativeEffectDistribution(om, alternatives.get(2)));
	}
}
