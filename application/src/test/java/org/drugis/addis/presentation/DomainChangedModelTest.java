/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.presentation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysisTest;
import org.drugis.addis.mocks.MockNetworkMetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DomainChangedModelTest {

	private Domain d_domain;
	private DomainChangedModel d_model;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_model = new DomainChangedModel(d_domain, false);
	}
	
	@Test
	public void testInitial() {
		assertFalse(d_model.getValue());
		assertTrue(new DomainChangedModel(d_domain, true).getValue());
	}
	
	@Test
	public void testSetter() {
		JUnitUtil.testSetter(d_model, false, true);
		JUnitUtil.testSetter(d_model, true, false);
	}
	
	@Test
	public void testEndpoint() {
		d_domain.getEndpoints().add(ExampleData.buildEndpointCgi());
		assertTrue(d_model.getValue());
	}
	
	@Test
	public void testStudy() {
		Indication i = ExampleData.buildIndicationDepression();
		d_domain.getIndications().add(i);
		assertTrue(d_model.getValue());
		d_model.setValue(false);
		d_domain.getStudies().add(new Study("heavy", i ));
		assertTrue(d_model.getValue());
	}
	
	@Test 
	public void testNetworkMetaAnalysisResults() throws InterruptedException { 
		assertFalse(d_model.getValue());
		ExampleData.initDefaultData(d_domain);
		
		// Add some results
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(ExampleData.buildStudyFava2002());
		MockNetworkMetaAnalysis mockAnalysis = (MockNetworkMetaAnalysis) NetworkMetaAnalysisTest.buildMockNetworkMetaAnalysis();
		d_domain.getMetaAnalyses().add(mockAnalysis);
		assertTrue(d_model.getValue());
		d_model.setValue(false);
		mockAnalysis.run();

		assertTrue(d_model.getValue());
	}
}
