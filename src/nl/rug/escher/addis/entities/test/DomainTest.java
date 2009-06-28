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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.rug.escher.addis.entities.AbstractStudy;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.DependentEntitiesException;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.DomainListener;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.MetaStudy;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.addis.entities.Endpoint.Type;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

public class DomainTest {

	private Domain d_domain;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
	}
	
	@Test
	public void testEmptyDomain() {
		assertTrue(d_domain.getEndpoints().isEmpty());
		assertTrue(d_domain.getStudies().isEmpty());
		assertTrue(d_domain.getDrugs().isEmpty());
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddEndpointNull() {
		d_domain.addEndpoint(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddStudyNull() {
		d_domain.addStudy(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddDrugNull() {
		d_domain.addStudy(null);
	}

	@Test
	public void testAddEndpoint() {
		Endpoint e = new Endpoint("e", Type.RATE);
		assertEquals(0, d_domain.getEndpoints().size());
		d_domain.addEndpoint(e);
		assertEquals(1, d_domain.getEndpoints().size());
		assertEquals(Collections.singleton(e), d_domain.getEndpoints());
	}
	
	@Test
	public void testAddStudy() {
		BasicStudy s = new BasicStudy("X");
		assertEquals(0, d_domain.getStudies().size());
		d_domain.addStudy(s);
		assertEquals(1, d_domain.getStudies().size());
		assertEquals(Collections.singleton(s), d_domain.getStudies());
	}
	
	@Test
	public void testAddDrug() {
		Drug d = new Drug();
		assertEquals(0, d_domain.getDrugs().size());
		d_domain.addDrug(d);
		assertEquals(1, d_domain.getDrugs().size());
		assertEquals(Collections.singleton(d), d_domain.getDrugs());
	}
	
	@Test
	public void testAddEndpointListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.endpointsChanged();
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addEndpoint(new Endpoint("e", Type.RATE));
		verify(mockListener);
	}
	
	@Test
	public void testAddStudyListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.studiesChanged();
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addStudy(new BasicStudy("X"));
		verify(mockListener);
	}
	
	@Test
	public void testAddDrugListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.drugsChanged();
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addDrug(new Drug());
		verify(mockListener);
	}
	
	@Test
	public void testGetStudiesByEndpoint() {
		Endpoint e1 = new Endpoint("e1", Type.RATE);
		Endpoint e2 = new Endpoint("e2", Type.RATE);
		Endpoint e3 = new Endpoint("e3", Type.RATE);
		
		List<Endpoint> l1 = new ArrayList<Endpoint>();
		l1.add(e1);
		BasicStudy s1 = new BasicStudy("X");
		s1.setId("s1");
		s1.setEndpoints(l1);
		
		List<Endpoint> l2 = new ArrayList<Endpoint>();
		l2.add(e2);
		l2.add(e1);
		BasicStudy s2 = new BasicStudy("X");
		s2.setId("s2");
		s2.setEndpoints(l2);
		
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		assertEquals(2, d_domain.getStudies(e1).size());
		assertEquals(1, d_domain.getStudies(e2).size());
		assertEquals(0, d_domain.getStudies(e3).size());
		
		assertTrue(d_domain.getStudies(e1).contains(s1));
		assertTrue(d_domain.getStudies(e1).contains(s2));
		assertTrue(d_domain.getStudies(e2).contains(s2));
	}
	
	@Test
	public void testEquals() {
		Domain d1 = new DomainImpl();
		Domain d2 = new DomainImpl();
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		Endpoint e1 = new Endpoint("e1", Type.RATE);
		Endpoint e2 = new Endpoint("e2", Type.RATE);
		d1.addEndpoint(e1);
		d1.addEndpoint(e2);
		d2.addEndpoint(e1);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.addEndpoint(e2);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		Drug d = new Drug("d1");
		d1.addDrug(d);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.addDrug(d);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		AbstractStudy s = new BasicStudy("s1");
		d1.addStudy(s);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.addStudy(s);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
	}
	
	@Test
	public void testDeleteStudy() throws DependentEntitiesException {
		AbstractStudy s = new BasicStudy("X");
		d_domain.addStudy(s);
		d_domain.deleteStudy(s);
		assertTrue(d_domain.getStudies().isEmpty());
	}
	
	@Test
	public void testDeleteStudyThrowsCorrectException() {
		AbstractStudy s1 = new BasicStudy("X");
		AbstractStudy s2 = new BasicStudy("Y");
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		Endpoint e = new Endpoint("e", Type.RATE);
		d_domain.addEndpoint(e);
		s1.addEndpoint(e);
		s2.addEndpoint(e);
		
		ArrayList<Study> studies = new ArrayList<Study>(d_domain.getStudies());
		MetaAnalysis ma = new MetaAnalysis(e, studies); 
		MetaStudy s = new MetaStudy("meta", ma);
		d_domain.addStudy(s);

		try {
			d_domain.deleteStudy(s1);
			fail();
		} catch (DependentEntitiesException e1) {
			assertEquals(Collections.singleton(s), e1.getDependents());
		}
	}
	
	@Test
	public void testDeleteStudyFires() throws DependentEntitiesException {
		AbstractStudy s1 = new BasicStudy("X");
		d_domain.addStudy(s1);
		
		DomainListener mock = createMock(DomainListener.class);
		d_domain.addListener(mock);
		mock.studiesChanged();
		replay(mock);
		d_domain.deleteStudy(s1);
		verify(mock);
	}

	@Test
	public void testDeleteDrug() throws DependentEntitiesException {
		Drug d = new Drug("X");
		d_domain.addDrug(d);
		d_domain.deleteDrug(d);
		assertTrue(d_domain.getDrugs().isEmpty());
	}
	
	@Test
	public void testDeleteDrugThrowsCorrectException() {
		BasicStudy s1 = new BasicStudy("X");
		d_domain.addStudy(s1);
		
		Drug d = new Drug("d");
		d_domain.addDrug(d);
	
		BasicPatientGroup g = new BasicPatientGroup(s1, d, new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY), 10);
		s1.addPatientGroup(g);
		
		try {
			d_domain.deleteDrug(d);
			fail();
		} catch (DependentEntitiesException e1) {
			assertEquals(Collections.singleton(s1), e1.getDependents());
		}
	}
	
	@Test
	public void testDeleteDrugFires() throws DependentEntitiesException {
		Drug d = new Drug("d");
		d_domain.addDrug(d);
		
		DomainListener mock = createMock(DomainListener.class);
		d_domain.addListener(mock);
		mock.drugsChanged();
		replay(mock);
		d_domain.deleteDrug(d);
		verify(mock);
	}

	@Test
	public void testDeleteEndpoint() throws DependentEntitiesException {
		Endpoint e = new Endpoint("e", Type.RATE);
		d_domain.addEndpoint(e);
		d_domain.deleteEndpoint(e);
		assertTrue(d_domain.getEndpoints().isEmpty());
	}
	
	@Test
	public void testDeleteEndpointThrowsCorrectException() {
		BasicStudy s1 = new BasicStudy("X");
		d_domain.addStudy(s1);
		
		Endpoint e = new Endpoint("e", Type.RATE);
		d_domain.addEndpoint(e);
		s1.addEndpoint(e);
			
		try {
			d_domain.deleteEndpoint(e);
			fail();
		} catch (DependentEntitiesException e1) {
			assertEquals(Collections.singleton(s1), e1.getDependents());
		}
	}
	
	@Test
	public void testDeleteEndpointFires() throws DependentEntitiesException {
		Endpoint d = new Endpoint("d", Type.RATE);
		d_domain.addEndpoint(d);
		
		DomainListener mock = createMock(DomainListener.class);
		d_domain.addListener(mock);
		mock.endpointsChanged();
		replay(mock);
		d_domain.deleteEndpoint(d);
		verify(mock);
	}	
}