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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class BasicStudyTest {
	
	private BasicArm d_pg;

	@Before
	public void setUp() {
		d_pg = new BasicArm(null, null, 0);
	}
	
	@Test
	public void testSetId() {
		JUnitUtil.testSetter(new BasicStudy("X", new Indication(0L, "")), Study.PROPERTY_ID, "X", "NCT00351273");
	}
	
	@Test
	public void testSetEndpoints() {
		Set<Endpoint> list = Collections.singleton(new Endpoint("e", Type.RATE));
		JUnitUtil.testSetter(new BasicStudy("X", new Indication(0L, "")), Study.PROPERTY_ENDPOINTS, Collections.EMPTY_SET, 
				list);
	}
	
	@Test
	public void testAddEndpoint() {
		JUnitUtil.testAdderSet(new BasicStudy("X", new Indication(0L, "")), Study.PROPERTY_ENDPOINTS, "addEndpoint", new Endpoint("e", Type.RATE));
	}
	
	@Test
	public void testSetArms() {
		List<BasicArm> list = Collections.singletonList(d_pg);
		JUnitUtil.testSetter(new BasicStudy("X", new Indication(0L, "")), Study.PROPERTY_ARMS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testInitialArms() {
		BasicStudy study = new BasicStudy("X", new Indication(0L, ""));
		assertNotNull(study.getArms());
		assertTrue(study.getArms().isEmpty());
	}
	
	@Test
	public void testAddArm() {
		JUnitUtil.testAdder(new BasicStudy("X", new Indication(0L, "")), Study.PROPERTY_ARMS, "addArm", d_pg);
	}
	
	@Test
	public void testGetDrugs() {
		BasicStudy s = ExampleData.buildStudyDeWilde();
		Set<Drug> expected = new HashSet<Drug>();
		expected.add(ExampleData.buildDrugFluoxetine());
		expected.add(ExampleData.buildDrugParoxetine());
		assertEquals(expected, s.getDrugs());
	}
	
	@Test
	public void testToString() {
		String id = "NCT00351273";
		BasicStudy study = new BasicStudy(id, new Indication(0L, ""));
		assertEquals(id, study.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMeasurementThrowsException1() {
		BasicStudy study = new BasicStudy("X", new Indication(0L, ""));
		study.getMeasurement(new Endpoint("E", Type.RATE), new BasicArm(null, null, 100));
	}
	
	@Test
	public void testSetMeasurement() {
		BasicStudy study = new BasicStudy("X", new Indication(0L, ""));
		Endpoint endpoint = new Endpoint("e", Type.RATE);
		study.addEndpoint(endpoint);
		BasicArm group = new BasicArm(null, null, 100);
		study.addArm(group);
		BasicRateMeasurement m = new BasicRateMeasurement(0, group.getSize());
		m.setRate(12);
		study.setMeasurement(study.getEndpoints().iterator().next(), study.getArms().get(0), m);
		
		assertEquals(m, study.getMeasurement(study.getEndpoints().iterator().next(), study.getArms().get(0)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetMeasurementThrowsException1() {
		BasicStudy study = new BasicStudy("X", new Indication(0L, ""));
		Endpoint e = new Endpoint("E", Type.RATE);
		BasicArm pg = new BasicArm(null, null, 100);
		study.setMeasurement(e, pg, 
				new BasicRateMeasurement(100, pg.getSize()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetMeasurementThrowsException2() {
		BasicStudy study = new BasicStudy("X", new Indication(0L, ""));
		Endpoint e = new Endpoint("e", Type.RATE);
		study.addEndpoint(e);
		BasicArm group = new BasicArm(null, null, 100);
		study.addArm(group);
		
		BasicMeasurement m = new BasicRateMeasurement(12, group.getSize());
		
		study.getEndpoints().iterator().next().setType(Type.CONTINUOUS);
		study.setMeasurement(study.getEndpoints().iterator().next(), study.getArms().get(0), m);
	}
	
	
	@Test
	public void testEquals() {
		String name1 = "Study A";
		String name2 = "Study B";
		Indication i = new Indication(0L, "");
		
		assertEquals(new BasicStudy(name1, i), new BasicStudy(name1, i));
		JUnitUtil.assertNotEquals(new BasicStudy(name1, i), new BasicStudy(name2, i));
		assertEquals(new BasicStudy(name1, i).hashCode(), new BasicStudy(name1, i).hashCode());
	}
	
	@Test
	public void testGetDependencies() {
		BasicStudy s = ExampleData.buildStudyDeWilde();
		assertFalse(s.getEndpoints().isEmpty());
		assertFalse(s.getDrugs().isEmpty());
		
		Set<Entity> dep = new HashSet<Entity>(s.getEndpoints());
		dep.addAll(s.getDrugs());
		dep.add((Indication) s.getCharacteristic(BasicStudyCharacteristic.INDICATION));
		assertEquals(dep, s.getDependencies());
	}	
	
	@Test
	public void testDeleteEndpoint() throws Exception {
		JUnitUtil.testDeleterSet(new BasicStudy("study", new Indication(0L, "")), Study.PROPERTY_ENDPOINTS, "deleteEndpoint",
				new Endpoint("e", Endpoint.Type.CONTINUOUS));
	}
	
	@Test
	public void testSetCharacteristic() {
		BasicStudy study = new BasicStudy("X", new Indication(0L, ""));
		
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(study.getCharacteristics(), 
				CharacteristicsMap.PROPERTY_CONTENTS,null, null);		
		study.getCharacteristics().addPropertyChangeListener(listener);

		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, new Integer(2));
		verify(listener);
	}
	
	@Test
	public void testGetSampleSize() {
		BasicArm pg1 = new BasicArm(null, null, 25);
		BasicArm pg2 = new BasicArm(null, null, 35);
		BasicStudy s = new BasicStudy("s1", new Indication(01L, "i"));
		s.addArm(pg1);
		s.addArm(pg2);
		assertEquals(60, s.getSampleSize());
	}
}
