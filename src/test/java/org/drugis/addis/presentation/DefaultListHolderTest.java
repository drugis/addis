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

import java.util.ArrayList;
import java.util.List;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class DefaultListHolderTest {

	private DefaultListHolder<String> d_holder;

	@Before
	public void setUp() {
		List<String> list = new ArrayList<String>();
		d_holder = new DefaultListHolder<String>(list);
	}
	
	@Test
	public void testSetValue() {
		List<String> list = new ArrayList<String>();
		list.add("sss");
		JUnitUtil.testSetter(d_holder, AbstractValueModel.PROPERTYNAME_VALUE, d_holder.getValue(), list);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testListIsImmutable() {
		d_holder.getValue().add("hmm");
	}
}
