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

import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Note;
import org.drugis.common.beans.AbstractObservable;

public class NotesModel extends AbstractObservable {
	public static final String PROPERTY_NOTES = "notes";
	private final List<Note> d_notes;
	
	public NotesModel(List<Note> notes) {
		d_notes = notes;
	}
	
	public List<Note> getNotes() {
		return Collections.unmodifiableList(d_notes);
	}
	
	/**
	 * Add a note to the list of notes. Fires a PROPERTY_NOTES changed.
	 * @param note
	 */
	public void addNote(Note note) {
		d_notes.add(note);
		firePropertyChange(PROPERTY_NOTES, null, getNotes());
	}
}
