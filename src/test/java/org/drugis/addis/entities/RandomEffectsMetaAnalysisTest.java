package org.drugis.addis.entities;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RandomEffectsMetaAnalysisTest {
	Drug d_fluox;
	Drug d_parox;
	
	Indication d_ind;
	Endpoint d_ep;
	
	Study d_choulinard, d_dewilde, d_fava98, d_fava02, d_gagiano, d_schone;
	private RandomEffectsMetaAnalysis d_ma;
	
	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_parox = new Drug("Paroxetine","02");
		d_ep = new Endpoint("ep", Endpoint.Type.RATE);
		
		d_choulinard = createStudy("Choulinard et al (1999)",67,101,67,102);
		d_dewilde = createStudy("De Wilde", 26, 41, 23, 37);
		d_fava98 = createStudy("Fava98", 29, 54, 32, 55);
		d_fava02 = createStudy("Fava02", 57, 92, 67, 96);
		d_gagiano = createStudy("Gagiano", 30, 45, 27, 45);
		d_schone = createStudy("Schone", 9, 52, 20, 54);
		
		List<Study> studyList = new ArrayList<Study>();
		studyList.add(d_choulinard);
		studyList.add(d_dewilde);
		studyList.add(d_fava98);
		studyList.add(d_fava02);
		studyList.add(d_gagiano);
		studyList.add(d_schone);
		d_ma = new RandomEffectsMetaAnalysis(d_ep, studyList, d_fluox, d_parox);
	}
		
	@Ignore
	@Test
	public void testGetRiskRatio() {
		RelativeEffectRate riskRatio = d_ma.getRiskRatio();
		assertEquals(1.09, riskRatio.getRelativeEffect(), 0.01);
		assertEquals(0.97, riskRatio.getConfidenceInterval().getLowerBound(), 0.01);
		assertEquals(1.21, riskRatio.getConfidenceInterval().getUpperBound(), 0.01);		
	}
	
	private Study createStudy(String studyName, int fluoxResp, int fluoxSize, int paroxResp, int paroxSize)
	{
		BasicStudy s = new BasicStudy(studyName, d_ind);
		s.addEndpoint(d_ep);
		BasicPatientGroup g_fluox = new BasicPatientGroup(s, d_fluox, new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY),fluoxSize);
		BasicPatientGroup g_parox = new BasicPatientGroup(s, d_parox, new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY),paroxSize);		
		
		s.addPatientGroup(g_parox);
		s.addPatientGroup(g_fluox);
		
		BasicRateMeasurement m_parox = (BasicRateMeasurement) d_ep.buildMeasurement(g_parox);
		BasicRateMeasurement m_fluox = (BasicRateMeasurement) d_ep.buildMeasurement(g_fluox);
		
		m_parox.setRate(paroxResp);
		m_fluox.setRate(fluoxResp);
		
		s.setMeasurement(d_ep, g_parox, m_parox);
		s.setMeasurement(d_ep, g_fluox, m_fluox);		
		
		return s;
	}
	
}
