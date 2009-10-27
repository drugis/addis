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
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildDefaultStudy1());
		studies.add(ExampleData.buildDefaultStudy2());
		assertTrue(d_domain.getDependents(ind).containsAll(studies));
		Drug fluox = ExampleData.buildDrugFluoxetine();
		assertEquals(new HashSet<Study>(studies), d_domain.getDependents(fluox));
		Study s = ExampleData.buildDefaultStudy1();
		assertEquals(Collections.emptySet(), d_domain.getDependents(s));
		Endpoint d1 = ExampleData.buildEndpointHamd();
		assertEquals(new HashSet<Study>(studies), d_domain.getDependents(d1));
	}	
	
	@Test
	public void testDependentsIncludeMetaStudies() {
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildDefaultStudy1());
		studies.add(ExampleData.buildDefaultStudy2());
		MetaAnalysis ma = new MetaAnalysis(ExampleData.buildEndpointHamd(), studies); 
		MetaStudy metaStudy = new MetaStudy("meta", ma);
		d_domain.addStudy(metaStudy);
		
		Set<Entity> deps = d_domain.getDependents(ExampleData.buildDrugFluoxetine());
		assertTrue(deps.contains(metaStudy));
	}
}
