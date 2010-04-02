package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

public class Note extends AbstractEntity {
	private static final long serialVersionUID = -7168275781000353799L;
	
	public static final String PROPERTY_TEXT = "text";
	public static final String PROPERTY_SOURCE = "source";
	
	private String d_text;
	private Source d_source;

	public Note() {
	}
	
	public Note(Source source){
		this(source, "");		
	}
	
	public Note(Source source, String text) {
		d_source = source;
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
	
	public Source getSource(){
		return d_source;
	}
	
	public void setSource(Source source){
		d_source = source;
	}
	

}
