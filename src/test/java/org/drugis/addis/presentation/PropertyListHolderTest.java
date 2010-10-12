package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

public class PropertyListHolderTest {
	@Test
	public void testListHolderReturnsContentsOfProperty() {
		Study study = new Study("X", new Indication(8L, "EIGHT"));
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		endpoints.add(new Endpoint("EP1", Type.RATE));
		endpoints.add(new Endpoint("EP2", Type.CONTINUOUS));
		study.setEndpoints(endpoints);
		
		assertEquals(endpoints, (new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class)).getValue());
		
		endpoints.add(new Endpoint("EP3", Type.CONTINUOUS));
		assertFalse(endpoints.equals((new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class)).getValue()));
		
		study.setEndpoints(endpoints);
		assertEquals(endpoints, (new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class)).getValue());
	}
	
	@Test
	public void testListHolderPropagatesChangeEvents() {
		Study study = new Study("X", new Indication(8L, "EIGHT"));
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		endpoints.add(new Endpoint("EP1", Type.RATE));
		endpoints.add(new Endpoint("EP2", Type.CONTINUOUS));
		
		PropertyListHolder<Endpoint> listHolder = new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class);
		
		// create mock listener
		PropertyChangeListener mockListener = JUnitUtil.mockListener(listHolder, "value", null, endpoints);
		listHolder.addValueChangeListener(mockListener);
		
		// verify that the event is generated
		study.setEndpoints(endpoints);
		EasyMock.verify(mockListener);
	}

	@Test
	@Ignore
	public void testListHolderSetsValuesInUnderlyingProperty() {
		Study study = new Study("X", new Indication(8L, "EIGHT"));
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		endpoints.add(new Endpoint("EP1", Type.RATE));
		endpoints.add(new Endpoint("EP2", Type.CONTINUOUS));
			
		PropertyListHolder<Endpoint> listHolder = new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class);
		listHolder.setValue(endpoints);
		
		assertEquals(endpoints, study.getEndpoints());
	}
	
	// FIXME: tests below should test for proper support of PROPERTY that is a Set rather than a List.
	
	@Test
	@Ignore
	public void testListHolderSetsValuesInUnderlyingSetProperty() {
		fail();
	}
	
	@Test
	@Ignore
	public void testListHolderReturnsContentsOfSetProperty() {
		fail();
	}
}
