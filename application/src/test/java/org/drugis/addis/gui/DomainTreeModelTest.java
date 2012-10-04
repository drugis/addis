/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

package org.drugis.addis.gui;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityCategory;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.Unit;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.common.event.TreeModelEventMatcher;
import org.junit.Before;
import org.junit.Test;

public class DomainTreeModelTest {
	private DomainTreeModel d_treeModel;
	private Domain d_domain;
	private Indication d_firstIndication;
	private Endpoint d_firstEndpoint;
	private AdverseEvent d_firstADE;
	private PopulationCharacteristic d_firstPopChar;
	private Study d_firstStudy;
	private Drug d_firstDrug;
	private RandomEffectsMetaAnalysis d_firstMetaAnalysis;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_firstIndication = new Indication(8L, "Indication");		
		d_firstEndpoint = new Endpoint("Endpoint", Endpoint.convertVarType(Variable.Type.RATE));
		d_firstADE = new AdverseEvent("firstADE", AdverseEvent.convertVarType(Variable.Type.CONTINUOUS));
		d_firstStudy = new Study("First", d_firstIndication);
		d_firstDrug = new Drug("Drug", "atc");
		
		ExampleData.addDefaultEpochs(d_firstStudy);

		Arm pg = d_firstStudy.createAndAddArm("first", 100, d_firstDrug, new FixedDose(100.0, DoseUnit.createMilliGramsPerDay()));
		
