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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.presentation.ListHolder;
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
	
	@Test(expected=NullPointerException.class)
	public void testAddCategoricalVariableNull() {
		d_domain.addCategoricalVariable(null);
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
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(new BasicStudy("iiidddd", ExampleData.buildIndicationDepression()));
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		d_domain.addMetaAnalysis(ma);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddMetaAnalysisThrowsOnDifferentIndication() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		BasicStudy study2 = ExampleData.buildStudyDeWilde();
		study2.setCharacteristic(BasicStudyCharacteristic.INDICATION, ExampleData.buildIndicationChronicHeartFailure());
		studies.add(study2);
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		d_domain.addMetaAnalysis(ma);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddMetaAnalysisThrowsOnUnknownIndication() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		List<Study> studies = new ArrayList<Study>();
		BasicStudy study2 = ExampleData.buildStudyDeWilde();
		study2.setCharacteristic(BasicStudyCharacteristic.INDICATION, new Indication(4356346L, "notExisting"));
		studies.add(study2);
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		d_domain.addMetaAnalysis(ma);
	}		
	
	@Test(expected=EntityIdExistsException.class)
	public void testAddMetaAnalysisThrowsOnExistingName() throws Exception {
		ExampleData.initDefaultData(d_domain);
		RandomEffectsMetaAnalysis ma = generateMetaAnalysis();
		d_domain.addMetaAnalysis(ma);
		RandomEffectsMetaAnalysis ma1 = generateMetaAnalysis();
		d_domain.addMetaAnalysis(ma1);
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
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
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
		mockListener.domainChanged(new DomainEvent(DomainEvent.Type.ENDPOINTS));		
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addEndpoint(new Endpoint("e", Type.RATE));
		verify(mockListener);
	}
	
	@Test
	public void testAddStudyListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.domainChanged(new DomainEvent(DomainEvent.Type.STUDIES));		
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
		mockListener.domainChanged(new DomainEvent(DomainEvent.Type.ANALYSES));
		d_domain.addListener(mockListener);
		
		replay(mockListener);
		d_domain.addMetaAnalysis(generateMetaAnalysis());
		verify(mockListener);		
	}
	
	@Test
	public void testAddDrugListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.domainChanged(new DomainEvent(DomainEvent.Type.DRUGS));		
		replay(mockListener);
		
		d_domain.addListener(mockListener);
		d_domain.addDrug(new Drug("name", "atc"));
		verify(mockListener);
	}
	
	@Test
	public void testAddIndicationListener() {
		DomainListener mockListener = createMock(DomainListener.class);
		mockListener.domainChanged(new DomainEvent(DomainEvent.Type.INDICATIONS));		
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
		
		ListHolder<Study> e1Studies = d_domain.getStudies(e1);
		ListHolder<Study> e2Studies = d_domain.getStudies(e2);
		ListHolder<Study> e3Studies = d_domain.getStudies(e3);
		
		d_domain.addIndication(d_indication);
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		assertEquals(2, e1Studies.getValue().size());
		assertEquals(1, e2Studies.getValue().size());
		assertEquals(0, e3Studies.getValue().size());
		
		assertTrue(e1Studies.getValue().contains(s1));
		assertTrue(e1Studies.getValue().contains(s2));
		assertTrue(e2Studies.getValue().contains(s2));
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
		
		ListHolder<Study> studies = d_domain.getStudies(i1);
		assertEquals(2, studies.getValue().size());
		
		assertEquals(0, d_domain.getStudies(i2).getValue().size());
		
		assertTrue(studies.getValue().contains(s1));
		assertTrue(studies.getValue().contains(s2));
		
		BasicStudy s3 = new BasicStudy("s3", i1);
		s3.setEndpoints(l2);
		
		d_domain.addStudy(s3);
		assertTrue(studies.getValue().contains(s3));
	}
	
	@Test
	public void testGetStudiesByIndicationListFiresOnChange() {
		Endpoint e1 = new Endpoint("e1", Type.RATE);

		Set<Endpoint> l1 = new HashSet<Endpoint>();
		l1.add(e1);
		Indication i1 = new Indication(0L, "");
		d_domain.addIndication(i1);
		BasicStudy s1 = new BasicStudy("s1", i1);
		s1.setEndpoints(l1);

		d_domain.addStudy(s1);
		
		ListHolder<Study> studies = d_domain.getStudies(i1);
		
		assertTrue(studies.getValue().contains(s1));
		
		BasicStudy s3 = new BasicStudy("s3", i1);
		s3.setEndpoints(l1);

		List<Study> oldValue = studies.getValue();
				
		List<Study> newValue = new ArrayList<Study>(oldValue);
		newValue.add(s3);
				
		PropertyChangeListener mock = JUnitUtil.mockListener(studies, "value", oldValue, newValue);
		studies.addValueChangeListener(mock);
		d_domain.addStudy(s3);
		verify(mock);
		assertTrue(studies.getValue().contains(s3));		
	}
	
	@Test
	public void testGetStudiesByDrug() {
		Drug d1 = new Drug("drug1", "atccode1");
		Drug d2 = new Drug("drug2", "atccode2");
		Drug d3 = new Drug("drug3", "atccode3");
		
		Endpoint e = new Endpoint("Death", Endpoint.Type.RATE);
		
		BasicStudy s1 = new BasicStudy("s1", d_indication);
		s1.setEndpoints(Collections.singleton(e));
		BasicArm g1 = new BasicArm(d1, new FixedDose(1.0, SIUnit.MILLIGRAMS_A_DAY), 
				100);
		BasicMeasurement m1 = new BasicRateMeasurement(10, g1.getSize());
		s1.setArms(Collections.singletonList(g1));
		s1.setMeasurement(e, g1, m1);
		d_domain.addIndication(d_indication);
		
		Indication indic2 = new Indication(1L, "");
		d_domain.addIndication(indic2);
		BasicStudy s2 = new BasicStudy("s2", indic2);
		s2.setEndpoints(Collections.singleton(e));
		BasicArm g2 = new BasicArm(d1, new FixedDose(5.0, SIUnit.MILLIGRAMS_A_DAY), 
				250);		
		BasicArm g3 = new BasicArm(d2, new FixedDose(5.0, SIUnit.MILLIGRAMS_A_DAY), 
				250);
		List<BasicArm> l1 = new ArrayList<BasicArm>();
		l1.add(g2);
		l1.add(g3);
		s2.setArms(l1);
		BasicMeasurement m2 = new BasicRateMeasurement(10, g2.getSize());
		BasicMeasurement m3 = new BasicRateMeasurement(10, g3.getSize());		
		s2.setMeasurement(e, g2, m2);
		s2.setMeasurement(e, g3, m3);
		
		
		ListHolder<Study> d1Studies = d_domain.getStudies(d1);
		ListHolder<Study> d2Studies = d_domain.getStudies(d2);
		ListHolder<Study> d3Studies = d_domain.getStudies(d3);		
		
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		assertEquals(2, d1Studies.getValue().size());
		assertEquals(1, d2Studies.getValue().size());
		assertEquals(0, d3Studies.getValue().size());
		
		assertTrue(d1Studies.getValue().contains(s1));
		assertTrue(d1Studies.getValue().contains(s2));
		assertTrue(d2Studies.getValue().contains(s2));
	}
	
	@Test
	public void testGetStudies() {
		Drug d1 = new Drug("drug1", "atccode1");
		Drug d2 = new Drug("drug2", "atccode2");
		
		Endpoint e = new Endpoint("Death", Endpoint.Type.RATE);
		
		BasicStudy s1 = new BasicStudy("s1", d_indication);
		s1.setEndpoints(Collections.singleton(e));
		BasicArm g1 = new BasicArm(d1, new FixedDose(1.0, SIUnit.MILLIGRAMS_A_DAY), 
				100);
		BasicMeasurement m1 = new BasicRateMeasurement(10, g1.getSize());
		s1.setArms(Collections.singletonList(g1));
		s1.setMeasurement(e, g1, m1);
		d_domain.addIndication(d_indication);
		
		Indication indic2 = new Indication(1L, "");
		d_domain.addIndication(indic2);
		BasicStudy s2 = new BasicStudy("s2", indic2);
		s2.setEndpoints(Collections.singleton(e));
		BasicArm g2 = new BasicArm(d1, new FixedDose(5.0, SIUnit.MILLIGRAMS_A_DAY), 
				250);		
		BasicArm g3 = new BasicArm(d2, new FixedDose(5.0, SIUnit.MILLIGRAMS_A_DAY), 
				250);
		List<BasicArm> l1 = new ArrayList<BasicArm>();
		l1.add(g2);
		l1.add(g3);
		s2.setArms(l1);
		BasicMeasurement m2 = new BasicRateMeasurement(10, g2.getSize());
		BasicMeasurement m3 = new BasicRateMeasurement(10, g3.getSize());		
		s2.setMeasurement(e, g2, m2);
		s2.setMeasurement(e, g3, m3);
		
		
		ListHolder<Study> Studies = d_domain.getStudiesHolder();
		
		d_domain.addStudy(s1);
		d_domain.addStudy(s2);
		
		assertEquals(2, Studies.getValue().size());
		
		assertTrue(Studies.getValue().contains(s1));
		assertTrue(Studies.getValue().contains(s2));
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
		mock.domainChanged(new DomainEvent(DomainEvent.Type.STUDIES));		
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
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteDrugThrowsCorrectException() throws DependentEntitiesException {
		BasicStudy s1 = new BasicStudy("X", d_indication);
		d_domain.addIndication(d_indication);
		d_domain.addStudy(s1);
		
		Drug d = new Drug("d", "atc");
		d_domain.addDrug(d);
	
		BasicArm g = new BasicArm(d, new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY), 10);
		s1.addArm(g);
		

		d_domain.deleteDrug(d);
	}
	
	@Test
	public void testDeleteDrugFires() throws DependentEntitiesException {
		Drug d = new Drug("d", "atc");
		d_domain.addDrug(d);
		
		DomainListener mock = createMock(DomainListener.class);
		d_domain.addListener(mock);
		mock.domainChanged(new DomainEvent(DomainEvent.Type.DRUGS));		
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
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteEndpointThrowsCorrectException() throws DependentEntitiesException {
		BasicStudy s1 = new BasicStudy("X", d_indication);
		d_domain.addIndication(d_indication);
		d_domain.addStudy(s1);
		
		Endpoint e = new Endpoint("e", Type.RATE);
		d_domain.addEndpoint(e);
		s1.addEndpoint(e);
			
		d_domain.deleteEndpoint(e);
	}
	
	@Test
	public void testDeleteEndpointFires() throws DependentEntitiesException {
		Endpoint d = new Endpoint("d", Type.RATE);
		d_domain.addEndpoint(d);
		
		DomainListener mock = createMock(DomainListener.class);
		d_domain.addListener(mock);
		mock.domainChanged(new DomainEvent(DomainEvent.Type.ENDPOINTS));		
		replay(mock);
		d_domain.deleteEndpoint(d);
		verify(mock);
	}
	
	@Test
	public void testDeleteIndication() throws DependentEntitiesException {
		Indication i = new Indication(01L, "i");
		d_domain.addIndication(i);
		d_domain.deleteIndication(i);
		assertTrue(d_domain.getIndications().isEmpty());
	}
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteIndicationThrowsCorrectException() throws DependentEntitiesException {
		Indication indication = new Indication(5L, "");
		BasicStudy s1 = new BasicStudy("X", indication);
		d_domain.addIndication(indication);
		d_domain.addStudy(s1);
			
		d_domain.deleteIndication(indication);
	}
	
	@Test
	public void testDeleteIndicationFires() throws DependentEntitiesException {
		Indication i = new Indication(5L, "");
		d_domain.addIndication(i);
		
		DomainListener mock = createMock(DomainListener.class);
		d_domain.addListener(mock);
		mock.domainChanged(new DomainEvent(DomainEvent.Type.INDICATIONS));		
		replay(mock);
		d_domain.deleteIndication(i);
		verify(mock);
	}
	
	@Test
	public void testGetCategoricalVariables() {
		CategoricalVariable c = new CategoricalVariable("x", new String[]{"x", "y", "z"});
		d_domain.addCategoricalVariable(c);
		
		assertEquals(Collections.singleton(c), d_domain.getCategoricalVariables());
	}
	
	@Test
	public void testAddCategoricalVariableFires() {
		DomainListener mock = createMock(DomainListener.class);
		d_domain.addListener(mock);
		mock.domainChanged(new DomainEvent(DomainEvent.Type.VARIABLES));
		replay(mock);
		d_domain.addCategoricalVariable(new CategoricalVariable("x", new String[]{"x"}));
		verify(mock);
	}

}
