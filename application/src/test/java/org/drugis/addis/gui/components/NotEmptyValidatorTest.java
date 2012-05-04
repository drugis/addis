/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import javax.swing.JComboBox;
import javax.swing.JTextField;

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
	public void testValidatesCorretly() {
		JTextField f1 = new JTextField("");
		JTextField f2 = new JTextField("");
		JComboBox box = new JComboBox(new Object[] { "x", "y"});
		box.setSelectedIndex(-1);
		v.add(f1);
		v.add(f2);
		v.add(box);
		assertFalse(button.isEnabled());
		f1.setText("jaa");
		assertFalse(button.isEnabled());
		f2.setText("hgmm");
		assertFalse(button.isEnabled());
		box.setSelectedIndex(0);
		assertTrue(button.isEnabled());
		f1.setText("");
		assertFalse(button.isEnabled());
	}

}