		d_firstStudy.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(d_firstEndpoint));
		d_firstStudy.getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(d_firstADE));

		ExampleData.addDefaultMeasurementMoments(d_firstStudy);
		
		d_firstStudy.setMeasurement(d_firstStudy.findStudyOutcomeMeasure(d_firstEndpoint), pg, d_firstEndpoint.buildMeasurement(pg));
		d_firstStudy.setMeasurement(d_firstStudy.findStudyOutcomeMeasure(d_firstADE), pg, d_firstADE.buildMeasurement(pg));
		
		d_firstMetaAnalysis = ExampleData.buildRandomEffectsMetaAnalysis("meta", d_firstEndpoint, Collections.singletonList((Study)d_firstStudy), TreatmentDefinition.createTrivial(d_firstDrug), TreatmentDefinition.createTrivial(d_firstDrug));
		
		d_firstPopChar = new PopulationCharacteristic("Age", new ContinuousVariableType());
		
		d_domain.getIndications().add(d_firstIndication);
		d_domain.getEndpoints().add(d_firstEndpoint);
		d_domain.getAdverseEvents().add(d_firstADE);
		d_domain.getDrugs().add(d_firstDrug);
		d_domain.getStudies().add(d_firstStudy);
		d_domain.getPopulationCharacteristics().add(d_firstPopChar);
		
		d_domain.getMetaAnalyses().add(d_firstMetaAnalysis);
		
		d_treeModel = new DomainTreeModel(d_domain);
	}
	
	@Test
	public void testGetRoot() {
		assertNotNull(d_treeModel.getRoot());
		assertNotNull(d_treeModel.getRoot().toString());
	}
	
	@Test
	public void testGetIndication() {
		assertEquals(d_firstIndication, d_treeModel.getChild(d_domain.getCategory(Indication.class), 0));
		assertEquals(null, d_treeModel.getChild(d_domain.getCategory(Indication.class), 1));
	}

	@Test
	public void testGetEndpoint() {
		assertEquals(d_firstEndpoint, d_treeModel.getChild(d_domain.getCategory(Endpoint.class), 0));
		assertEquals(null, d_treeModel.getChild(d_domain.getCategory(Endpoint.class), 1));
	}
	
	@Test
	public void testGetADE() {
		assertEquals(d_firstADE, d_treeModel.getChild(d_domain.getCategory(AdverseEvent.class), 0));
		assertEquals(null, d_treeModel.getChild(d_domain.getCategory(AdverseEvent.class), 1));
	}
	
	@Test
	public void testGetPopChar() {
		assertEquals(d_firstPopChar, d_treeModel.getChild(d_domain.getCategory(PopulationCharacteristic.class), 0));
		assertEquals(null, d_treeModel.getChild(d_domain.getCategory(AdverseEvent.class), 1));
	}
	
	@Test
	public void testGetDrug() {
		assertEquals(d_firstDrug, d_treeModel.getChild(d_domain.getCategory(Drug.class), 0));
		assertEquals(null, d_treeModel.getChild(d_domain.getCategory(Drug.class), 1));		
	}
	
	@Test
	public void testGetAnalysis() {
		assertEquals(null, d_treeModel.getChild(d_domain.getCategory(NetworkMetaAnalysis.class), 0));
		assertEquals(d_firstMetaAnalysis, d_treeModel.getChild(d_domain.getCategory(PairWiseMetaAnalysis.class), 0));
		assertEquals(null, d_treeModel.getChild(d_domain.getCategory(PairWiseMetaAnalysis.class), 1));		
	}
	
	@Test
	public void testGetStudy() {
		assertEquals(d_firstStudy, d_treeModel.getChild(d_domain.getCategory(Study.class), 0));
		assertEquals(null, d_treeModel.getChild(d_domain.getCategory(Study.class), 1));
	}
	
	@Test
	public void testGetChildCount() {
		assertEquals(d_domain.getCategories().size(), d_treeModel.getChildCount(d_treeModel.getRoot()));
		
		assertEquals(1, d_treeModel.getChildCount(d_domain.getCategory(PairWiseMetaAnalysis.class)));
		assertEquals(0, d_treeModel.getChildCount(d_domain.getCategory(NetworkMetaAnalysis.class)));
		assertEquals(1, d_treeModel.getChildCount(d_domain.getCategory(Indication.class)));
		assertEquals(1, d_treeModel.getChildCount(d_domain.getCategory(Indication.class)));
		assertEquals(1, d_treeModel.getChildCount(d_domain.getCategory(Endpoint.class)));
		assertEquals(1, d_treeModel.getChildCount(d_domain.getCategory(AdverseEvent.class)));	
		assertEquals(1, d_treeModel.getChildCount(d_domain.getCategory(PopulationCharacteristic.class)));	
		assertEquals(1, d_treeModel.getChildCount(d_domain.getCategory(Drug.class)));		
		assertEquals(1, d_treeModel.getChildCount(d_domain.getCategory(Study.class)));
		assertEquals(0, d_treeModel.getChildCount(d_domain.getCategory(MetaBenefitRiskAnalysis.class)));
		assertEquals(0, d_treeModel.getChildCount(d_firstEndpoint));
	}

	@Test
	public void testGetIndexOfChild() {
		// test root
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), d_domain.getCategory(Unit.class)));
			
		// test categories
		for (EntityCategory cat : d_domain.getCategories()) {
			assertEquals(d_domain.getCategories().indexOf(cat),
					d_treeModel.getIndexOfChild(d_treeModel.getRoot(), cat));
		}
		
		// test first element of every category
		assertEquals(0, d_treeModel.getIndexOfChild(d_domain.getCategory(Unit.class), Domain.GRAM));
		assertEquals(0, d_treeModel.getIndexOfChild(d_domain.getCategory(Indication.class), d_firstIndication));
		assertEquals(0, d_treeModel.getIndexOfChild(d_domain.getCategory(Endpoint.class), d_firstEndpoint));	
		assertEquals(0, d_treeModel.getIndexOfChild(d_domain.getCategory(AdverseEvent.class), d_firstADE));			
		assertEquals(0, d_treeModel.getIndexOfChild(d_domain.getCategory(PopulationCharacteristic.class), d_firstPopChar));			
		assertEquals(0, d_treeModel.getIndexOfChild(d_domain.getCategory(Study.class), d_firstStudy));
		assertEquals(0, d_treeModel.getIndexOfChild(d_domain.getCategory(Drug.class), d_firstDrug));
		
		// test non element of tree
		assertEquals(-1, d_treeModel.getIndexOfChild(d_domain.getCategory(Endpoint.class), new Object()));
	}
	
	@Test
	public void testIsLeaf() {
		assertFalse(d_treeModel.isLeaf(d_treeModel.getRoot()));
		
		assertFalse(d_treeModel.isLeaf(d_domain.getCategory(Indication.class)));		
		assertFalse(d_treeModel.isLeaf(d_domain.getCategory(Endpoint.class)));
		assertFalse(d_treeModel.isLeaf(d_domain.getCategory(AdverseEvent.class)));		
		assertFalse(d_treeModel.isLeaf(d_domain.getCategory(Study.class)));
		assertFalse(d_treeModel.isLeaf(d_domain.getCategory(Drug.class)));		
		assertFalse(d_treeModel.isLeaf(d_domain.getCategory(MetaAnalysis.class)));	
		
		assertTrue(d_treeModel.isLeaf(d_firstIndication));
		assertTrue(d_treeModel.isLeaf(d_firstEndpoint));
		assertTrue(d_treeModel.isLeaf(d_firstADE));	
		assertTrue(d_treeModel.isLeaf(d_firstPopChar));	
		assertTrue(d_treeModel.isLeaf(d_firstStudy));
		assertTrue(d_treeModel.isLeaf(d_firstDrug));		
		assertTrue(d_treeModel.isLeaf(d_firstMetaAnalysis));
	}
	
	@Test
	public void testChangeIndicationsFires() {
		Indication indication1 = new Indication(10L, "Blah");
		Indication indication2 = new Indication(11L, "XBlah");
		Indication indication3 = new Indication(12L, "YBlah");
		TreeModelEvent expectedEvent1 = new TreeModelEvent(d_treeModel,
				new Object[] {d_treeModel.getRoot(), d_domain.getCategory(Indication.class)},
				new int[] {0}, new Object[] {indication1});
		TreeModelEvent expectedEvent2 = new TreeModelEvent(d_treeModel,
				new Object[] {d_treeModel.getRoot(), d_domain.getCategory(Indication.class)},
				new int[] {2}, new Object[] {indication2});
		TreeModelEvent expectedEvent3 = new TreeModelEvent(d_treeModel,
				new Object[] {d_treeModel.getRoot(), d_domain.getCategory(Indication.class)},
				new int[] {3}, new Object[] {indication3});
		TreeModelEvent expectedEvent4 = new TreeModelEvent(d_treeModel,
				new Object[] {d_treeModel.getRoot(), d_domain.getCategory(Indication.class)},
				new int[] {2}, null);
		TreeModelListener listener = createStrictMock(TreeModelListener.class);
		listener.treeNodesInserted(TreeModelEventMatcher.eqTreeModelEvent(expectedEvent1));
		listener.treeNodesInserted(TreeModelEventMatcher.eqTreeModelEvent(expectedEvent2));
		listener.treeNodesInserted(TreeModelEventMatcher.eqTreeModelEvent(expectedEvent3));
		listener.treeNodesRemoved(TreeModelEventMatcher.eqTreeModelEvent(expectedEvent4));
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.getIndications().add(indication1);
		d_domain.getIndications().addAll(Arrays.asList(indication2, indication3));
		d_domain.getIndications().remove(indication2);
		verify(listener);
	}
	
	@Test
	public void testAddEndpointFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeNodesInserted((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.getEndpoints().add(new Endpoint("E", Endpoint.convertVarType(Variable.Type.RATE)));
		verify(listener);
	}
	
	@Test
	public void testAddAdeFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeNodesInserted((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.getAdverseEvents().add(new AdverseEvent());
		verify(listener);
	}
	
	
	@Test
	public void testAddStudyFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeNodesInserted((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.getStudies().add(new Study("X", d_firstIndication));
		verify(listener);
	}
	
	@Test
	public void testAddDrugFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeNodesInserted((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.getDrugs().add(new Drug("X", "atc"));
		verify(listener);
	}	
	
	@Test
	public void testAddPopCharFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeNodesInserted((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.getPopulationCharacteristics().add(new PopulationCharacteristic("X", new ContinuousVariableType()));
		verify(listener);
	}	
	
	@Test
	public void testMetaStudyIsLeaf() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		RandomEffectsMetaAnalysis study = ExampleData.buildRandomEffectsMetaAnalysis("meta2", d_firstEndpoint, new ArrayList<Study>(Collections.singleton(d_firstStudy)), TreatmentDefinition.createTrivial(d_firstDrug), TreatmentDefinition.createTrivial(d_firstDrug));
		d_domain.getMetaAnalyses().add(study);
		assertTrue(d_treeModel.isLeaf(study));
		assertTrue(d_treeModel.isLeaf(d_firstMetaAnalysis));
	}
	
	@Test
	public void testGetPathToRoot() {
		assertEquals(new TreePath(new Object[] { d_treeModel.getRoot() }), 
				d_treeModel.getPathTo(d_treeModel.getRoot()));
	}
	
	@Test
	public void testGetPathToCategory() {
		assertEquals(new TreePath(new Object[] { d_treeModel.getRoot(), d_domain.getCategory(Indication.class) }), 
				d_treeModel.getPathTo(d_domain.getCategory(Indication.class)));
		assertEquals(new TreePath(new Object[] { d_treeModel.getRoot(), d_domain.getCategory(Endpoint.class) }), 
				d_treeModel.getPathTo(d_domain.getCategory(Endpoint.class)));
		assertEquals(new TreePath(new Object[] { d_treeModel.getRoot(), d_domain.getCategory(MetaBenefitRiskAnalysis.class) }), 
				d_treeModel.getPathTo(d_domain.getCategory(MetaBenefitRiskAnalysis.class)));
	}
	
	@Test
	public void testGetPathToEntity() {
		assertEquals(
				new TreePath(new Object[] {
						d_treeModel.getRoot(), d_domain.getCategory(Indication.class), d_firstIndication}),
				d_treeModel.getPathTo(d_firstIndication));
		assertEquals(
				new TreePath(new Object[] {
						d_treeModel.getRoot(), d_domain.getCategory(PairWiseMetaAnalysis.class), d_firstMetaAnalysis}),
				d_treeModel.getPathTo(d_firstMetaAnalysis));
	}
}
