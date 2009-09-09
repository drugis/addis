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

package org.drugis.addis.entities.test;

import static org.junit.Assert.assertEquals;

import java.util.Collections;


import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
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
		TestData.initDefaultData(d_domain);
		Drug fluox = TestData.buildDrugFluoxetine();
		assertEquals(d_domain.getStudies(), d_domain.getDependents(fluox));
		Drug viagra = TestData.buildDrugViagra();
		assertEquals(Collections.singleton(TestData.buildDefaultStudy2()), d_domain.getDependents(viagra));
		Study s = TestData.buildDefaultStudy();
		assertEquals(Collections.emptySet(), d_domain.getDependents(s));
		Endpoint d1 = TestData.buildEndpointHamd();
		assertEquals(d_domain.getStudies(), d_domain.getDependents(d1));
	}	
}
