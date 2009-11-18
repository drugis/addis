package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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

	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertra = new Drug("Sertraline","02");
		d_ep = new Endpoint("ep", Endpoint.Type.RATE);
		
		d_bennie = createStudy("Bennie 1995",63,144,73,142);
		d_boyer = createStudy("Boyer 1998", 61,120, 63,122);
		d_fava = createStudy("Fava 2002", 57, 92, 70, 96);
		d_newhouse = createStudy("Newhouse 2000", 84,119, 85,117);
		d_sechter = createStudy("Sechter 1999", 76,120, 86,118);
		
		List<Study> studyList = new ArrayList<Study>();
		studyList.add(d_bennie);
		studyList.add(d_boyer);
		studyList.add(d_fava);
		studyList.add(d_newhouse);
		studyList.add(d_sechter);
		d_rema = new RandomEffectsMetaAnalysis(studyList, d_ep, d_fluox, d_sertra);
	}
		
	@Test
	public void testGetRiskRatio() {
		RelativeEffectRate riskRatio = d_rema.getRiskRatio();
		assertEquals(1.10, Math.exp(riskRatio.getRelativeEffect()), 0.01); 
		assertEquals(1.01, Math.exp(riskRatio.getConfidenceInterval().getLowerBound()), 0.01);
		assertEquals(1.20, Math.exp(riskRatio.getConfidenceInterval().getUpperBound()), 0.01);		
	}
	
	private Study createStudy(String studyName, int fluoxResp, int fluoxSize, int sertraResp, int sertraSize)
	{
		BasicStudy s = new BasicStudy(studyName, d_ind);
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
	
}
