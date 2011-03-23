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

package org.drugis.addis.presentation;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class NotesModelTest {
	private static final Note MY_NOTE = new Note(Source.CLINICALTRIALS, "May the source be with you");
	private List<Note> d_list;
	private NotesModel d_model;

	@Before
	public void setUp() {
		d_list = new ArrayList<Note>();
		d_model = new NotesModel(d_list);
	}
	
	@Test
	public void testGetNotes() {
		assertEquals(d_list, d_model.getNotes());
		assertNotSame(d_list, d_model.getNotes());
		
		d_list.add(MY_NOTE);
		assertEquals(d_list, d_model.getNotes());
		assertNotSame(d_list, d_model.getNotes());
	}
	
	@Test
	public void testAddNote() {
		d_model.addNote(MY_NOTE);
		assertEquals(Collections.singletonList(MY_NOTE), d_model.getNotes());
		assertEquals(Collections.singletonList(MY_NOTE), d_list);
	}
	
	@Test
	public void testAddNoteFires() {
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(d_model, NotesModel.PROPERTY_NOTES, null, Collections.singletonList(MY_NOTE));
		d_model.addPropertyChangeListener(listener);
		d_model.addNote(MY_NOTE);
		verify(listener);
	}
}
