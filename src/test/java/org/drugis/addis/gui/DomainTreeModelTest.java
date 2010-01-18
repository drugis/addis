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

package org.drugis.addis.gui;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import org.drugis.addis.entities.AdverseDrugEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.OutcomeMeasure.Type;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.junit.Before;
import org.junit.Test;

public class DomainTreeModelTest {
	private DomainTreeModel d_treeModel;
	private Domain d_domain;
	private Indication d_firstIndication;
	private Endpoint d_firstEndpoint;
	private AdverseDrugEvent d_firstADE;
	private Study d_firstStudy;
	private Drug d_firstDrug;
	private RandomEffectsMetaAnalysis d_firstMetaAnalysis;
	
	@Before
	public void setUp() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		d_domain = new DomainImpl();
		d_firstIndication = new Indication(8L, "Indication");		
		d_firstEndpoint = new Endpoint("Endpoint", Type.RATE);
		d_firstADE = new AdverseDrugEvent("firstADE", Type.CONTINUOUS);
		d_firstStudy = new Study("First", d_firstIndication);
		d_firstDrug = new Drug("Drug", "atc");
		
		Arm pg = new Arm(d_firstDrug, new FixedDose(100.0, SIUnit.MILLIGRAMS_A_DAY), 100);
		
		d_firstStudy.addArm(pg);
		d_firstStudy.addOutcomeMeasure(d_firstEndpoint);
		d_firstStudy.addOutcomeMeasure(d_firstADE);
		
		d_firstStudy.setMeasurement(d_firstEndpoint, pg, d_firstEndpoint.buildMeasurement(pg));
		d_firstStudy.setMeasurement(d_firstADE, pg, d_firstADE.buildMeasurement(pg));
				
		d_firstMetaAnalysis = new RandomEffectsMetaAnalysis("meta", d_firstEndpoint, 
				Collections.singletonList((Study)d_firstStudy), d_firstDrug, d_firstDrug);
		
		d_domain.addIndication(d_firstIndication);
		d_domain.addEndpoint(d_firstEndpoint);
		d_domain.addAde(d_firstADE);
		d_domain.addStudy(d_firstStudy);
		d_domain.addDrug(d_firstDrug);
		
		d_domain.addMetaAnalysis(d_firstMetaAnalysis);
		
