package org.drugis.addis.entities;

import java.util.Set;

public class Field<T> extends AbstractEntity {
	private static final long serialVersionUID = -6196400327798299006L;

	public static final String PROPERTY_VALUE = "value";
	public static final String PROPERTY_NOTE = "note";
	
	private Note d_note = new Note();
	private T d_value;
	
	@Override
	public Set<? extends Entity> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Field(T t) {
		d_value = t;
	}

	public void setNote(Note note) {
		Note oldNote = d_note;
		d_note = note;
		firePropertyChange(PROPERTY_NOTE, oldNote, d_note);
	}

	public Note getNote() {
		return d_note;
	}

	public void setValue(T value) throws IllegalAccessException {
		throw new IllegalAccessException("Field is immutable");
	}

	public T getValue() {
		return d_value;
	}
	
	

}
