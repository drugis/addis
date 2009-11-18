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

import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;


public class LogRiskRatioTest {

	Drug d_fluox;
	Drug d_parox;
	
	Indication d_ind;
	Endpoint d_ep;
	
	Study d_choulinard;
	
	private LogRiskRatio d_ratio;

	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_parox = new Drug("Paroxetine","02");
		d_ep = new Endpoint("ep", Endpoint.Type.RATE);
		
		d_choulinard = createStudy("Choulinard et al (1999)",67,101,67,102);
		d_ratio = (LogRiskRatio) RelativeEffectFactory.buildRelativeEffect(d_choulinard, d_ep, d_fluox, d_parox, LogRiskRatio.class);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(Math.log(0.99), d_ratio.getRelativeEffect(), 0.01);
	}
	
	@Test
	public void testGetError() {
		double expected = Math.sqrt(1/67D + 1/67D - 1/101D - 1/102D);
		assertEquals(expected, d_ratio.getError(), 0.001);
	}
	
	@Test
	public void testGetConfidenceInterval() {
		Interval<Double> ival = d_ratio.getConfidenceInterval();
		assertEquals(Math.log(0.81), ival.getLowerBound(), 0.01);
		assertEquals(Math.log(1.21), ival.getUpperBound(), 0.01);
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
