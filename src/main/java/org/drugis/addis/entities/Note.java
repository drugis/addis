/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
