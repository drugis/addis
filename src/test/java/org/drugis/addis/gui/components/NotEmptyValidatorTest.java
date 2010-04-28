/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import static org.junit.Assert.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;


import org.drugis.addis.gui.components.NotEmptyValidator;
import org.junit.Before;
import org.junit.Test;

public class NotEmptyValidatorTest {
	
	private NotEmptyValidator v;
	private JButton button;
	
	@Before
	public void setUp() {
		button = new JButton("but");
		v = new NotEmptyValidator(button);
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
