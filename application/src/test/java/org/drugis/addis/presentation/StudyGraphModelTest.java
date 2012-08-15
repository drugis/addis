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

package org.drugis.addis.presentation;

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Edge;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Vertex;
import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;


public class StudyGraphModelTest {
	private TreatmentDefinitionsGraphModel d_pm;
	private List<TreatmentDefinition> d_drugs;
	private Domain d_domain;
	private ObservableList<TreatmentDefinition> d_drugListHolder;
	private ValueHolder<OutcomeMeasure> d_outcome;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_drugs = new ArrayList<TreatmentDefinition>();
		d_drugs.add(TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine()));
		d_drugs.add(TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine()));
		d_drugs.add(TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline()));
		d_outcome = new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd());
		ObservableList<Study> studies = new ArrayListModel<Study>(Arrays.asList(
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard(), 
				ExampleData.buildStudyDeWilde(), ExampleData.buildStudyMultipleArmsperDrug()));
		d_drugListHolder = new ArrayListModel<TreatmentDefinition>(d_drugs);
		d_pm = new TreatmentDefinitionsGraphModel(studies, d_drugListHolder, d_outcome);
	}
	
	@Test
	public void testGetDrugs() {
		assertAllAndOnly(d_drugs, d_pm.getDefinitions());
	}
	
	@Test
	public void testGetStudies1() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		
		assertAllAndOnly(studies, d_pm.getStudies(TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine())));
		
		studies.remove(ExampleData.buildStudyBennie());
		assertAllAndOnly(studies, d_pm.getStudies(TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine())));
		
		assertAllAndOnly(Collections.<Study>singletonList(ExampleData.buildStudyBennie()),
				d_pm.getStudies(TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline())));
	}
	
	@Test
	public void testGetStudies2() {
		List<Study> studies = new ArrayList<Study>();
		
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		
		assertAllAndOnly(studies, d_pm.getStudies(TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine()), TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine())));
		
		studies.remove(ExampleData.buildStudyBennie());
		assertAllAndOnly(Collections.emptyList(), 
				d_pm.getStudies(TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine()), TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline())));
		
		assertAllAndOnly(Collections.<Study>singletonList(ExampleData.buildStudyBennie()),
				d_pm.getStudies(TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline()), TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine())));
	}
	
	@Test
	public void testGetStudiesWithNonTrivial() {
		TreatmentCategorization upto20mg = ExampleData.buildCategorizationUpto20mg(ExampleData.buildDrugFluoxetine());
		TreatmentDefinition def = new TreatmentDefinition(upto20mg.getCategories());
		ArrayListModel<TreatmentDefinition> defs = new ArrayListModel<TreatmentDefinition>();
		defs.add(def);

		ObservableList<Study> studies = new ArrayListModel<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		Study bennie = ExampleData.buildStudyBennie();
		studies.add(bennie);
		TreatmentDefinitionsGraphModel pm = new TreatmentDefinitionsGraphModel(studies, defs,  new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()));
		assertAllAndOnly(Collections.singleton(bennie), pm.getStudies(def));
	}
	
	@Test
	public void testVertexSet() {
		Set<Vertex> vertexSet = d_pm.vertexSet();
		assertEquals(3, vertexSet.size());
		
		for (Vertex vertex : vertexSet) {
			assertTrue(d_drugs.contains(vertex.getTreatmentDefinition()));
			assertEquals(calcSampleSize(vertex.getTreatmentDefinition()), vertex.getSampleSize());
		}
	}
	
	@Test
	public void testEdgeSet() {
		Set<Edge> edgeSet = d_pm.edgeSet();
		assertEquals(2, edgeSet.size());
		
		Edge edge1 = d_pm.getEdge(
				getVertex(TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine())),
				getVertex(TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine())));
		assertNotNull(edge1);
		assertEquals(3, edge1.getStudyCount());
		
		Edge edge2 = d_pm.getEdge(
				getVertex(TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine())),
				getVertex(TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline())));
		assertNotNull(edge2);
		assertEquals(1, edge2.getStudyCount());
		
		Edge edge3 = d_pm.getEdge(
				getVertex(TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline())),
				getVertex(TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine())));
		assertEquals(edge2, edge3);
	}
	
	@Test
	public void testFindVertex() {
		assertEquals(TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine()),
				d_pm.findVertex(TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine())).getTreatmentDefinition());
	}
	
	@Test
	public void testNullEndpoint() {
		d_pm = new TreatmentDefinitionsGraphModel(new ArrayListModel<Study>(), new ArrayListModel<TreatmentDefinition>(Collections.<TreatmentDefinition>emptyList()), 
				new UnmodifiableHolder<OutcomeMeasure>(null));
		assertTrue(d_pm.vertexSet().isEmpty());
	}
		
	@Test
	public void testChangeDrugList() {
		assertEquals(3, d_pm.vertexSet().size());
		assertEquals(2, d_pm.edgeSet().size());
		
		ListDataListener l = createStrictMock(ListDataListener.class);
		l.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_drugListHolder, ListDataEvent.INTERVAL_REMOVED, 0, 2)));
		replay(l);

		d_drugListHolder.addListDataListener(l);
		d_drugListHolder.clear();
		
		d_pm.rebuildGraph();
		assertTrue(d_pm.vertexSet().isEmpty());
		verify(l);
	}
	
	@Test
	public void testChangeStudyList() {
		ObservableList<TreatmentDefinition> drugListHolder = new ArrayListModel<TreatmentDefinition>(d_drugs);
		ObservableList<Study> studyListHolder = new ArrayListModel<Study>();
		
		d_pm = new TreatmentDefinitionsGraphModel(studyListHolder, drugListHolder, new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()));
		assertEquals(3, d_pm.vertexSet().size());
		assertTrue(d_pm.edgeSet().isEmpty());
		
		studyListHolder.addAll(d_domain.getStudies(ExampleData.buildEndpointHamd()));
		
		d_pm.rebuildGraph();
		assertEquals(3, d_pm.vertexSet().size());
		assertEquals(2, d_pm.edgeSet().size());
	}

	private Vertex getVertex(TreatmentDefinition drug) {
		return d_pm.findVertex(drug);
	}

	private int calcSampleSize(TreatmentDefinition drug) {
		int n = 0;
		for (Study s : d_pm.getStudies(drug)) {
			n += s.getSampleSize();
		}
		return n;
	}
}
