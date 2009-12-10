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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
		entities.add(ExampleData.buildStudyChouinard());
		entities.add(ExampleData.buildStudyDeWilde());
		entities.add(ExampleData.buildStudyBennie());

		JUnitUtil.assertAllAndOnly(entities, d_domain.getDependents(ind));
		
		Drug fluox = ExampleData.buildDrugFluoxetine();
		JUnitUtil.assertAllAndOnly(new HashSet<Entity>(entities), d_domain.getDependents(fluox));
		Study s = ExampleData.buildStudyChouinard();
		assertEquals(Collections.emptySet(), d_domain.getDependents(s));
		Endpoint d1 = ExampleData.buildEndpointHamd();
		assertEquals(new HashSet<Entity>(entities), d_domain.getDependents(d1));
	}	
	
	@Test
	public void testDependentsIncludeMetaStudies() throws Exception {
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(), studies,
				ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine()); 
		d_domain.addMetaAnalysis(ma);
		
		Set<Entity> deps = d_domain.getDependents(ExampleData.buildDrugFluoxetine());
		assertTrue(deps.contains(ma));
	}
	
	@Test
	public void testReloadingDomainFiresListeners() throws Exception {
		ExampleData.initDefaultData(d_domain);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		d_domain.saveDomainData(bos);

		DomainListener mock2 = createMock(DomainListener.class);
		d_domain.addListener(mock2);
		for (DomainEvent.Type t : DomainEvent.Type.values()) {
			mock2.domainChanged(new DomainEvent(t));
		}
		replay(mock2);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		d_domain.loadDomainData(bis);

		verify(mock2);	
	}
	
	@Test
	public void testReloadingDomainKeepsStudyListener() throws Exception {
		ExampleData.initDefaultData(d_domain);
		
			
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		d_domain.saveDomainData(bos);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		d_domain.loadDomainData(bis);
		
		BasicStudy s = (BasicStudy) d_domain.getStudies().first();
		
		DomainListener mock3 = createMock(DomainListener.class);
		d_domain.addListener(mock3);
		mock3.domainChanged(new DomainEvent(DomainEvent.Type.STUDIES));
		replay(mock3);
		
		s.addPatientGroup(new BasicPatientGroup(new Drug("viagra-2", "atc"), new Dose(100.0, SIUnit.MILLIGRAMS_A_DAY), 
				10));
		verify(mock3);
	}
	
}
