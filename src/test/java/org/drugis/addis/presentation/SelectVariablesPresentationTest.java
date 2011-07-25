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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.util.SortedSetModel;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class SelectVariablesPresentationTest {
	private static final String TYPENAME = "Adverse Event";
	private static final String DESCRIPTION = "Please select the appropriate adverse events.";
	private static final String TITLE = "Select Adverse Events";

	public static class SelectPresentation extends SelectVariablesPresentation<AdverseEvent> {
		public SelectPresentation(ObservableList<AdverseEvent> options) {
			super(options, TYPENAME, TITLE, DESCRIPTION, null);
		}
		
	}
	private AdverseEvent d_ade1 = new AdverseEvent("ADE 1", AdverseEvent.convertVarType(Variable.Type.RATE));
	private AdverseEvent d_ade2 = new AdverseEvent("ADE 2", AdverseEvent.convertVarType(Variable.Type.RATE));
	private AdverseEvent d_ade3 = new AdverseEvent("ADE 3", AdverseEvent.convertVarType(Variable.Type.RATE));
	private SortedSetModel<AdverseEvent> d_list;
	private SelectAdverseEventsPresentation d_pm;
	
	@Before
	public void setUp() {
		d_list = new SortedSetModel<AdverseEvent>(Arrays.asList(d_ade1, d_ade2));
		
		d_pm = new SelectAdverseEventsPresentation(d_list, null);
	}
	
	@Test
	public void testGetTypeName() {
		assertEquals(TYPENAME, d_pm.getTypeName());
	}
	
	@Test
	public void testHasAddOptionDialog() {
		assertTrue(d_pm.hasAddOptionDialog());
	}
	
	@Test
	public void testGetTitle() {
		assertEquals(TITLE, d_pm.getTitle());
		assertEquals(DESCRIPTION, d_pm.getDescription());
	}
	
	@Test
	public void testGetOptions() {
		assertEquals(d_list, d_pm.getOptions());
		d_list.add(d_ade3);
		assertEquals(d_list, d_pm.getOptions());
	}
	
	@Test
	public void testAddSlot() {
		assertEquals(0, d_pm.countSlots());
		d_pm.addSlot();
		assertEquals(1, d_pm.countSlots());
	}
	
	@Test
	public void testGetSlot() {
		d_pm.addSlot();
		d_pm.getSlot(0).setValue(d_ade2);
		assertEquals(d_ade2, d_pm.getSlot(0).getValue());
	}
	
	@Test
	public void testRemoveSlot() {
		d_pm.addSlot();
		assertEquals(1, d_pm.countSlots());
		d_pm.removeSlot(0);
		assertEquals(0, d_pm.countSlots());
		
		d_pm.addSlot();
		d_pm.getSlot(0).setValue(d_ade1);
		d_pm.addSlot();
		d_pm.getSlot(1).setValue(d_ade2);
		d_pm.removeSlot(0);
		assertEquals(d_pm.getSlot(0).getValue(), d_ade2);
	}
	
	@Test
	public void testAddSlotsEnabledModel() {
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.TRUE);
		d_pm.addSlot();
		d_pm.addSlot();
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.TRUE);
		d_pm.addSlot();
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.TRUE);
	}
	
	@Test
	public void testInputCompleteModel() {
		assertEquals(Boolean.TRUE, d_pm.getInputCompleteModel().getValue());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm.getInputCompleteModel(), "value",
				Boolean.TRUE, Boolean.FALSE);
		d_pm.getInputCompleteModel().addValueChangeListener(mock);
		d_pm.addSlot();
		assertEquals(Boolean.FALSE, d_pm.getInputCompleteModel().getValue());
		verify(mock);
		
		mock = JUnitUtil.mockListener(d_pm.getInputCompleteModel(), "value",
				Boolean.FALSE, Boolean.TRUE);
		d_pm.getInputCompleteModel().addValueChangeListener(mock);
		d_pm.getSlot(0).setValue(d_ade2);
		assertEquals(Boolean.TRUE, d_pm.getInputCompleteModel().getValue());
		verify(mock);
	}
	
	@Test
	public void testInputCompleteModelAfterSetSlots() {
		assertEquals(Boolean.TRUE, d_pm.getInputCompleteModel().getValue());
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm.getInputCompleteModel(), "value",
				Boolean.TRUE, Boolean.FALSE);
		d_pm.getInputCompleteModel().addValueChangeListener(mock);
		ArrayList<StudyOutcomeMeasure<AdverseEvent>> slots = new ArrayList<StudyOutcomeMeasure<AdverseEvent>>();
		slots.add(new StudyOutcomeMeasure<AdverseEvent>(null));
		d_pm.setSlots(slots);
		assertEquals(Boolean.FALSE, d_pm.getInputCompleteModel().getValue());
		verify(mock);
	}
	
	@Test
	public void testSetSlotsModifiesUnderlyingList() {
		ArrayList<StudyOutcomeMeasure<AdverseEvent>> list = new ArrayList<StudyOutcomeMeasure<AdverseEvent>>();
		d_pm.setSlots(list);
		d_pm.addSlot();
		assertEquals(Collections.singletonList(new StudyOutcomeMeasure<Variable>(null)), list);		
	}
	
	@Test
	public void testSelectSameValueTwiceRemovesFromFirst() {
		d_pm.addSlot();
		d_pm.addSlot();
		d_pm.getSlot(1).setValue(d_ade1);
		assertEquals(d_ade1, d_pm.getSlot(1).getValue());
		d_pm.getSlot(0).setValue(d_ade1);
		assertEquals(d_ade1, d_pm.getSlot(0).getValue());
		assertEquals(null, d_pm.getSlot(1).getValue());
	}
}
