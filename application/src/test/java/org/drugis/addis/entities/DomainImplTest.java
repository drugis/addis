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

package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DomainImplTest {

	private DomainImpl d_domain;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
	}
	
	@Test
	public void testGetDependents() {
		ExampleData.initDefaultData(d_domain);
		Indication ind = ExampleData.buildIndicationDepression();
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(ExampleData.buildStudyChouinard());
		entities.add(ExampleData.buildStudyDeWilde());
		entities.add(ExampleData.buildStudyBennie());
		entities.add(ExampleData.buildStudyMultipleArmsperDrug());

		JUnitUtil.assertAllAndOnly(entities, d_domain.getDependents(ind));
		
		Drug fluox = ExampleData.buildDrugFluoxetine();
		JUnitUtil.assertAllAndOnly(new HashSet<Entity>(entities), d_domain.getDependents(fluox));
		Study s = ExampleData.buildStudyChouinard();
		assertEquals(Collections.emptySet(), d_domain.getDependents(s));
		OutcomeMeasure d1 = ExampleData.buildEndpointHamd();
		assertEquals(new HashSet<Entity>(entities), d_domain.getDependents(d1));
	}	
	
	@Test
	public void testDependentsIncludeMetaStudies() throws Exception {
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(), studies,
				DrugSet.createTrivial(ExampleData.buildDrugFluoxetine()), DrugSet.createTrivial(ExampleData.buildDrugParoxetine())); 
		d_domain.getMetaAnalyses().add(ma);
		
		Set<Entity> deps = d_domain.getDependents(ExampleData.buildDrugFluoxetine());
		assertTrue(deps.contains(ma));
	}
}
