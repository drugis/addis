package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.presentation.ModifiableHolder;

@SuppressWarnings("serial")
public class ObjectWithNotes<T> extends ModifiableHolder<T> {
	private List<Note> d_notes = new ArrayList<Note>();

	public ObjectWithNotes(T obj) {
		super(obj);	
	}
	
	public List<Note> getNotes() { 
		return d_notes;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ObjectWithNotes<?>) {
			ObjectWithNotes<?> other = (ObjectWithNotes<?>)o;
			return other.getValue().equals(getValue());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getValue().hashCode();
	}
	
	@Override
	public ObjectWithNotes<T> clone() {
		ObjectWithNotes<T> clone = new ObjectWithNotes<T>(getValue());
		clone.d_notes.addAll(d_notes);
		return clone;
	}
}
