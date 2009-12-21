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

import org.drugis.addis.entities.BasicArm;
import org.drugis.addis.entities.BasicStudy;
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
	private BasicStudy d_firstStudy;
	private Drug d_firstDrug;
	private RandomEffectsMetaAnalysis d_firstMetaAnalysis;
	
	@Before
	public void setUp() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		d_domain = new DomainImpl();
		d_firstIndication = new Indication(8L, "Indication");		
		d_firstEndpoint = new Endpoint("Endpoint", Type.RATE);
		d_firstStudy = new BasicStudy("First", d_firstIndication);
		d_firstDrug = new Drug("Drug", "atc");
		
		BasicArm pg = new BasicArm(d_firstDrug,
				new FixedDose(100.0, SIUnit.MILLIGRAMS_A_DAY), 100);
		d_firstStudy.addArm(pg);
		d_firstStudy.addEndpoint(d_firstEndpoint);
		
		d_firstStudy.setMeasurement(d_firstEndpoint, pg, 
				d_firstEndpoint.buildMeasurement(pg));
		
		d_firstMetaAnalysis = new RandomEffectsMetaAnalysis("meta", d_firstEndpoint, 
				Collections.singletonList((Study)d_firstStudy), d_firstDrug, d_firstDrug);
		
		d_domain.addIndication(d_firstIndication);
		d_domain.addEndpoint(d_firstEndpoint);
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
		assertNotNull(getIndicationsNode());
		assertEquals("Indications", getIndicationsNode().toString());
	}
	
	@Test
	public void testGetEndpointsNode() {
		assertNotNull(getEndpointsNode());
		assertEquals("Endpoints", getEndpointsNode().toString());
	}
	
	@Test
	public void testGetStudiesNode() {
		assertNotNull(getStudiesNode());
		assertEquals("Studies", getStudiesNode().toString());
	}
	
	@Test
	public void testGetDrugsNode() {
		assertEquals("Drugs", getDrugsNode().toString());
	}
	
	@Test
	public void testGetAnalysesNode(){
		assertEquals("Analyses",getAnalysesNode().toString());
	}
	
	private Object getIndicationsNode() {
		return d_treeModel.getIndicationsNode();
	}
	
	private Object getStudiesNode() {
		return d_treeModel.getStudiesNode();
	}
	
	@Test
	public void testGetIndication() {
		assertEquals(d_firstIndication, d_treeModel.getChild(getIndicationsNode(), 0));
		assertEquals(null, d_treeModel.getChild(getIndicationsNode(), 1));
	}

	@Test
	public void testGetEndpoint() {
		assertEquals(d_firstEndpoint, d_treeModel.getChild(getEndpointsNode(), 0));
		assertEquals(null, d_treeModel.getChild(getEndpointsNode(), 1));
	}
	
	@Test
	public void testGetDrug() {
		assertEquals(d_firstDrug, d_treeModel.getChild(getDrugsNode(), 0));
		assertEquals(null, d_treeModel.getChild(getDrugsNode(), 1));		
	}
	
	private Object getDrugsNode() {
		return d_treeModel.getDrugsNode();
	}

	@Test
	public void testGetAnalysis() {
		assertEquals(d_firstMetaAnalysis, d_treeModel.getChild(getAnalysesNode(), 0));
		assertEquals(null, d_treeModel.getChild(getAnalysesNode(), 1));		
	}

	
	private Object getAnalysesNode() {
		return d_treeModel.getAnalysesNode();
	}
	
	@Test
	public void testGetStudy() {
		assertEquals(d_firstStudy, d_treeModel.getChild(getStudiesNode(), 0));
		assertEquals(null, d_treeModel.getChild(getStudiesNode(), 1));
	}
	
	@Test
	public void testGetChildCount() {
		assertEquals(5, d_treeModel.getChildCount(d_treeModel.getRoot()));
		assertEquals(1, d_treeModel.getChildCount(getAnalysesNode()));
		assertEquals(1, d_treeModel.getChildCount(getIndicationsNode()));
		assertEquals(1, d_treeModel.getChildCount(getEndpointsNode()));
		assertEquals(1, d_treeModel.getChildCount(getDrugsNode()));		
		assertEquals(1, d_treeModel.getChildCount(getStudiesNode()));
		assertEquals(0, d_treeModel.getChildCount(d_firstEndpoint));
	}

	private Object getEndpointsNode() {
		return d_treeModel.getEndpointsNode();
	}
	
	@Test
	public void testGetIndexOfChild() {
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), getIndicationsNode()));
		assertEquals(0, d_treeModel.getIndexOfChild(getIndicationsNode(), d_firstIndication));
		assertEquals(2, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), getEndpointsNode()));
		assertEquals(0, d_treeModel.getIndexOfChild(getEndpointsNode(), d_firstEndpoint));
		assertEquals(-1, d_treeModel.getIndexOfChild(getEndpointsNode(), new Object()));
		assertEquals(4, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), getAnalysesNode()));
		assertEquals(3, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), getStudiesNode()));
		assertEquals(1, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), getDrugsNode()));		
		assertEquals(0, d_treeModel.getIndexOfChild(getStudiesNode(), d_firstStudy));
		assertEquals(0, d_treeModel.getIndexOfChild(getDrugsNode(), d_firstDrug));
		assertEquals(0, d_treeModel.getIndexOfChild(getAnalysesNode(), d_firstMetaAnalysis));
	}
	
	@Test
	public void testIsLeaf() {
		assertFalse(d_treeModel.isLeaf(d_treeModel.getRoot()));
		assertFalse(d_treeModel.isLeaf(getIndicationsNode()));		
		assertFalse(d_treeModel.isLeaf(getEndpointsNode()));
		assertFalse(d_treeModel.isLeaf(getStudiesNode()));
		assertFalse(d_treeModel.isLeaf(getDrugsNode()));		
		assertFalse(d_treeModel.isLeaf(getAnalysesNode()));		
		assertTrue(d_treeModel.isLeaf(d_firstIndication));
		assertTrue(d_treeModel.isLeaf(d_firstEndpoint));
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
	public void testAddStudyFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addStudy(new BasicStudy("X", d_firstIndication));
		
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
