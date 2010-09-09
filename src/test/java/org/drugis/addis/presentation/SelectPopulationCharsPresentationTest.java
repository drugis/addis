/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("serial")
public class SelectPopulationCharsPresentationTest {
	private PopulationCharacteristic d_var1 = ExampleData.buildAgeVariable();
	private PopulationCharacteristic d_var2 = ExampleData.buildGenderVariable();
	private PopulationCharacteristic d_var3 = new ContinuousPopulationCharacteristic("Blood Pressure");
	private AbstractListHolder<PopulationCharacteristic> d_list;
	private SelectPopulationCharsPresentation d_pm;
	
	@Before
	public void setUp() {
		d_list = new AbstractListHolder<PopulationCharacteristic>() {
			@Override
			public List<PopulationCharacteristic> getValue() {
				List<PopulationCharacteristic> l = new ArrayList<PopulationCharacteristic>();
				l.add(d_var1);
				l.add(d_var2);
				return l;
			}
		};
		
		d_pm = new SelectPopulationCharsPresentation(d_list, null);
	}
	
	@Test
	public void testGetTypeName() {
		assertNotNull(d_pm.getTypeName());
	}
	
	@Test
	public void testHasAddOptionDialog() {
		assertTrue(d_pm.hasAddOptionDialog());
	}
	
	@Test
	public void testGetTitle() {
		assertNotNull(d_pm.getTitle());
		assertNotNull(d_pm.getDescription());
	}
	
	@Test
	public void testGetOptions() {
		assertEquals(d_list.getValue(), d_pm.getOptions().getValue());
		d_list.getValue().add(d_var3);
		assertEquals(d_list.getValue(), d_pm.getOptions().getValue());
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
		d_pm.getSlot(0).setValue(d_var2);
		assertEquals(d_var2, d_pm.getSlot(0).getValue());
	}
	
	@Test
	public void testRemoveSlot() {
		d_pm.addSlot();
		assertEquals(1, d_pm.countSlots());
		d_pm.removeSlot(0);
		assertEquals(0, d_pm.countSlots());
		
		d_pm.addSlot();
		d_pm.getSlot(0).setValue(d_var1);
		d_pm.addSlot();
		d_pm.getSlot(1).setValue(d_var2);
		d_pm.removeSlot(0);
		assertEquals(d_pm.getSlot(0).getValue(), d_var2);
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
		d_pm.getSlot(0).setValue(d_var2);
		assertEquals(Boolean.TRUE, d_pm.getInputCompleteModel().getValue());
		verify(mock);
	}
	
	@Test
	public void testSelectSameValueTwiceRemovesFromFirst() {
		d_pm.addSlot();
		d_pm.addSlot();
		d_pm.getSlot(1).setValue(d_var1);
		assertEquals(d_var1, d_pm.getSlot(1).getValue());
		d_pm.getSlot(0).setValue(d_var1);
		assertEquals(d_var1, d_pm.getSlot(0).getValue());
		assertEquals(null, d_pm.getSlot(1).getValue());
	}
}
