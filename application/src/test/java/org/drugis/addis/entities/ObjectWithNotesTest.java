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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class ObjectWithNotesTest {

	@Test
	public void testEquals() {
		ObjectWithNotes<String> a = new ObjectWithNotes<String>("A");
		ObjectWithNotes<String> b = new ObjectWithNotes<String>("B");
		JUnitUtil.assertNotEquals(a, b);
		b.setValue("A");
		assertEquals(a, b);
		
		b.getNotes().add(new Note(Source.CLINICALTRIALS, "This used to be B"));
		assertEquals(a, b);
		a.getNotes().add(new Note(Source.CLINICALTRIALS, "This used to be B"));
		assertEquals(a, b);
	}
	
	@Test
	
	public void testDeepEqual() {
		ObjectWithNotes<String> a = new ObjectWithNotes<String>("B");
		ObjectWithNotes<String> b = new ObjectWithNotes<String>("B");
		b.getNotes().add(new Note(Source.CLINICALTRIALS, "This used to be B"));
		
		assertFalse(a.deepEquals(b));
		a.getNotes().add(new Note(Source.CLINICALTRIALS, "This used to be B"));
		assertTrue(a.deepEquals(b));
	}
	
}
