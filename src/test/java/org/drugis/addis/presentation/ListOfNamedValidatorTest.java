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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Arm;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class ListOfNamedValidatorTest {
	private ObservableList<Arm> d_list;
	private ListOfNamedValidator<Arm> d_validator;

	@Before
	public void setUp() {
		d_list = new ArrayListModel<Arm>();
		d_list.add(new Arm("My arm!", 0));
		d_validator = new ListOfNamedValidator<Arm>(d_list, 2);
	}
	
	@Test
	public void testMinElements() {
		assertFalse(d_validator.getValue());
		d_list.add(new Arm("His arm!", 0));
		assertTrue(d_validator.getValue());
	}
	
	@Test
	public void testAddElementsFiresChange() {
		PropertyChangeListener mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.TRUE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.add(new Arm("His arm!", 0));
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
		
		mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.FALSE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.remove(0);
		verify(mockListener);
	}
	
	@Test
	public void testNamesShouldBeUniqueAndNotEmpty() {
		Arm arm = new Arm("My arm!", 0);
		d_list.add(arm);
		assertFalse(d_validator.getValue());
		arm.setName("His Arm!");
		assertTrue(d_validator.getValue());
		arm.setName("");
		assertFalse(d_validator.getValue());
	}
	
	@Test
	public void testNameChangeFiresChange() {
		Arm arm = new Arm("My arm!", 0);
		d_list.add(arm);

		PropertyChangeListener mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.TRUE);
		d_validator.addPropertyChangeListener(mockListener);
		arm.setName("His Arm!");
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
		
		// also test listening to elements initially in the list.
		d_validator = new ListOfNamedValidator<Arm>(d_list, 2);
		mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.FALSE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.get(0).setName("His Arm!");
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
	}
}
