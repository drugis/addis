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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Test;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

public class PropertyListHolderTest {
	
	@Test
	public void testListHolderReturnsContentsOfProperty() {
		Study study = new Study("X", new Indication(8L, "EIGHT"));
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		endpoints.add(new Endpoint("EP1", Type.RATE));
		endpoints.add(new Endpoint("EP2", Type.CONTINUOUS));
		List<StudyOutcomeMeasure<Endpoint>> wrappedEndpoints = Study.wrapVariables(endpoints);
		study.getEndpoints().addAll(wrappedEndpoints);
		
		assertEquals(wrappedEndpoints, (new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class)).getValue());
		
		endpoints.add(new Endpoint("EP3", Type.CONTINUOUS));
		assertFalse(endpoints.equals((new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class)).getValue()));
		
		study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(endpoints));
		wrappedEndpoints.clear();
		wrappedEndpoints.addAll(study.getEndpoints());
		assertEquals(wrappedEndpoints, (new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class)).getValue());
	}
	
	@Test
	public void testListHolderPropagatesChangeEvents() {
		Study study = new Study("X", new Indication(8L, "EIGHT"));
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		endpoints.add(new Endpoint("EP1", Type.RATE));
		endpoints.add(new Endpoint("EP2", Type.CONTINUOUS));
		
		List<StudyOutcomeMeasure<Endpoint>> wrappedEndpoints = Study.wrapVariables(endpoints);
		PropertyListHolder<Endpoint> listHolder = new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class);
		
		// create mock listener
		PropertyChangeListener mockListener = JUnitUtil.mockListener(listHolder, "value", null, wrappedEndpoints);
		listHolder.addValueChangeListener(mockListener);
		
		// verify that the event is generated
		study.getEndpoints().clear();
		study.getEndpoints().addAll(wrappedEndpoints);
		EasyMock.verify(mockListener);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListHolderSetsValuesInUnderlyingProperty() {
		Study study = new Study("X", new Indication(8L, "EIGHT"));
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		endpoints.add(new Endpoint("EP1", Type.RATE));
		endpoints.add(new Endpoint("EP2", Type.CONTINUOUS));
		
		List<StudyOutcomeMeasure<Endpoint>> wrapEndpoints = Study.wrapVariables(endpoints);
		
		PropertyListHolder<Endpoint> listHolder = new PropertyListHolder<Endpoint>(study, Study.PROPERTY_ENDPOINTS, Endpoint.class);
		listHolder.setValue(wrapEndpoints);
	
		assertFalse(listHolder.getValue() instanceof Set);
		assertEquals(wrapEndpoints, study.getEndpoints());
	}
	
	// FIXME: tests below should test for proper support of PROPERTY that is a Set rather than a List.
	
	@SuppressWarnings("unchecked")
	@Test
	public void testListHolderSetsValuesInUnderlyingSetProperty() {
		ValueModel valueHolderModel = new ValueHolder(new HashSet<String>());
		
		List<String> values = new ArrayList<String>();
		values.add("a"); values.add("b"); values.add("c");
		
		PropertyListHolder<String> propertyListHolder = new PropertyListHolder<String>(valueHolderModel, "value", String.class);
		propertyListHolder.setValue(values);
		
		assertTrue(valueHolderModel.getValue() instanceof Set);
		assertEquals(new HashSet(values), valueHolderModel.getValue());
	}
	
	@Test
	public void testListHolderReturnsContentsOfSetProperty() {
		List<String> values = new ArrayList<String>();
		values.add("a"); values.add("b"); values.add("c");
		
		ValueModel valueHolderModel = new ValueHolder(new HashSet<String>());
		
		PropertyListHolder<String> propertyListHolder = new PropertyListHolder<String>(valueHolderModel, "value", String.class);
		valueHolderModel.setValue(new HashSet<String>(values));
		
		assertEquals(values, propertyListHolder.getValue());
	}
}