		d_treeModel = new DomainTreeModel(d_domain);
	}
	
	@Test
	public void testGetRoot() {
		assertNotNull(d_treeModel.getRoot());
		assertNotNull(d_treeModel.getRoot().toString());
	}
	
	@Test
	public void testGetIndicationsNode() {
		assertNotNull(d_treeModel.getIndicationsNode());
		assertEquals("Indications", d_treeModel.getIndicationsNode().toString());
	}
	
	@Test
	public void testGetEndpointsNode() {
		assertNotNull(d_treeModel.getEndpointsNode());
		assertEquals("Endpoints", d_treeModel.getEndpointsNode().toString());
	}
	
	@Test
	public void testGetAdeNode() {
		assertNotNull(d_treeModel.getAdeNode());
		assertEquals("Adverse drug events", d_treeModel.getAdeNode().toString());
	}
	
	@Test
	public void testGetStudiesNode() {
		assertNotNull(d_treeModel.getStudiesNode());
		assertEquals("Studies", d_treeModel.getStudiesNode().toString());
	}
	
	@Test
	public void testGetDrugsNode() {
		assertEquals("Drugs", d_treeModel.getDrugsNode().toString());
	}
	
	@Test
	public void testGetAnalysesNode(){
		assertEquals("Analyses",d_treeModel.getAnalysesNode().toString());
	}
	
	@Test
	public void testGetIndication() {
		assertEquals(d_firstIndication, d_treeModel.getChild(d_treeModel.getIndicationsNode(), 0));
		assertEquals(null, d_treeModel.getChild(d_treeModel.getIndicationsNode(), 1));
	}

	@Test
	public void testGetEndpoint() {
		assertEquals(d_firstEndpoint, d_treeModel.getChild(d_treeModel.getEndpointsNode(), 0));
		assertEquals(null, d_treeModel.getChild(d_treeModel.getEndpointsNode(), 1));
	}
	
	@Test
	public void testGetADE() {
		assertEquals(d_firstADE, d_treeModel.getChild(d_treeModel.getAdeNode(), 0));
		assertEquals(null, d_treeModel.getChild(d_treeModel.getAdeNode(), 1));
	}
	
	@Test
	public void testGetDrug() {
		assertEquals(d_firstDrug, d_treeModel.getChild(d_treeModel.getDrugsNode(), 0));
		assertEquals(null, d_treeModel.getChild(d_treeModel.getDrugsNode(), 1));		
	}
	
	@Test
	public void testGetAnalysis() {
		assertEquals(d_firstMetaAnalysis, d_treeModel.getChild(d_treeModel.getAnalysesNode(), 0));
		assertEquals(null, d_treeModel.getChild(d_treeModel.getAnalysesNode(), 1));		
	}
	
	@Test
	public void testGetStudy() {
		assertEquals(d_firstStudy, d_treeModel.getChild(d_treeModel.getStudiesNode(), 0));
		assertEquals(null, d_treeModel.getChild(d_treeModel.getStudiesNode(), 1));
	}
	
	@Test
	public void testGetChildCount() {
		assertEquals(5, d_treeModel.getChildCount(d_treeModel.getRoot()));
		assertEquals(1, d_treeModel.getChildCount(d_treeModel.getAnalysesNode()));
		assertEquals(1, d_treeModel.getChildCount(d_treeModel.getIndicationsNode()));
		assertEquals(1, d_treeModel.getChildCount(d_treeModel.getEndpointsNode()));
		assertEquals(1, d_treeModel.getChildCount(d_treeModel.getAdeNode()));	
		assertEquals(1, d_treeModel.getChildCount(d_treeModel.getDrugsNode()));		
		assertEquals(1, d_treeModel.getChildCount(d_treeModel.getStudiesNode()));
		assertEquals(0, d_treeModel.getChildCount(d_firstEndpoint));
	}

	@Test
	public void testGetIndexOfChild() {
		// test root
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), d_treeModel.getIndicationsNode()));
			
		// test categories
		assertEquals(DomainTreeModel.INDICATIONS, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), d_treeModel.getIndicationsNode()));
		assertEquals(DomainTreeModel.ENDPOINTS, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), d_treeModel.getEndpointsNode()));
		assertEquals(DomainTreeModel.ADES, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), d_treeModel.getAdeNode()));
		assertEquals(DomainTreeModel.ANALYSES, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), d_treeModel.getAnalysesNode()));
		assertEquals(DomainTreeModel.STUDIES, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), d_treeModel.getStudiesNode()));
		assertEquals(DomainTreeModel.DRUGS, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), d_treeModel.getDrugsNode()));
		
		// test first element of every category
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getIndicationsNode(), d_firstIndication));
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getEndpointsNode(), d_firstEndpoint));	
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getAdeNode(), d_firstADE));			
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getStudiesNode(), d_firstStudy));
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getDrugsNode(), d_firstDrug));
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getAnalysesNode(), d_firstMetaAnalysis));
		
		// test non element of tree
		assertEquals(-1, d_treeModel.getIndexOfChild(d_treeModel.getEndpointsNode(), new Object()));
	}
	
	@Test
	public void testIsLeaf() {
		assertFalse(d_treeModel.isLeaf(d_treeModel.getRoot()));
		
		assertFalse(d_treeModel.isLeaf(d_treeModel.getIndicationsNode()));		
		assertFalse(d_treeModel.isLeaf(d_treeModel.getEndpointsNode()));
		assertFalse(d_treeModel.isLeaf(d_treeModel.getAdeNode()));		
		assertFalse(d_treeModel.isLeaf(d_treeModel.getStudiesNode()));
		assertFalse(d_treeModel.isLeaf(d_treeModel.getDrugsNode()));		
		assertFalse(d_treeModel.isLeaf(d_treeModel.getAnalysesNode()));	
		
		assertTrue(d_treeModel.isLeaf(d_firstIndication));
		assertTrue(d_treeModel.isLeaf(d_firstEndpoint));
		assertTrue(d_treeModel.isLeaf(d_firstADE));	
		assertTrue(d_treeModel.isLeaf(d_firstStudy));
		assertTrue(d_treeModel.isLeaf(d_firstDrug));		
		assertTrue(d_treeModel.isLeaf(d_firstMetaAnalysis));		
	}
	
	@Test
	public void testAddIndicationFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addIndication(new Indication(10L, "Blah"));
		
		verify(listener);
	}
	
	@Test
	public void testAddEndpointFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addEndpoint(new Endpoint("E", Type.RATE));
		
		verify(listener);
	}
	
	@Test
	public void testAddAdeFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addAde(d_firstADE);
		
		verify(listener);
	}
	
	
	@Test
	public void testAddStudyFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addStudy(new Study("X", d_firstIndication));
		
		verify(listener);
	}
	
	@Test
	public void testAddDrugFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addDrug(new Drug("X", "atc"));
		
		verify(listener);
	}	
	
	@Test
	public void testMetaStudyIsLeaf() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		RandomEffectsMetaAnalysis study = new RandomEffectsMetaAnalysis("meta2", d_firstEndpoint, new ArrayList<Study>(Collections.singleton(d_firstStudy)),
				d_firstDrug, d_firstDrug);
		d_domain.addMetaAnalysis(study);
		assertTrue(d_treeModel.isLeaf(study));
	}
}
