package org.drugis.addis.entities.metaanalysis;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RelativeEffectMetaAnalysis;
import org.drugis.addis.entities.RiskRatio;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
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
		d_rateEndpoint = new Endpoint("rate", OutcomeMeasure.Type.RATE);
		d_contEndpoint = new Endpoint("continuous", OutcomeMeasure.Type.CONTINUOUS);
		
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentIndicationsThrows() {
		Indication newInd = new Indication(666L, "bad");
		Study newStudy = createRateStudy("name", 0, 10, 0, 20, newInd);
		d_studyList.add(newStudy);
		d_rema = new RandomEffectsMetaAnalysis("meta", d_rateEndpoint, d_studyList, d_fluox, d_sertr);
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
	public void testGetStudies() {
		assertEquals(d_studyList, d_rema.getStudies());
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
		RelativeEffectMetaAnalysis<Measurement> riskRatio = d_rema.getRelativeEffect(RiskRatio.class);
		assertEquals(2.03, riskRatio.getHeterogeneity(), 0.01);
		assertEquals(calculateI2(2.03,d_rema.getStudies().size()), riskRatio.getHeterogeneityI2(), 0.01);
		assertEquals(1.10, (riskRatio.getRelativeEffect()), 0.01); 
		assertEquals(1.01, (riskRatio.getConfidenceInterval().getLowerBound()), 0.01);
		assertEquals(1.20, (riskRatio.getConfidenceInterval().getUpperBound()), 0.01);		
	}
	
	@Test
	public void testGetOddsRatioRelativeEffect() {
		RelativeEffectMetaAnalysis<Measurement> oddsRatio = d_rema.getRelativeEffect(OddsRatio.class);
		assertEquals(2.14, oddsRatio.getHeterogeneity(), 0.01);
		assertEquals(1.30, (oddsRatio.getRelativeEffect()), 0.01); 
		assertEquals(1.03, (oddsRatio.getConfidenceInterval().getLowerBound()), 0.01);
		assertEquals(1.65, (oddsRatio.getConfidenceInterval().getUpperBound()), 0.01);		
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
		RelativeEffectMetaAnalysis<Measurement> relativeEffect = ma.getRelativeEffect(MeanDifference.class);
		assertEquals(2.5, relativeEffect.getRelativeEffect(), 0.01);
	}
		
	private Study createRateStudy(String studyName,
			int fluoxResp, int fluoxSize,
			int sertraResp, int sertraSize,
			Indication ind) {
		Study s = new Study(studyName, ind);
		s.addOutcomeMeasure(d_rateEndpoint);
		
		addRateMeasurement(s, d_fluox, fluoxSize, fluoxResp);		
		addRateMeasurement(s, d_sertr, sertraSize, sertraResp);
		
		return s;
	}
	
	private Study createContStudy(String studyName,
			int fluoxSize, double fluoxMean, double fluoxDev,
			int sertrSize, double sertrMean, double sertrDev,
			Indication ind) {
		Study s = new Study(studyName, ind);
		s.addOutcomeMeasure(d_contEndpoint);
		
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
