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

package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.mocks.MockNetworkMetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.util.Pair;

public class NetworkMetaAnalysisTest {
	private NetworkMetaAnalysis d_analysis;
	private MockNetworkMetaAnalysis d_mockAnalysis;

	@Before
	public void setup() throws InterruptedException{
		d_analysis = ExampleData.buildNetworkMetaAnalysisHamD();
		d_mockAnalysis = (MockNetworkMetaAnalysis) NetworkMetaAnalysisTest.buildMockNetworkMetaAnalysis();
		d_mockAnalysis.run();
	}
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(d_analysis, MetaAnalysis.PROPERTY_NAME, d_analysis.getName(), "TEST");
	}
	
	@Test
	public void testGetType() {
		assertEquals("Markov Chain Monte Carlo Network Meta-Analysis", d_analysis.getType());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRelativeEffectsList() {
		TreatmentDefinition fluox = TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine());
		TreatmentDefinition parox = TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine());
		TreatmentDefinition sertr = TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline());
		List<Pair<TreatmentDefinition>> expected = Arrays.asList(
				new Pair<TreatmentDefinition>(fluox, parox),
				new Pair<TreatmentDefinition>(fluox, sertr)
		);
		assertEquals(expected, d_analysis.getConsistencyModel().getRelativeEffectsList());
	}
	
	@Test
	public void testIsContinuous() {
		assertFalse(NetworkMetaAnalysisTest.buildMockNetworkMetaAnalysis().isContinuous());
		assertTrue(NetworkMetaAnalysisTest.buildMockContinuousNetworkMetaAnalysis().isContinuous());
	}

	public static NetworkMetaAnalysis buildMockNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard(), ExampleData.buildStudyDeWilde(), ExampleData.buildStudyFava2002()});
		List<TreatmentDefinition> drugs = Arrays.asList(new TreatmentDefinition[] {
				TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine()),
				TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine()), 
				TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline())});
		NetworkMetaAnalysis analysis = new MockNetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointHamd(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}

	public static NetworkMetaAnalysis buildMockContinuousNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard()});
		List<TreatmentDefinition> drugs = Arrays.asList(new TreatmentDefinition[] {
				TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine()),
				TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine()), 
				TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline())});
		
		NetworkMetaAnalysis analysis = new MockNetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointCgi(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}
	
}