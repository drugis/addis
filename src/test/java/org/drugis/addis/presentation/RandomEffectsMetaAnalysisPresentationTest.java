/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
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
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine());
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		RandomEffectsMetaAnalysisPresentation pres = (RandomEffectsMetaAnalysisPresentation) fact.getModel(meta);
		assertEquals(Variable.Type.RATE, pres.getAnalysisType());
	}
	
	@Test
	public void testGetAnalysisTypeContinuous() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine());
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		RandomEffectsMetaAnalysisPresentation pres = (RandomEffectsMetaAnalysisPresentation) fact.getModel(meta);
		assertEquals(Variable.Type.CONTINUOUS, pres.getAnalysisType());
	}
	
	@Test
	public void testGetIncludedStudies() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine());
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		RandomEffectsMetaAnalysisPresentation pres = (RandomEffectsMetaAnalysisPresentation) fact.getModel(meta);
		List<Study> expected = new ArrayList<Study>();
		expected.add(ExampleData.buildStudyChouinard());

		JUnitUtil.assertAllAndOnly(expected, pres.getIncludedStudies().getValue());
	}
	
	@Test
	public void testGetForestPlotPresentation() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine());
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		RandomEffectsMetaAnalysisPresentation pres = (RandomEffectsMetaAnalysisPresentation) fact.getModel(meta);
		
		ForestPlotPresentation expected = new ForestPlotPresentation(meta, BasicMeanDifference.class, 
				new PresentationModelFactory(new DomainImpl()));
		ForestPlotPresentation actual = pres.getForestPlotPresentation(BasicMeanDifference.class);
		assertEquals(expected.getRelativeEffectAt(0).getRelativeEffect(), actual.getRelativeEffectAt(0).getRelativeEffect(), 0.001);
		assertEquals(expected.getHeterogeneity(), actual.getHeterogeneity());
		assertEquals(expected.getHeterogeneityI2(), actual.getHeterogeneityI2());
	}
	
}
