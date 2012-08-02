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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.TreatmentCategorySet;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.junit.Before;
import org.junit.Test;

public class ForestPlotPresentationMetaTest {
	private ForestPlotPresentation d_pm;
	
	@Before
	public void setUp() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		RandomEffectsMetaAnalysis analysis = new RandomEffectsMetaAnalysis("TestMetaAnalysis",ExampleData.buildEndpointHamd(),
				studies, TreatmentCategorySet.createTrivial(ExampleData.buildDrugFluoxetine()), TreatmentCategorySet.createTrivial(ExampleData.buildDrugParoxetine()));
		d_pm = new ForestPlotPresentation(analysis, BasicOddsRatio.class, new PresentationModelFactory(new DomainImpl()));
	}
	
	@Test
	public void testNumEffects() {
		assertEquals(3, d_pm.getNumRelativeEffects());
	}
	
	@Test
	public void testStudyLabels() {
		assertEquals(ExampleData.buildStudyChouinard().toString(),
				d_pm.getStudyLabelAt(0));
		assertEquals(ExampleData.buildStudyDeWilde().toString(),
				d_pm.getStudyLabelAt(1));
	}
	
	@Test
	public void testCombinedLabel() {
		assertEquals("Combined", d_pm.getStudyLabelAt(2));
	}
	
	@Test
	public void testIsCombined() {
		assertEquals(false, d_pm.isCombined(0));
		assertEquals(false, d_pm.isCombined(1));
		assertEquals(true, d_pm.isCombined(2));
	}
	
	@Test
	public void testGetDiamondSize() {
		assertEquals(21, d_pm.getDiamondSize(0));
		assertEquals(8, d_pm.getDiamondSize(2));
	}
}
