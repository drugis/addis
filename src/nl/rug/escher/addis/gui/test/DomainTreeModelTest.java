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

package nl.rug.escher.addis.gui.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MetaStudy;
import nl.rug.escher.addis.gui.DomainTreeModel;

import org.junit.Before;
import org.junit.Test;

public class DomainTreeModelTest {
	private DomainTreeModel d_treeModel;
	private Domain d_domain;
	private Endpoint d_firstEndpoint;
	private BasicStudy d_firstStudy;
	private Drug d_firstDrug;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_firstEndpoint = new Endpoint("Endpoint");
		d_firstStudy = new BasicStudy("First");
		d_firstDrug = new Drug("Drug");
		d_domain.addEndpoint(d_firstEndpoint);
		d_domain.addStudy(d_firstStudy);
		d_domain.addDrug(d_firstDrug);
		d_treeModel = new DomainTreeModel(d_domain);
	}
	
	@Test
	public void testGetRoot() {
		assertNotNull(d_treeModel.getRoot());
		assertNotNull(d_treeModel.getRoot().toString());
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
	
	private Object getStudiesNode() {
		return d_treeModel.getStudiesNode();
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
	public void testGetStudy() {
		assertEquals(d_firstStudy, d_treeModel.getChild(getStudiesNode(), 0));
		assertEquals(null, d_treeModel.getChild(getStudiesNode(), 1));
	}
	
	@Test
	public void testGetChildCount() {
		assertEquals(3, d_treeModel.getChildCount(d_treeModel.getRoot()));
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
		assertEquals(1, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), getEndpointsNode()));
		assertEquals(0, d_treeModel.getIndexOfChild(getEndpointsNode(), d_firstEndpoint));
		assertEquals(-1, d_treeModel.getIndexOfChild(getEndpointsNode(), new Object()));
		assertEquals(2, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), getStudiesNode()));
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), getDrugsNode()));		
		assertEquals(0, d_treeModel.getIndexOfChild(getStudiesNode(), d_firstStudy));
		assertEquals(0, d_treeModel.getIndexOfChild(getDrugsNode(), d_firstDrug));
	}
	
	@Test
	public void testIsLeaf() {
		assertFalse(d_treeModel.isLeaf(d_treeModel.getRoot()));
		assertFalse(d_treeModel.isLeaf(getEndpointsNode()));
		assertFalse(d_treeModel.isLeaf(getStudiesNode()));
		assertFalse(d_treeModel.isLeaf(getDrugsNode()));		
		assertTrue(d_treeModel.isLeaf(d_firstEndpoint));
		assertTrue(d_treeModel.isLeaf(d_firstStudy));
		assertTrue(d_treeModel.isLeaf(d_firstDrug));		
	}
	
	@Test
	public void testAddEndpointFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addEndpoint(new Endpoint("E"));
		
		verify(listener);
	}
	
	@Test
	public void testAddStudyFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addStudy(new BasicStudy("X"));
		
		verify(listener);
	}
	
	@Test
	public void testAddDrugFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addDrug(new Drug("X"));
		
		verify(listener);
	}	
	
	@Test
	public void testMetaStudyIsLeaf() {
		MetaStudy study = new MetaStudy("meta", null);
		d_domain.addStudy(study);
		assertTrue(d_treeModel.isLeaf(study));
	}
}