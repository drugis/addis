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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DomainTest {

	private Domain d_domain;
	private Indication d_indication;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_indication = new Indication(0L, "ind");
	}
	
	@Test
	public void testEmptyDomain() {
		assertTrue(d_domain.getEndpoints().isEmpty());
		assertTrue(d_domain.getStudies().isEmpty());
		assertTrue(d_domain.getDrugs().isEmpty());
		assertTrue(d_domain.getIndications().isEmpty());
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
	
	@Test(expected=NullPointerException.class)
	public void testAddIndicationNull() {
		d_domain.addIndication(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddMetaAnalysisNull() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		d_domain.addMetaAnalysis(null);
	}
	

	@Test
	public void testAddEndpoint() {
		Endpoint e = new Endpoint("e", Type.RATE);
		assertEquals(0, d_domain.getEndpoints().size());
		d_domain.addEndpoint(e);
		assertEquals(1, d_domain.getEndpoints().size());
		assertEquals(Collections.singleton(e), d_domain.getEndpoints());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddStudyThrowsOnUnknownIndication() {
		BasicStudy s = new BasicStudy("X", new Indication(2L, ""));
		d_domain.addStudy(s);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddMetaAnalysisThrowsOnUnknownStudy() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildDefaultStudy1());
		studies.add(ExampleData.buildDefaultStudy2());
		studies.add(new BasicStudy("iiidddd", ExampleData.buildIndicationDepression()));
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		d_domain.addMetaAnalysis(ma);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddMetaAnalysisThrowsOnDifferentIndication() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildDefaultStudy1());
		BasicStudy study2 = ExampleData.buildDefaultStudy2();
		study2.setCharacteristic(StudyCharacteristic.INDICATION, ExampleData.buildIndicationChronicHeartFailure());
		studies.add(study2);
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		d_domain.addMetaAnalysis(ma);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddMetaAnalysisThrowsOnUnknownIndication() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		List<Study> studies = new ArrayList<Study>();
		BasicStudy study2 = ExampleData.buildDefaultStudy2();
		study2.setCharacteristic(StudyCharacteristic.INDICATION, new Indication(4356346L, "notExisting"));
		studies.add(study2);
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		d_domain.addMetaAnalysis(ma);
	}		
	
	@Test(expected=EntityIdExistsException.class)
	public void testAddMetaAnalysisThrowsOnExistingName() throws Exception {
		addMetaAnalysisToDomain();
		addMetaAnalysisToDomain();
	}
	
	@Test
	public void testAddStudy() {
		d_domain.addIndication(d_indication);
		BasicStudy s = new BasicStudy("X", d_indication);
		assertEquals(0, d_domain.getStudies().size());
		d_domain.addStudy(s);
		assertEquals(1, d_domain.getStudies().size());
		assertEquals(Collections.singleton(s), d_domain.getStudies());
	}
	
	@Test
	public void testAddMetaAnalysis() throws Exception {
		assertEquals(0, d_domain.getMetaAnalyses().size());
		RandomEffectsMetaAnalysis s = addMetaAnalysisToDomain();
		
		assertTrue(d_domain.getMetaAnalyses().contains(s));
		assertEquals(1, d_domain.getMetaAnalyses().size());
	}

	private RandomEffectsMetaAnalysis addMetaAnalysisToDomain() throws Exception {
		ExampleData.initDefaultData(d_domain);
		RandomEffectsMetaAnalysis ma = generateMetaAnalysis();
		d_domain.addMetaAnalysis(ma);
		return ma;
	}

	private RandomEffectsMetaAnalysis generateMetaAnalysis() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildDefaultStudy1());
		studies.add(ExampleData.buildDefaultStudy2());
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		return ma;
	}
	
	@Test
	public void testDeleteMetaAnalysis() throws Exception {
		RandomEffectsMetaAnalysis s = addMetaAnalysisToDomain();
		
		assertTrue(d_domain.getMetaAnalyses().contains(s));
		d_domain.deleteMetaAnalysis(s);
		assertFalse(d_domain.getMetaAnalyses().contains(s));
	}		
	
	@Test
	public void testAddDrug() {
		Drug d = new Drug("name", "atc");
		assertEquals(0, d_domain.getDrugs().size());
		d_domain.addDrug(d);
		assertEquals(1, d_domain.getDrugs().size());
		assertEquals(Collections.singleton(d), d_domain.getDrugs());
	}
	
	@Test
	public void testAddIndication() {
		Indication i1 = new Indication(310497006L, "Severe depression");
		assertEquals(0, d_domain.getIndications().size());
		d_domain.addIndication(i1);
		assertEquals(1, d_domain.getIndications().size());
		assertEquals(Collections.singleton(i1), d_domain.getIndications());
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
		
		d_domain.addIndication(d_indication);
		d_domain.addListener(mockListener);
		d_indication = new Indication(0L, "");
		d_domain.addStudy(new BasicStudy("X", d_indication));
		verify(mockListener);
	}
	
	@Test
	public void testAddAnalysisListener() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		ExampleData.initDefaultData(d_domain);
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.analysesChanged();
		d_domain.addListener(mockListener);
		
		replay(mockListener);
		d_domain.addMetaAnalysis(generateMetaAnalysis());
		verify(mockListener);		
	}
	
	@Test
	public void testAddDrugListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.drugsChanged();
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addDrug(new Drug("name", "atc"));
		verify(mockListener);
	}
	
	@Test
	public void testAddIndicationListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.indicationsChanged();
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addIndication(new Indication(310497006L, "Severe depression"));
		verify(mockListener);
	}
	
	@Test
	public void testGetStudiesByEndpoint() {
		Endpoint e1 = new Endpoint("e1", Type.RATE);
		Endpoint e2 = new Endpoint("e2", Type.RATE);
		Endpoint e3 = new Endpoint("e3", Type.RATE);
		
		Set<Endpoint> l1 = new HashSet<Endpoint>();
		l1.add(e1);
		BasicStudy s1 = new BasicStudy("X", d_indication);
		s1.setId("s1");
		s1.setEndpoints(l1);
		
		Set<Endpoint> l2 = new HashSet<Endpoint>();
		l2.add(e2);
		l2.add(e1);
		BasicStudy s2 = new BasicStudy("X", d_indication);
		s2.setId("s2");
		s2.setEndpoints(l2);
		
		d_domain.addIndication(d_indication);
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
	public void testGetStudiesByIndication() {
		Endpoint e1 = new Endpoint("e1", Type.RATE);
		Endpoint e2 = new Endpoint("e2", Type.RATE);
			
		Set<Endpoint> l1 = new HashSet<Endpoint>();
		l1.add(e1);
		Indication i1 = new Indication(0L, "");
		d_domain.addIndication(i1);
		BasicStudy s1 = new BasicStudy("s1", i1);
		s1.setEndpoints(l1);
		
		Set<Endpoint> l2 = new HashSet<Endpoint>();
		l2.add(e2);
		l2.add(e1);
		BasicStudy s2 = new BasicStudy("s2", i1);
		s2.setEndpoints(l2);
		
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		Indication i2 = new Indication(007L,"This indication does not exists.");
		d_domain.addIndication(i2);
		
		assertEquals(2, d_domain.getStudies(i1).size());
		
		assertEquals(0, d_domain.getStudies(i2).size());
		
		assertTrue(d_domain.getStudies(i1).contains(s1));
		assertTrue(d_domain.getStudies(i1).contains(s2));
		
	}
	
	
	
	
	@Test
	public void testGetStudiesByDrug() {
		Drug d1 = new Drug("drug1", "atccode1");
		Drug d2 = new Drug("drug2", "atccode2");
		Drug d3 = new Drug("drug3", "atccode3");
		
		Endpoint e = new Endpoint("Death", Endpoint.Type.RATE);
		
		BasicStudy s1 = new BasicStudy("s1", d_indication);
		s1.setEndpoints(Collections.singleton(e));
		BasicPatientGroup g1 = new BasicPatientGroup(s1, d1, 
				new Dose(1.0, SIUnit.MILLIGRAMS_A_DAY), 100);
		BasicRateMeasurement m1 = new BasicRateMeasurement(e, g1);
		s1.setPatientGroups(Collections.singletonList(g1));
		s1.setMeasurement(e, g1, m1);
		d_domain.addIndication(d_indication);
		
		Indication indic2 = new Indication(1L, "");
		d_domain.addIndication(indic2);
		BasicStudy s2 = new BasicStudy("s2", indic2);
		s2.setEndpoints(Collections.singleton(e));
		BasicPatientGroup g2 = new BasicPatientGroup(s2, d1, 
				new Dose(5.0, SIUnit.MILLIGRAMS_A_DAY), 250);		
		BasicPatientGroup g3 = new BasicPatientGroup(s2, d2, 
				new Dose(5.0, SIUnit.MILLIGRAMS_A_DAY), 250);
		List<BasicPatientGroup> l1 = new ArrayList<BasicPatientGroup>();
		l1.add(g2);
		l1.add(g3);
		s2.setPatientGroups(l1);
		BasicRateMeasurement m2 = new BasicRateMeasurement(e, g2);
		BasicRateMeasurement m3 = new BasicRateMeasurement(e, g3);		
		s2.setMeasurement(e, g2, m2);
		s2.setMeasurement(e, g3, m3);
		
		
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		assertEquals(2, d_domain.getStudies(d1).size());
		assertEquals(1, d_domain.getStudies(d2).size());
		assertEquals(0, d_domain.getStudies(d3).size());
		
		assertTrue(d_domain.getStudies(d1).contains(s1));
		assertTrue(d_domain.getStudies(d1).contains(s2));
		assertTrue(d_domain.getStudies(d2).contains(s2));
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
		
		Drug d = new Drug("d1", "atc");
		d1.addDrug(d);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.addDrug(d);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		d1.addIndication(d_indication);
		BasicStudy s = new BasicStudy("s1", d_indication);
		d1.addStudy(s);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.addIndication(d_indication);
		d2.addStudy(s);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
	}
	
	@Test
	public void testDeleteStudy() throws DependentEntitiesException {
		BasicStudy s = new BasicStudy("X", d_indication);
		d_domain.addIndication(d_indication);
		d_domain.addStudy(s);
		d_domain.deleteStudy(s);
		assertTrue(d_domain.getStudies().isEmpty());
	}
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteStudyThrowsCorrectException() throws DependentEntitiesException, NullPointerException, IllegalArgumentException, EntityIdExistsException {
		BasicStudy s1 = new BasicStudy("X", d_indication);
		BasicStudy s2 = new BasicStudy("Y", d_indication);
		d_domain.addIndication(d_indication);
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		Endpoint e = new Endpoint("e", Type.RATE);
		d_domain.addEndpoint(e);
		s1.addEndpoint(e);
		s2.addEndpoint(e);
		
		ArrayList<Study> studies = new ArrayList<Study>(d_domain.getStudies());
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", e, studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine()); 
		d_domain.addMetaAnalysis(ma);
		d_domain.deleteStudy(s1);
	}
	
	@Test
	public void testDeleteStudyFires() throws DependentEntitiesException {
		BasicStudy s1 = new BasicStudy("X", d_indication);
		d_domain.addIndication(d_indication);
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
		Drug d = new Drug("X", "atc");
		d_domain.addDrug(d);
		d_domain.deleteDrug(d);
		assertTrue(d_domain.getDrugs().isEmpty());
	}
	
	@Test
	public void testDeleteDrugThrowsCorrectException() {
		BasicStudy s1 = new BasicStudy("X", d_indication);
		d_domain.addIndication(d_indication);
		d_domain.addStudy(s1);
		
		Drug d = new Drug("d", "atc");
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
		Drug d = new Drug("d", "atc");
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
		BasicStudy s1 = new BasicStudy("X", d_indication);
		d_domain.addIndication(d_indication);
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
	
	@Test
	public void testSerializationBasicStudyChangeFires() throws Exception {
		addMetaAnalysisToDomain();
		Domain newDomain = JUnitUtil.serializeObject(d_domain);
		// check connect of basic study listener
		BasicStudy bs = (BasicStudy) newDomain.getStudies().first();
		DomainListener mock2 = createMock(DomainListener.class);
		newDomain.addListener(mock2);
		mock2.studiesChanged();
		replay(mock2);
		bs.addPatientGroup(new BasicPatientGroup(bs, new Drug("viagra-2", "atc"), 
				new Dose(100.0, SIUnit.MILLIGRAMS_A_DAY), 10));
		verify(mock2);
		
	}
		
	@Test
	public void testSerializationDeleteStudyFires() throws Exception {
		ExampleData.initDefaultData(d_domain);
		Domain newDomain = JUnitUtil.serializeObject(d_domain);		
		DomainListener mock = createMock(DomainListener.class);
		newDomain.addListener(mock);
		mock.studiesChanged();
		replay(mock);
		newDomain.deleteStudy(newDomain.getStudies().last());
		verify(mock);
	}	
	
}
