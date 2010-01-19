package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

public class Note extends AbstractEntity {
	private static final long serialVersionUID = -7168275781000353799L;
	
	public static final String PROPERTY_TEXT = "text";
	
	private String d_text;

	public Note(){
		d_text = "";
	}
	
	public Note(String text) {
		d_text = text;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void setText(String text) {
		String oldVal = d_text;
		d_text = text;
		firePropertyChange(PROPERTY_TEXT, oldVal, text);
	}

	public String getText() {
		return d_text;
	}
	
	public String toString() {
		return getText();
	}

}
