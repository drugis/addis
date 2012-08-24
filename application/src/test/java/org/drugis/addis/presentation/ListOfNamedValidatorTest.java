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

package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.Set;

import org.drugis.addis.entities.AbstractNamedEntity;
import org.drugis.addis.entities.Entity;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class ListOfNamedValidatorTest {
	private static class NamedType extends AbstractNamedEntity<NamedType> {
		public NamedType(String name) {
			super(name);
		}
		
		public void setName(String newName) {
			String oldName = d_name;
			d_name = newName;
			firePropertyChange(PROPERTY_NAME, oldName, newName);
		}

		@Override
		public Set<? extends Entity> getDependencies() {
			return null;
		}
	}
	
	private ObservableList<NamedType> d_list;
	private ListOfNamedValidator<NamedType> d_validator;

	@Before
	public void setUp() {
		d_list = new ArrayListModel<NamedType>();
		d_list.add(new NamedType("My NamedType!"));
		d_validator = new ListOfNamedValidator<NamedType>(d_list, 2);
	}
	
	@Test
	public void testMinElements() {
		assertFalse(d_validator.getValue());
		d_list.add(new NamedType("His NamedType!"));
		assertTrue(d_validator.getValue());
	}
	
	@Test
	public void testAddElementsFiresChange() {
		PropertyChangeListener mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.TRUE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.add(new NamedType("His NamedType!"));
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
		
		mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.FALSE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.remove(0);
		verify(mockListener);
	}
	
	@Test
	public void testNamesShouldBeUniqueAndNotEmpty() {
		NamedType NamedType = new NamedType("My NamedType!");
		d_list.add(NamedType);
		assertFalse(d_validator.getValue());
		NamedType.setName("His NamedType!");
		assertTrue(d_validator.getValue());
		NamedType.setName("");
		assertFalse(d_validator.getValue());
	}
	
	@Test
	public void testNameChangeFiresChange() {
		NamedType NamedType = new NamedType("My NamedType!");
		d_list.add(NamedType);

		PropertyChangeListener mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.TRUE);
		d_validator.addPropertyChangeListener(mockListener);
		NamedType.setName("His NamedType!");
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
		
		// also test listening to elements initially in the list.
		d_validator = new ListOfNamedValidator<NamedType>(d_list, 2);
		mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.FALSE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.get(0).setName("His NamedType!");
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
	}
}
