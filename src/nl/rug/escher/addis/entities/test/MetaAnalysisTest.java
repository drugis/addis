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

package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.addis.entities.Endpoint.Type;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

public class MetaAnalysisTest {
	private Domain d_domain;
	private MetaAnalysis d_analysis;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		TestData.initDefaultData(d_domain);
		d_analysis = new MetaAnalysis(TestData.buildEndpointHamd(), 
				new ArrayList<Study>(d_domain.getStudies()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValidateStudiesMeasureEndpoint() {
		Endpoint e = new Endpoint("e1", Type.RATE);
		Endpoint other = new Endpoint("e2", Type.RATE);
		BasicStudy s = new BasicStudy("X");
		s.addEndpoint(other);
		new MetaAnalysis(e, Collections.singletonList((Study)s));
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(TestData.buildEndpointHamd(), d_analysis.getEndpoint());
	}
	
	@Test
	public void testGetStudies() {
		assertEquals(d_domain.getStudies(), new HashSet<Study>(d_analysis.getStudies()));
	}
	
	@Test
	public void testGetDrugs() {
		List<Drug> expect = Arrays.asList(new Drug[] {
				TestData.buildDrugFluoxetine(), TestData.buildDrugParoxetine()});
		assertEquals(expect.size(), d_analysis.getDrugs().size());
		assertTrue(d_analysis.getDrugs().containsAll(expect));
	}
	
	@Test
	public void testGetMeasurement() {
		Study s = d_domain.getStudies().first();
		PatientGroup g = s.getPatientGroups().get(1);
		assertEquals(s.getMeasurement(d_analysis.getEndpoint(), g),
				d_analysis.getMeasurement(s, g.getDrug()));
	}
	
	@Test
	public void testGetPooledMeasurement() {
		Drug d = TestData.buildDrugFluoxetine();
		int rate = 26 + 67;
		int size = 41 + 101;
		
		RateMeasurement m = (RateMeasurement)d_analysis.getPooledMeasurement(d);
		assertEquals(new Integer(rate), m.getRate());
		assertEquals(new Integer(size), m.getSampleSize());
	}

	@Test
	public void testEquals() {
		Endpoint e1 = new Endpoint("E1", Type.RATE);
		Endpoint e2 = new Endpoint("E2", Type.RATE);
		BasicStudy s1 = new BasicStudy("Test");
		BasicStudy s2 = new BasicStudy("Study");
		BasicStudy s3 = new BasicStudy("X");
		s1.addEndpoint(e1);
		s2.addEndpoint(e1);
		s3.addEndpoint(e1);
		s1.addEndpoint(e2);
		s2.addEndpoint(e2);
		s3.addEndpoint(e2);
		
		List<Study> l1 = new ArrayList<Study>();
		l1.add(s1); l1.add(s2);
		assertEquals(new MetaAnalysis(e1, l1), new MetaAnalysis(e1, l1));
		assertEquals(
				new MetaAnalysis(e1, l1).hashCode(),
				new MetaAnalysis(e1, l1).hashCode());
		
		List<Study> l2 = new ArrayList<Study>();
		l2.add(s1); l2.add(s3);
		JUnitUtil.assertNotEquals(new MetaAnalysis(e1, l1), new MetaAnalysis(e1, l2));
		
		List<Study> l3 = new ArrayList<Study>();
		l3.add(s2); l3.add(s1);
		assertEquals(new MetaAnalysis(e1, l1), new MetaAnalysis(e1, l3));
		assertEquals(
				new MetaAnalysis(e1, l1).hashCode(),
				new MetaAnalysis(e1, l3).hashCode());
		
		JUnitUtil.assertNotEquals(new MetaAnalysis(e2, l1), new MetaAnalysis(e1, l1));
	}
}