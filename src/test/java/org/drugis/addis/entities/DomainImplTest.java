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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
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
		entities.add(ExampleData.buildDefaultStudy1());
		entities.add(ExampleData.buildDefaultStudy2());
		entities.add(ExampleData.buildDefaultStudy3());
		entities.add(ExampleData.buildStudyBoyer1998());
		entities.add(ExampleData.buildStudyFava2002());
		entities.add(ExampleData.buildStudyNewhouse2000());
		entities.add(ExampleData.buildStudySechter1999());
		entities.add(ExampleData.buildMetaHansen2005());
		JUnitUtil.assertAllAndOnly(entities, d_domain.getDependents(ind));
		
		Drug fluox = ExampleData.buildDrugFluoxetine();
		JUnitUtil.assertAllAndOnly(new HashSet<Entity>(entities), d_domain.getDependents(fluox));
		Study s = ExampleData.buildDefaultStudy1();
		assertEquals(Collections.emptySet(), d_domain.getDependents(s));
		Endpoint d1 = ExampleData.buildEndpointHamd();
		assertEquals(new HashSet<Entity>(entities), d_domain.getDependents(d1));
	}	
	
	@Test
	public void testDependentsIncludeMetaStudies() {
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildDefaultStudy1());
		studies.add(ExampleData.buildDefaultStudy2());
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(), studies,
				ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine()); 
		d_domain.addMetaAnalysis(ma);
		
		Set<Entity> deps = d_domain.getDependents(ExampleData.buildDrugFluoxetine());
		assertTrue(deps.contains(ma));
	}
}
