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

package org.drugis.addis.gui.components;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JButton;

import org.drugis.addis.presentation.ModifiableHolder;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.adapter.Bindings;

public class NotEmptyValidatorTest {
	private NotEmptyValidator v;
	private JButton button;
	
	@Before
	public void setUp() {
		button = new JButton("but");
		v = new NotEmptyValidator();
		Bindings.bind(button, "enabled", v);
	}
	
	@Test
	public void testValidatesCorrectly() {
		ModifiableHolder<String> vm1 = new ModifiableHolder<String>("");
		ModifiableHolder<String> vm2 = new ModifiableHolder<String>("");
		
		v.add(vm1);
		v.add(vm2);
		
		assertFalse(button.isEnabled());
		vm1.setValue("text");
		assertFalse(button.isEnabled());
		vm2.setValue("more text");
		assertTrue(button.isEnabled());
		vm1.setValue(null);
		assertFalse(button.isEnabled());
	}
}
