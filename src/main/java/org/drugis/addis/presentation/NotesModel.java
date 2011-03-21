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
