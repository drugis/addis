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
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class OddsRatioTest {
	private Drug d_fluox;
	private Drug d_sertra;
	
	private Indication d_ind;
	private Endpoint d_ep;
	
	private Study d_bennie, d_boyer, d_fava, d_newhouse, d_sechter;
	
	private OddsRatio d_ratioBennie, d_ratioBoyer, d_ratioFava, d_ratioNewhouse, d_ratioSechter;

	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertra = new Drug("Sertraline","02");
		d_ep = new Endpoint("ep", AbstractOutcomeMeasure.Type.RATE);
		
		d_bennie = createStudy("Bennie 1995",63,144,73,142);
		d_boyer = createStudy("Boyer 1998", 61,120, 63,122);
		d_fava = createStudy("Fava 2002", 57, 92, 70, 96);
		d_newhouse = createStudy("Newhouse 2000", 84,119, 85,117);
		d_sechter = createStudy("Sechter 1999", 76,120, 86,118);
				
		
		d_ratioBennie = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_bennie, d_ep, d_fluox, d_sertra, OddsRatio.class);
		d_ratioBoyer = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_boyer, d_ep, d_fluox, d_sertra, OddsRatio.class);
		d_ratioFava = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_fava, d_ep, d_fluox, d_sertra, OddsRatio.class);
		d_ratioNewhouse = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_newhouse, d_ep, d_fluox, d_sertra, OddsRatio.class);
		d_ratioSechter = (OddsRatio) RelativeEffectFactory.buildRelativeEffect(d_sechter, d_ep, d_fluox, d_sertra, OddsRatio.class);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(1.36, (d_ratioBennie.getRelativeEffect()), 0.01);
		assertEquals(1.03, (d_ratioBoyer.getRelativeEffect()), 0.01); 
		assertEquals(1.65, (d_ratioFava.getRelativeEffect()), 0.01);
		assertEquals(1.11, (d_ratioNewhouse.getRelativeEffect()), 0.01);
		assertEquals(1.56, (d_ratioSechter.getRelativeEffect()), 0.01); 
	}
	
	@Test
	public void testGetError() {
		double expected = Math.sqrt(1/63D + 1/73D + 1/(144D-63D) + 1/(142D-73D));
		assertEquals(expected, d_ratioBennie.getError(), 0.001);
	}
	
	@Test
	public void testGetConfidenceIntervalBennie() {
		Interval<Double> ival = d_ratioBennie.getConfidenceInterval();
		assertEquals(0.85, (ival.getLowerBound()), 0.01);
		assertEquals(2.17, (ival.getUpperBound()), 0.01);
	}
	
	@Test
	public void testGetConfidenceIntervalBoyer() {
		Interval<Double> ival = d_ratioBoyer.getConfidenceInterval();
		assertEquals(0.62, (ival.getLowerBound()), 0.01); 
		assertEquals(1.71, (ival.getUpperBound()), 0.01); 
	}
	
	@Test
	public void testGetConfidenceIntervalFava() {
		Interval<Double> ival = d_ratioFava.getConfidenceInterval();
		assertEquals(0.89, (ival.getLowerBound()), 0.01); 
		assertEquals(3.06, (ival.getUpperBound()), 0.015); 
	}
	
	@Test
	public void testGetConfidenceIntervalNewhouse() {
		Interval<Double> ival = d_ratioNewhouse.getConfidenceInterval();
		assertEquals(0.63, (ival.getLowerBound()), 0.01); 
		assertEquals(1.95, (ival.getUpperBound()), 0.01); 
	}
	
	@Test
	public void testGetConfidenceIntervalSechter() {
		Interval<Double> ival = d_ratioSechter.getConfidenceInterval();
		assertEquals(0.90, (ival.getLowerBound()), 0.01); 
		assertEquals(2.70, (ival.getUpperBound()), 0.01); 
	}
		
	private Study createStudy(String studyName, int fluoxResp, int fluoxSize, int sertraResp, int sertraSize)
	{
		Study s = new Study(studyName, d_ind);
		s.addOutcomeMeasure(d_ep);
		BasicArm g_fluox = new BasicArm(d_fluox, new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY), fluoxSize);
		BasicArm g_parox = new BasicArm(d_sertra, new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY), sertraSize);		
		
		s.addArm(g_parox);
		s.addArm(g_fluox);
		
		BasicRateMeasurement m_parox = (BasicRateMeasurement) d_ep.buildMeasurement(g_parox);
		BasicRateMeasurement m_fluox = (BasicRateMeasurement) d_ep.buildMeasurement(g_fluox);
		
		m_parox.setRate(sertraResp);
		m_fluox.setRate(fluoxResp);
		
		s.setMeasurement(d_ep, g_parox, m_parox);
		s.setMeasurement(d_ep, g_fluox, m_fluox);		
		
		return s;
	}

}
