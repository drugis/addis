package nl.rug.escher.gui.test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.DomainImpl;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.gui.DomainTreeModel;

import org.junit.Before;
import org.junit.Test;

public class DomainTreeModelTest {
	DomainTreeModel d_treeModel;
	Domain d_domain;
	Endpoint d_firstEndpoint;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_firstEndpoint = new Endpoint();
		d_domain.addEndpoint(d_firstEndpoint);
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
	public void testGetEndpoint() {
		Object endpointsNode = getEndpointsNode();
		assertEquals(d_firstEndpoint, d_treeModel.getChild(endpointsNode, 0));
		assertEquals(null, d_treeModel.getChild(endpointsNode, 1));
	}
	
	@Test
	public void testGetChildCount() {
		assertEquals(1, d_treeModel.getChildCount(d_treeModel.getRoot()));
		assertEquals(1, d_treeModel.getChildCount(getEndpointsNode()));
		assertEquals(0, d_treeModel.getChildCount(d_firstEndpoint));
	}

	private Object getEndpointsNode() {
		return d_treeModel.getChild(d_treeModel.getRoot(), DomainTreeModel.ENDPOINTS);
	}
	
	@Test
	public void testGetIndexOfChild() {
		Object endpointsNode = getEndpointsNode();
		assertEquals(0, d_treeModel.getIndexOfChild(d_treeModel.getRoot(), endpointsNode));
		assertEquals(0, d_treeModel.getIndexOfChild(endpointsNode, d_firstEndpoint));
		assertEquals(-1, d_treeModel.getIndexOfChild(endpointsNode, new Object()));
	}
	
	@Test
	public void testIsLeaf() {
		assertFalse(d_treeModel.isLeaf(d_treeModel.getRoot()));
		assertFalse(d_treeModel.isLeaf(getEndpointsNode()));
		assertTrue(d_treeModel.isLeaf(d_firstEndpoint));
	}
	
	@Test
	public void testAddEndpointFires() {
		TreeModelListener listener = createMock(TreeModelListener.class);
		listener.treeStructureChanged((TreeModelEvent)notNull());
		replay(listener);
		
		d_treeModel.addTreeModelListener(listener);
		d_domain.addEndpoint(new Endpoint());
		
		verify(listener);
	}
}
