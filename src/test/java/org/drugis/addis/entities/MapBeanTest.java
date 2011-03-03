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

package org.drugis.addis.entities;

import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeListener;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class MapBeanTest {
	CharacteristicsMap d_map;
	
	@Before
	public void setUp() {
		 d_map = new CharacteristicsMap();
	}
	
	@Test(expected=RuntimeException.class)
	public void testRemove() {
		d_map.remove(BasicStudyCharacteristic.ALLOCATION);
	}
	
	@Test(expected=RuntimeException.class)
	public void testClear() {
		d_map.clear();
	}
	
	@Test
	public void testPutEmits() {
		PropertyChangeListener listener =
			JUnitUtil.mockStrictListener(d_map, MapBean.PROPERTY_CONTENTS, null, null);
		d_map.addPropertyChangeListener(listener);
		d_map.put(BasicStudyCharacteristic.BLINDING, new ObjectWithNotes<Object>(BasicStudyCharacteristic.Blinding.SINGLE_BLIND));
		verify(listener);
	}
}
