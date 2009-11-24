/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.metaanalysis.RelativeEffectFactory;
import org.junit.Before;
import org.junit.Test;

public class LogRiskRatioTest {

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
	
	private LogRiskRatio d_ratioBennie, d_ratioBoyer, d_ratioFava, d_ratioNewhouse, d_ratioSechter;

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
				
		
		d_ratioBennie = (LogRiskRatio) RelativeEffectFactory.buildRelativeEffect(d_bennie, d_ep, d_fluox, d_sertra, LogRiskRatio.class);
		d_ratioBoyer = (LogRiskRatio) RelativeEffectFactory.buildRelativeEffect(d_boyer, d_ep, d_fluox, d_sertra, LogRiskRatio.class);
		d_ratioFava = (LogRiskRatio) RelativeEffectFactory.buildRelativeEffect(d_fava, d_ep, d_fluox, d_sertra, LogRiskRatio.class);
		d_ratioNewhouse = (LogRiskRatio) RelativeEffectFactory.buildRelativeEffect(d_newhouse, d_ep, d_fluox, d_sertra, LogRiskRatio.class);
		d_ratioSechter = (LogRiskRatio) RelativeEffectFactory.buildRelativeEffect(d_sechter, d_ep, d_fluox, d_sertra, LogRiskRatio.class);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(1.18, Math.exp(d_ratioBennie.getRelativeEffect()), 0.01);
		assertEquals(1.02, Math.exp(d_ratioBoyer.getRelativeEffect()), 0.01); 
		assertEquals(1.18, Math.exp(d_ratioFava.getRelativeEffect()), 0.01);
		assertEquals(1.03, Math.exp(d_ratioNewhouse.getRelativeEffect()), 0.01);
		assertEquals(1.15, Math.exp(d_ratioSechter.getRelativeEffect()), 0.01); 
	}
	
	@Test
	public void testGetError() {
		double expected = Math.sqrt(1/63D + 1/73D - 1/144D - 1/142D);
		assertEquals(expected, d_ratioBennie.getError(), 0.001);
	}
			
	private Study createStudy(String studyName, int fluoxResp, int fluoxSize, int sertraResp, int sertraSize) {
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
