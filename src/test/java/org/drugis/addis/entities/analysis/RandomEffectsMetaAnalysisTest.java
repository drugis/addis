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

package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.RandomEffectMetaAnalysisRelativeEffect;
import org.drugis.addis.entities.relativeeffect.BasicRiskRatio;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class RandomEffectsMetaAnalysisTest {
	/* 
	 * Test data from Figure 2 in "Efficacy and Safety of Second-Generation 
	 * Antidepressants in the Treatment of Major Depressive Disorder" 
	 * by Hansen et al. 2005
	 * 
	 */
	
	private Drug d_fluox;
	private Drug d_sertr;
	
	private Indication d_ind;
	private Endpoint d_rateEndpoint;
	private Endpoint d_contEndpoint;
	
	private Study d_bennie, d_boyer, d_fava, d_newhouse, d_sechter;
	
	RandomEffectsMetaAnalysis d_rema;
	private List<Study> d_studyList;

	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertr = new Drug("Sertraline","02");
		d_rateEndpoint = new Endpoint("rate", Variable.Type.RATE);
		d_contEndpoint = new Endpoint("continuous", Variable.Type.CONTINUOUS);
		
		d_bennie = createRateStudy("Bennie 1995",63,144,73,142, d_ind);
		d_boyer = createRateStudy("Boyer 1998", 61,120, 63,122, d_ind);
		d_fava = createRateStudy("Fava 2002", 57, 92, 70, 96, d_ind);
		d_newhouse = createRateStudy("Newhouse 2000", 84,119, 85,117, d_ind);
		d_sechter = createRateStudy("Sechter 1999", 76,120, 86,118, d_ind);
		
		d_studyList = new ArrayList<Study>();
		d_studyList.add(d_bennie);
		d_studyList.add(d_boyer);
		d_studyList.add(d_fava);
		d_studyList.add(d_newhouse);
		d_studyList.add(d_sechter);
		d_rema = new RandomEffectsMetaAnalysis("meta", d_rateEndpoint, d_studyList, d_fluox, d_sertr);
	}
	
	@Test
	public void testGetStudyArms() {
		List<StudyArmsEntry> entries = d_rema.getStudyArms();
		assertEquals(d_studyList.size(), entries.size());
		for (int i = 0; i < d_studyList.size(); ++i) {
			assertEquals(d_studyList.get(i), entries.get(i).getStudy());
			assertEquals(d_fluox, entries.get(i).getBase().getDrug());
			assertEquals(d_sertr, entries.get(i).getSubject().getDrug());
			assertTrue(d_studyList.get(i).getArms().contains(entries.get(i).getBase()));
			assertTrue(d_studyList.get(i).getArms().contains(entries.get(i).getSubject()));
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentIndicationsThrows() {
		Indication newInd = new Indication(666L, "bad");
		Study newStudy = createRateStudy("name", 0, 10, 0, 20, newInd);
		d_studyList.add(newStudy);
		d_rema = new RandomEffectsMetaAnalysis("meta", d_rateEndpoint, d_studyList, d_fluox, d_sertr);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentDrugs() {
		Indication newInd = new Indication(666L, "bad");
		Study newStudy = createRateStudy("name", 0, 10, 0, 20, newInd);
		
		List<StudyArmsEntry> armsList = new ArrayList<StudyArmsEntry>();
		armsList.add(new StudyArmsEntry(newStudy,newStudy.getArms().get(0),newStudy.getArms().get(1)));
		armsList.add(new StudyArmsEntry(newStudy,newStudy.getArms().get(1),newStudy.getArms().get(0)));
		
		d_rema = new RandomEffectsMetaAnalysis("meta", d_rateEndpoint, armsList);
	}	
	
	
	@Test
	public void testGetToString() {
		assertEquals(d_rema.getName(), d_rema.toString());
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(144+142+120+122+92+96+119+117+120+118, d_rema.getSampleSize());
	}
	
	@Test
	public void testGetFirstDrug() {
		assertEquals(d_fluox, d_rema.getFirstDrug());
	}
	
	@Test
	public void testGetSecondDrug() {
		assertEquals(d_sertr, d_rema.getSecondDrug());
	}
	
	@Test
	public void testIncludedDrugs() {
		assertEquals(Arrays.asList(new Drug [] {d_fluox, d_sertr}), d_rema.getIncludedDrugs());
	}
	
	@Test
	public void testGetStudies() {
		assertEquals(d_studyList, d_rema.getIncludedStudies());
	}
	
	@Test
	public void testGetName() {
		JUnitUtil.testSetter(d_rema, RandomEffectsMetaAnalysis.PROPERTY_NAME, "meta", "newname");
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(d_rateEndpoint, d_rema.getOutcomeMeasure());
	}
	
	@Test
	public void testGetIndication() {
		assertEquals(d_ind, d_rema.getIndication());
	}	
	
	public double calculateI2(double hetr, int k) {
		return Math.max(0, 100 * ((hetr-(k-1)) / hetr ));
	}
	
	@Test
	public void testGetRiskRatioRelativeEffect() {
		RandomEffectMetaAnalysisRelativeEffect<Measurement> riskRatio = d_rema.getRelativeEffect(BasicRiskRatio.class);
		assertEquals(2.03, riskRatio.getHeterogeneity(), 0.01);
		assertEquals(calculateI2(2.03,d_rema.getIncludedStudies().size()), riskRatio.getHeterogeneityI2(), 0.01);
		assertEquals(1.10, (riskRatio.getConfidenceInterval().getPointEstimate()), 0.01); 
		assertEquals(1.01, (riskRatio.getConfidenceInterval().getLowerBound()), 0.01);
		assertEquals(1.20, (riskRatio.getConfidenceInterval().getUpperBound()), 0.01);		
	}
	
	@Test
	public void testGetOddsRatioRelativeEffect() {
		RandomEffectMetaAnalysisRelativeEffect<Measurement> oddsRatio = d_rema.getRelativeEffect(BasicOddsRatio.class);
		assertEquals(2.14, oddsRatio.getHeterogeneity(), 0.01);
		assertEquals(1.30, (oddsRatio.getConfidenceInterval().getPointEstimate()), 0.01); 
		assertEquals(1.03, (oddsRatio.getConfidenceInterval().getLowerBound()), 0.01);
		assertEquals(1.65, (oddsRatio.getConfidenceInterval().getUpperBound()), 0.01);		
	}
	
	@Test
	public void testGetRelativeEffect() {
		RandomEffectMetaAnalysisRelativeEffect<Measurement> approach1 = 
			d_rema.getRelativeEffect(BasicOddsRatio.class);
		RandomEffectMetaAnalysisRelativeEffect<Measurement> approach2 = 
			d_rema.getRelativeEffect(d_rema.getFirstDrug(), d_rema.getSecondDrug(), BasicOddsRatio.class);

		assertEquals(approach1.getConfidenceInterval().getPointEstimate(), approach2.getConfidenceInterval().getPointEstimate());
		assertEquals(approach1.getConfidenceInterval(), approach2.getConfidenceInterval());
		assertEquals(approach1.getSampleSize(), approach2.getSampleSize());
		assertEquals(approach1.getAxisType(), approach2.getAxisType());
	}
	
	@Test
	public void testContinuousMetaAnalysis() {
		Study s1 = createContStudy("s1", 50, 4, 2, 50, 6, 2, d_ind);
		Study s2 = createContStudy("s2", 50, 4, 2, 50, 7, 2, d_ind);
		List<Study> studies = new ArrayList<Study>();
		studies.add(s1);
		studies.add(s2);
		
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta",
				d_contEndpoint, studies, d_fluox, d_sertr);
		RandomEffectMetaAnalysisRelativeEffect<Measurement> relativeEffect = ma.getRelativeEffect(BasicMeanDifference.class);
		assertEquals(2.5, relativeEffect.getConfidenceInterval().getPointEstimate(), 0.01);
	}
		
	private Study createRateStudy(String studyName,
			int fluoxResp, int fluoxSize,
			int sertraResp, int sertraSize,
			Indication ind) {
		Study s = new Study(studyName, ind);
		s.addEndpoint(d_rateEndpoint);
		
		addRateMeasurement(s, d_fluox, fluoxSize, fluoxResp);		
		addRateMeasurement(s, d_sertr, sertraSize, sertraResp);
		
		return s;
	}
	
	private Study createContStudy(String studyName,
			int fluoxSize, double fluoxMean, double fluoxDev,
			int sertrSize, double sertrMean, double sertrDev,
			Indication ind) {
		Study s = new Study(studyName, ind);
		s.addEndpoint(d_contEndpoint);
		
		addContinuousMeasurement(s, d_fluox, fluoxSize, fluoxMean, fluoxDev);
		addContinuousMeasurement(s, d_sertr, sertrSize, sertrMean, sertrDev);
		
		return s ;
	}

	private void addRateMeasurement(Study study, Drug drug, int nSubjects, int nResponders) {
		Arm group = addArm(study, drug, nSubjects);
		BasicRateMeasurement measurement = (BasicRateMeasurement) d_rateEndpoint.buildMeasurement(group);
		measurement.setRate(nResponders);
		study.setMeasurement(d_rateEndpoint, group, measurement);
	}

	private void addContinuousMeasurement(Study study, Drug drug,
			int nSubjects, double mean, double stdDev) {
		Arm group = addArm(study, drug, nSubjects);
		BasicContinuousMeasurement measurement =
			(BasicContinuousMeasurement) d_contEndpoint.buildMeasurement(group);
		measurement.setMean(mean);
		measurement.setStdDev(stdDev);
		study.setMeasurement(d_contEndpoint, group, measurement);
	}
	
	private Arm addArm(Study study, Drug drug, int nSubjects) {
		FixedDose dose = new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY);
		Arm group = new Arm(drug, dose, nSubjects);
		study.addArm(group);
		return group;
	}
	
	@Test
	public void testGetDependencies() {
		HashSet<Entity> deps = new HashSet<Entity>();
		deps.add(d_fluox);
		deps.add(d_sertr);
		deps.add(d_ind);
		deps.add(d_rateEndpoint);
		deps.addAll(Arrays.asList(new Study[]{d_bennie, d_boyer, d_fava, d_newhouse, d_sechter}));
		
		assertEquals(deps, d_rema.getDependencies());
	}
}
