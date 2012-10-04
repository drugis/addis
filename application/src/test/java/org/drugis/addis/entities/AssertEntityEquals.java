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

package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

public class AssertEntityEquals {
	
	public static boolean armsEqual(Arm expected, Arm actual) {
		if (expected == null || actual == null) {
			return expected == actual;
		}
		return expected.deepEquals(actual);
	}

	public static void assertEntityEquals(SortedSet<? extends Entity> expected, SortedSet<? extends Entity> actual) {
		assertEntityEquals(asList(expected), asList(actual));
	}

	private static List<? extends Entity> asList(SortedSet<? extends Entity> expected) {
		List<Entity> expList = new ArrayList<Entity>();
		expList.addAll(expected);
		return expList;
	}
	
	public static void assertEntityEquals(List<? extends Entity> expected, List<? extends Entity> actual) {
		assertEquals(expected.size(), actual.size());
		Iterator<? extends Entity> expectedIterator = expected.iterator();
		Iterator<? extends Entity> actualIterator = actual.iterator();
		while (expectedIterator.hasNext()) 
			assertEntityEquals(expectedIterator.next(), actualIterator.next());
	}
	
	
	public static void assertEntityEquals(Entity expected, Entity actual){
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}

	public static void assertDomainEquals(Domain d1, Domain d2) {
		assertTrue(EqualsUtil.equal(d1, d2));
	}

}
