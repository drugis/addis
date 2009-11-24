package org.drugis.addis.entities.metaanalysis;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
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
	private Drug d_sertra;
	
	private Indication d_ind;
	private Endpoint d_ep;
	
	private Study d_bennie, d_boyer, d_fava, d_newhouse, d_sechter;
	
	RandomEffectsMetaAnalysis d_rema;
	private List<Study> d_studyList;

	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertra = new Drug("Sertraline","02");
		d_ep = new Endpoint("ep", Endpoint.Type.RATE);
		
		d_bennie = createStudy("Bennie 1995",63,144,73,142, d_ind);
		d_boyer = createStudy("Boyer 1998", 61,120, 63,122, d_ind);
		d_fava = createStudy("Fava 2002", 57, 92, 70, 96, d_ind);
		d_newhouse = createStudy("Newhouse 2000", 84,119, 85,117, d_ind);
		d_sechter = createStudy("Sechter 1999", 76,120, 86,118, d_ind);
		
		d_studyList = new ArrayList<Study>();
		d_studyList.add(d_bennie);
		d_studyList.add(d_boyer);
		d_studyList.add(d_fava);
		d_studyList.add(d_newhouse);
		d_studyList.add(d_sechter);
		d_rema = new RandomEffectsMetaAnalysis("meta", d_ep, d_studyList, d_fluox, d_sertra);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentIndicationsThrows() {
		Indication newInd = new Indication(666L, "bad");
		Study newStudy = createStudy("name", 0, 10, 0, 20, newInd);
		d_studyList.add(newStudy);
		d_rema = new RandomEffectsMetaAnalysis("meta", d_ep, d_studyList, d_fluox, d_sertra);
	}
	
	@Test
	public void testGetToString() {
		assertEquals(d_rema.getName(), d_rema.toString());
	}
	
	@Test
	public void testGetFirstDrug() {
		assertEquals(d_fluox, d_rema.getFirstDrug());
	}
	
	@Test
	public void testGetSecondDrug() {
		assertEquals(d_sertra, d_rema.getSecondDrug());
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
		assertEquals(d_ep, d_rema.getEndpoint());
	}
	
	@Test
	public void testGetIndication() {
		assertEquals(d_ind, d_rema.getIndication());
	}	
		
	@Test
	public void testGetRiskRatioRelativeEffect() {
		RelativeEffectMetaAnalysis<Measurement> riskRatio = d_rema.getRelativeEffect(RiskRatio.class);
		assertEquals(2.03, riskRatio.getHeterogeneity(), 0.01);
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
		
	private Study createStudy(String studyName, int fluoxResp, int fluoxSize, int sertraResp, int sertraSize, Indication ind)
	{
		BasicStudy s = new BasicStudy(studyName, ind);
		s.addEndpoint(d_ep);
		BasicPatientGroup g_fluox = new BasicPatientGroup(s, d_fluox, new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY),fluoxSize);
		BasicPatientGroup g_parox = new BasicPatientGroup(s, d_sertra, new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY),sertraSize);		
		
		s.addPatientGroup(g_parox);
		s.addPatientGroup(g_fluox);
		
		BasicRateMeasurement m_parox = (BasicRateMeasurement) d_ep.buildMeasurement(g_parox);
		BasicRateMeasurement m_fluox = (BasicRateMeasurement) d_ep.buildMeasurement(g_fluox);
		
		m_parox.setRate(sertraResp);
		m_fluox.setRate(fluoxResp);
		
		s.setMeasurement(d_ep, g_parox, m_parox);
		s.setMeasurement(d_ep, g_fluox, m_fluox);		
		
		return s;
	}
	
	@Test
	public void testGetDependencies() {
		HashSet<Entity> deps = new HashSet<Entity>();
		deps.add(d_fluox);
		deps.add(d_sertra);
		deps.add(d_ind);
		deps.add(d_ep);
		deps.addAll(Arrays.asList(new Study[]{d_bennie, d_boyer, d_fava, d_newhouse, d_sechter}));
		
		assertEquals(deps, d_rema.getDependencies());
	}
}
