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

package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class ArmTest {
	
	private Arm d_arm;
	private String d_name;

	@Before
	public void setUp() {
		d_name = "Group 1";
		d_arm = new Arm(d_name, 0);
	}
	
	@Test
	public void testSetSize() {
		JUnitUtil.testSetter(d_arm, Arm.PROPERTY_SIZE, 0, 1);
	}
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(d_arm, Arm.PROPERTY_NAME, d_name, "New Name");
	}
	
	@Test
	public void testEquals() {
		// equality is defined on the NAME field.
		assertEquals(d_arm, new Arm(d_name, 12));
		assertEquals(d_arm.hashCode(), new Arm(d_name, 12).hashCode());
		JUnitUtil.assertNotEquals(d_arm, new Arm("Group 2", 0));

		// deep equality is defined by equality of the object graph
		assertTrue(d_arm.deepEquals(d_arm));
		assertFalse(d_arm.deepEquals(new Arm(d_name, 12)));
		assertTrue(d_arm.deepEquals(new Arm(d_name, 0)));
		d_arm.getNotes().add(new Note());
		assertFalse(d_arm.deepEquals(new Arm(d_name, 0)));
	}
	
	 
	@Test
	public void testDependencies() {
		assertEquals(Collections.emptySet(), d_arm.getDependencies());
	}
	
	@Test
	public void testToString() {
		assertEquals(d_arm.getName(), d_arm.toString());
	}
	
	@Test
	public void testNotes() {
		assertEquals(Collections.emptyList(), d_arm.getNotes());
		Note n = new Note(Source.MANUAL, "Zis is a note");
		d_arm.getNotes().add(n);
		assertEquals(Collections.singletonList(n), d_arm.getNotes());
	}
	
	@Test
	public void testCloneReturnsEqualEntity() {
		assertTrue(d_arm.clone().deepEquals(d_arm));
	}
	
	@Test
	public void testCloneReturnsDistinctObject() {
		assertNotSame(d_arm, d_arm.clone());
	}
}
