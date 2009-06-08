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

package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

public class StudyTest {
	
	private BasicPatientGroup d_pg;

	@Before
	public void setUp() {
		d_pg = new BasicPatientGroup(null, null, null, 0);
	}
	
	@Test
	public void testSetId() {
		JUnitUtil.testSetter(new BasicStudy("X"), BasicStudy.PROPERTY_ID, "X", "NCT00351273");
	}
	
	@Test
	public void testSetEndpoints() {
		List<Endpoint> list = Collections.singletonList(new Endpoint());
		JUnitUtil.testSetter(new BasicStudy("X"), BasicStudy.PROPERTY_ENDPOINTS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testAddEndpoint() {
		JUnitUtil.testAdder(new BasicStudy("X"), BasicStudy.PROPERTY_ENDPOINTS, "addEndpoint", new Endpoint());
	}
	
	@Test
	public void testSetPatientGroups() {
		List<BasicPatientGroup> list = Collections.singletonList(d_pg);
		JUnitUtil.testSetter(new BasicStudy("X"), BasicStudy.PROPERTY_PATIENTGROUPS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testInitialPatientGroups() {
		BasicStudy study = new BasicStudy("X");
		assertNotNull(study.getPatientGroups());
		assertTrue(study.getPatientGroups().isEmpty());
	}
	
	@Test
	public void testAddPatientGroup() {
		JUnitUtil.testAdder(new BasicStudy("X"), BasicStudy.PROPERTY_PATIENTGROUPS, "addPatientGroup", d_pg);
	}
	
	@Test
	public void testGetDrugs() {
		BasicStudy s = TestData.buildDefaultStudy2();
		Set<Drug> expected = new HashSet<Drug>();
		expected.add(TestData.buildDrugFluoxetine());
		expected.add(TestData.buildDrugParoxetine());
		expected.add(TestData.buildDrugViagra());
		assertEquals(expected, s.getDrugs());
	}
	
	@Test
	public void testToString() {
		String id = "NCT00351273";
		BasicStudy study = new BasicStudy(id);
		assertEquals(id, study.toString());
	}

	@Test
	public void testEquals() {
		String name1 = "Study A";
		String name2 = "Study B";
		
		assertEquals(new BasicStudy(name1), new BasicStudy(name1));
		JUnitUtil.assertNotEquals(new BasicStudy(name1), new BasicStudy(name2));
		assertEquals(new BasicStudy(name1).hashCode(), new BasicStudy(name1).hashCode());
	}
}