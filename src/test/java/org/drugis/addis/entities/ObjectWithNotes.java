package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.List;

public class ObjectWithNotes<T> {
	private T d_obj;
	private List<Note> d_notes = new ArrayList<Note>();

	public ObjectWithNotes(T obj) {
		d_obj = obj;	
	}
	
	public T getValue() {
		return d_obj;
	}
	
	public void setValue(T obj) {
		d_obj = obj;
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
}
