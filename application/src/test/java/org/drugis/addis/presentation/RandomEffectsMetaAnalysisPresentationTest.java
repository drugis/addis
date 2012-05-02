/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class RandomEffectsMetaAnalysisPresentationTest {
	@Test
	public void testGetAnalysisTypeRate() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointHamd(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				new DrugSet(ExampleData.buildDrugFluoxetine()),
				new DrugSet(ExampleData.buildDrugParoxetine()));
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		PairWiseMetaAnalysisPresentation pres = (PairWiseMetaAnalysisPresentation) fact.getModel(meta);
		assertEquals(new RateVariableType(), pres.getAnalysisType());
	}
	
	@Test
	public void testGetAnalysisTypeContinuous() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				new DrugSet(ExampleData.buildDrugFluoxetine()),
				new DrugSet(ExampleData.buildDrugParoxetine()));
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		PairWiseMetaAnalysisPresentation pres = (PairWiseMetaAnalysisPresentation) fact.getModel(meta);
		assertTrue(pres.getAnalysisType() instanceof ContinuousVariableType);
	}
	
	@Test
	public void testGetIncludedStudies() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				new DrugSet(ExampleData.buildDrugFluoxetine()),
				new DrugSet(ExampleData.buildDrugParoxetine()));
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		PairWiseMetaAnalysisPresentation pres = (PairWiseMetaAnalysisPresentation) fact.getModel(meta);
		List<Study> expected = new ArrayList<Study>();
		expected.add(ExampleData.buildStudyChouinard());

		JUnitUtil.assertAllAndOnly(expected, pres.getIncludedStudies());
	}
	
	@Test
	public void testGetForestPlotPresentation() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				new DrugSet(ExampleData.buildDrugFluoxetine()),
				new DrugSet(ExampleData.buildDrugParoxetine()));
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		PairWiseMetaAnalysisPresentation pres = (PairWiseMetaAnalysisPresentation) fact.getModel(meta);
		
		ForestPlotPresentation expected = new ForestPlotPresentation(meta, BasicMeanDifference.class, 
				new PresentationModelFactory(new DomainImpl()));
		ForestPlotPresentation actual = pres.getForestPlotPresentation(BasicMeanDifference.class);
		assertEquals(expected.getRelativeEffectAt(0).getConfidenceInterval().getPointEstimate(), actual.getRelativeEffectAt(0).getConfidenceInterval().getPointEstimate(), 0.001);
		assertEquals(expected.getHeterogeneity(), actual.getHeterogeneity());
		assertEquals(expected.getHeterogeneityI2(), actual.getHeterogeneityI2());
	}
	
}
