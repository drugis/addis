/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.DrugSet;
import org.drugis.addis.presentation.StudyGraphModel.Edge;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;


public class StudyGraphModelTest {
	private StudyGraphModel d_pm;
	private List<DrugSet> d_drugs;
	private Domain d_domain;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_drugs = new ArrayList<DrugSet>();
		d_drugs.add(new DrugSet(ExampleData.buildDrugFluoxetine()));
		d_drugs.add(new DrugSet(ExampleData.buildDrugParoxetine()));
		d_drugs.add(new DrugSet(ExampleData.buildDrugSertraline()));
		d_pm = new StudyGraphModel(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()),
				new AbstractListHolder<DrugSet>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<DrugSet> getValue() {
						return d_drugs;
					}}, d_domain);
	}
	
	@Test
	public void testGetDrugs() {
		assertAllAndOnly(d_drugs, d_pm.getDrugs());
	}
	
	@Test
	public void testGetStudies1() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		
		assertAllAndOnly(studies, d_pm.getStudies(new DrugSet(ExampleData.buildDrugFluoxetine())));
		
		studies.remove(ExampleData.buildStudyBennie());
		assertAllAndOnly(studies, d_pm.getStudies(new DrugSet(ExampleData.buildDrugParoxetine())));
		
		assertAllAndOnly(Collections.<Study>singletonList(ExampleData.buildStudyBennie()),
				d_pm.getStudies(new DrugSet(ExampleData.buildDrugSertraline())));
	}
	
	@Test
	public void testGetStudies2() {
		List<Study> studies = new ArrayList<Study>();
		
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		
		assertAllAndOnly(studies, d_pm.getStudies(new DrugSet(ExampleData.buildDrugFluoxetine()), new DrugSet(ExampleData.buildDrugParoxetine())));
		
		studies.remove(ExampleData.buildStudyBennie());
		assertAllAndOnly(Collections.emptyList(), 
				d_pm.getStudies(new DrugSet(ExampleData.buildDrugParoxetine()), new DrugSet(ExampleData.buildDrugSertraline())));
		
		assertAllAndOnly(Collections.<Study>singletonList(ExampleData.buildStudyBennie()),
				d_pm.getStudies(new DrugSet(ExampleData.buildDrugSertraline()), new DrugSet(ExampleData.buildDrugFluoxetine())));
	}
	
	@Test
	public void testVertexSet() {
		Set<Vertex> vertexSet = d_pm.vertexSet();
		assertEquals(3, vertexSet.size());
		
		for (Vertex vertex : vertexSet) {
			assertTrue(d_drugs.contains(vertex.getDrug()));
			assertEquals(calcSampleSize(vertex.getDrug()), vertex.getSampleSize());
		}
	}
	
	@Test
	public void testEdgeSet() {
		Set<Edge> edgeSet = d_pm.edgeSet();
		assertEquals(2, edgeSet.size());
		
		Edge edge1 = d_pm.getEdge(
				getVertex(new DrugSet(ExampleData.buildDrugFluoxetine())),
				getVertex(new DrugSet(ExampleData.buildDrugParoxetine())));
		assertNotNull(edge1);
		assertEquals(3, edge1.getStudyCount());
		
		Edge edge2 = d_pm.getEdge(
				getVertex(new DrugSet(ExampleData.buildDrugFluoxetine())),
				getVertex(new DrugSet(ExampleData.buildDrugSertraline())));
		assertNotNull(edge2);
		assertEquals(1, edge2.getStudyCount());
		
		Edge edge3 = d_pm.getEdge(
				getVertex(new DrugSet(ExampleData.buildDrugSertraline())),
				getVertex(new DrugSet(ExampleData.buildDrugFluoxetine())));
		assertEquals(edge2, edge3);
	}
	
	@Test
	public void testFindVertex() {
		assertEquals(ExampleData.buildDrugFluoxetine(),
				d_pm.findVertex(new DrugSet(ExampleData.buildDrugFluoxetine())).getDrug());
	}
	
	@Test
	public void testNullIndication() {
		d_pm = new StudyGraphModel(new UnmodifiableHolder<Indication>(null),
				new UnmodifiableHolder<OutcomeMeasure>(null),
				new AbstractListHolder<DrugSet>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<DrugSet> getValue() {
						return new ArrayList<DrugSet>();
					}}, d_domain);
		assertTrue(d_pm.vertexSet().isEmpty());
	}
	
	@Test
	public void testNullEndpoint() {
		d_pm = new StudyGraphModel(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(null),
				new AbstractListHolder<DrugSet>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<DrugSet> getValue() {
						return new ArrayList<DrugSet>();
					}}, d_domain);
		assertTrue(d_pm.vertexSet().isEmpty());
	}
		
	@Test
	public void testChangeDrugList() {
		AbstractListHolder<DrugSet> drugListHolder = new DefaultListHolder<DrugSet>(new ArrayList<DrugSet>());
		
		PropertyChangeListener l = JUnitUtil.mockListener(drugListHolder, "value",
				new ArrayList<DrugSet>(), new ArrayList<DrugSet>(d_drugs));
		drugListHolder.addValueChangeListener(l);
	
		d_pm = new StudyGraphModel(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()),
				drugListHolder, d_domain);
		assertTrue(d_pm.vertexSet().isEmpty());
		
		drugListHolder.setValue(d_drugs);
		verify(l);
		assertEquals(3, d_pm.vertexSet().size());
		assertEquals(2, d_pm.edgeSet().size());
	}
	
	@Test
	public void testChangeStudyList() {
		AbstractListHolder<DrugSet> drugListHolder = new DefaultListHolder<DrugSet>(d_drugs);
		AbstractListHolder<Study> studyListHolder = new DefaultListHolder<Study>(new ArrayList<Study>());
		
		d_pm = new StudyGraphModel(studyListHolder, drugListHolder, new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()));
		assertEquals(3, d_pm.vertexSet().size());
		assertTrue(d_pm.edgeSet().isEmpty());
		
		studyListHolder.setValue(d_domain.getStudies(ExampleData.buildEndpointHamd()).getValue());
		assertEquals(3, d_pm.vertexSet().size());
		assertEquals(2, d_pm.edgeSet().size());
	}

	private Vertex getVertex(DrugSet drug) {
		return d_pm.findVertex(drug);
	}

	private int calcSampleSize(DrugSet drug) {
		int n = 0;
		for (Study s : d_pm.getStudies(drug)) {
			n += s.getSampleSize();
		}
		return n;
	}
}
