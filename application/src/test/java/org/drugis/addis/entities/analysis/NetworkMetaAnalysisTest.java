/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.presentation.NetworkTableModelTest;
import org.drugis.common.JUnitUtil;
import org.drugis.mtc.BasicParameter;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NormalSummary;
import org.junit.Before;
import org.junit.Test;

public class NetworkMetaAnalysisTest {
	private NetworkMetaAnalysis d_analysis;
	private NetworkMetaAnalysis d_mockAnalysis;

	@Before
	public void setup() throws InterruptedException{
		d_analysis = ExampleData.buildNetworkMetaAnalysisHamD();
		d_mockAnalysis = NetworkTableModelTest.buildMockNetworkMetaAnalysis();
		d_mockAnalysis.run();
		while (!d_mockAnalysis.getConsistencyModel().isReady()) {
			Thread.sleep(10);
		}
	}
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(d_analysis, MetaAnalysis.PROPERTY_NAME, d_analysis.getName(), "TEST");
	}
	
	@Test
	public void testGetType() {
		assertEquals("Markov Chain Monte Carlo Network Meta-Analysis", d_analysis.getType());
	}
	
	@Test
	public void testRelativeEffectsSummary() {
		DrugSet fluox = new DrugSet(ExampleData.buildDrugFluoxetine());
		DrugSet parox = new DrugSet(ExampleData.buildDrugParoxetine());
		DrugSet sertr = new DrugSet(ExampleData.buildDrugSertraline());
		Parameter[] expected = new Parameter[] {
				d_analysis.getConsistencyModel().getRelativeEffect(d_analysis.getTreatment(fluox), d_analysis.getTreatment(parox)),
				d_analysis.getConsistencyModel().getRelativeEffect(d_analysis.getTreatment(fluox), d_analysis.getTreatment(sertr))
		};
		assertArrayEquals(expected, d_analysis.getRelativeEffectsSummary().getParameters());
	}
	
	@Test
	public void testGetRelativeEffect() {
		Drug base = ExampleData.buildDrugFluoxetine();
		Drug subj = ExampleData.buildDrugParoxetine();
		RelativeEffect<?> actual = d_mockAnalysis.getRelativeEffect(new DrugSet(base), new DrugSet(subj), BasicOddsRatio.class);
		NormalSummary summary = d_mockAnalysis.getNormalSummary(d_mockAnalysis.getConsistencyModel(), 
				new BasicParameter(d_mockAnalysis.getTreatment(new DrugSet(base)), d_mockAnalysis.getTreatment(new DrugSet(subj))));
		RelativeEffect<?> expected = NetworkRelativeEffect.buildOddsRatio(summary.getMean(), summary.getStandardDeviation());
		assertNotNull(expected);
		assertNotNull(actual);
		assertEquals(expected.getConfidenceInterval().getPointEstimate(), actual.getConfidenceInterval().getPointEstimate());
		assertEquals(expected.getConfidenceInterval(), actual.getConfidenceInterval());
		assertEquals(expected.getAxisType(), actual.getAxisType());
	}
	
	@Test
	public void testIsContinuous() {
		assertFalse(NetworkTableModelTest.buildMockNetworkMetaAnalysis().isContinuous());
		assertTrue(NetworkTableModelTest.buildMockContinuousNetworkMetaAnalysis().isContinuous());
	}
	
	@Test
	public void testTransformCombinationTreatment() {
		Study study = ExampleData.buildStudyMcMurray().clone();
		DrugTreatment ta1 = new DrugTreatment(ExampleData.buildDrugCandesartan(), null);
		DrugTreatment ta2 = new DrugTreatment(ExampleData.buildDrugFluoxetine(), null);
		StudyActivity activity = new StudyActivity("DRUGS", new TreatmentActivity(Arrays.asList(ta1, ta2)));
		study.getStudyActivities().add(activity);
		study.setStudyActivityAt(study.getArms().get(0), study.findTreatmentEpoch(), activity);
		
		Map<Study, Map<DrugSet, Arm>> armMap = new HashMap<Study, Map<DrugSet, Arm>>();
		Map<DrugSet, Arm> drugArmMap = new HashMap<DrugSet, Arm>();
		for (Arm a : study.getArms()) {
			drugArmMap.put(study.getDrugs(a), a);
		}
		armMap.put(study, drugArmMap);
		NetworkMetaAnalysis nma = new NetworkMetaAnalysis("don'tcare", study.getIndication(), study.getOutcomeMeasures().get(0), armMap);
		
		assertEquals("Candesartan_Fluoxetine", nma.getTreatment(new DrugSet(Arrays.asList(ExampleData.buildDrugCandesartan(), ExampleData.buildDrugFluoxetine()))).id());
	}
	
	@Test
	public void testTransformTreatmentWithIllegalCharacters() {
		Study study = ExampleData.buildStudyMcMurray().clone();
		Drug myDrug = new Drug("My Drug!", "3");
		DrugTreatment ta1 = new DrugTreatment(myDrug, null);
		DrugTreatment ta2 = new DrugTreatment(ExampleData.buildDrugFluoxetine(), null);
		StudyActivity activity = new StudyActivity("DRUGS", new TreatmentActivity(Arrays.asList(ta1, ta2)));
		study.getStudyActivities().add(activity);
		study.setStudyActivityAt(study.getArms().get(0), study.findTreatmentEpoch(), activity);
		
		Map<Study, Map<DrugSet, Arm>> armMap = new HashMap<Study, Map<DrugSet, Arm>>();
		Map<DrugSet, Arm> drugArmMap = new HashMap<DrugSet, Arm>();
		for (Arm a : study.getArms()) {
			drugArmMap.put(study.getDrugs(a), a);
		}
		armMap.put(study, drugArmMap);
		NetworkMetaAnalysis nma = new NetworkMetaAnalysis("don'tcare", study.getIndication(), study.getOutcomeMeasures().get(0), armMap);
		
		assertEquals("Fluoxetine_MyDrug", nma.getTreatment(new DrugSet(Arrays.asList(myDrug, ExampleData.buildDrugFluoxetine()))).id());
	}
	
	@Test
	public void testTransformTreatmentDuplicateCleanName() {
		Study study = ExampleData.buildStudyMcMurray().clone();
		Drug myDrug1 = new Drug("My Drug!", "3");
		Drug myDrug2 = new Drug("My!Drug", "4");
		DrugTreatment ta1 = new DrugTreatment(myDrug1, null);
		DrugTreatment ta2 = new DrugTreatment(myDrug2, null);
		StudyActivity act1 = new StudyActivity("DRUGS1", new TreatmentActivity(Arrays.asList(ta1)));
		StudyActivity act2 = new StudyActivity("DRUGS2", new TreatmentActivity(Arrays.asList(ta2)));
		study.getStudyActivities().add(act1);
		study.getStudyActivities().add(act2);
		study.setStudyActivityAt(study.getArms().get(0), study.findTreatmentEpoch(), act1);
		study.setStudyActivityAt(study.getArms().get(1), study.findTreatmentEpoch(), act2);
		
		Map<Study, Map<DrugSet, Arm>> armMap = new HashMap<Study, Map<DrugSet, Arm>>();
		Map<DrugSet, Arm> drugArmMap = new HashMap<DrugSet, Arm>();
		for (Arm a : study.getArms()) {
			drugArmMap.put(study.getDrugs(a), a);
		}
		armMap.put(study, drugArmMap);
		NetworkMetaAnalysis nma = new NetworkMetaAnalysis("don'tcare", study.getIndication(), study.getOutcomeMeasures().get(0), armMap);
		
		assertEquals("MyDrug", nma.getTreatment(new DrugSet(Arrays.asList(myDrug1))).id());
		assertEquals("MyDrug2", nma.getTreatment(new DrugSet(Arrays.asList(myDrug2))).id());
	}
}