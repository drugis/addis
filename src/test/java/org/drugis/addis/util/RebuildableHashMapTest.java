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

package org.drugis.addis.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.drugis.addis.entities.Arm;
import org.junit.Test;

public class RebuildableHashMapTest {
	
	@Test
	public void testRebuild() {
		Arm arm1 = new Arm("Arm1", 0);
		Arm arm2 = new Arm("Arm2", 0);
		
		RebuildableTreeMap<Arm, Object> map = new RebuildableTreeMap<Arm, Object>();
		map.put(arm1, "bla");
		map.put(arm2, "Bla2");
		
		arm1.setName("Arm8");
		assertNull(map.get(arm1));
		
		map.rebuild();
		assertNotNull(map.get(arm1));
	}
}
