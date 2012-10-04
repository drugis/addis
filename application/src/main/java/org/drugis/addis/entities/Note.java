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

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class Note extends AbstractEntity {
	
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
	
	@Override
	public String toString() {
		return getText();
	}
	
	public Source getSource(){
		return d_source;
	}
	
	public void setSource(Source source){
		d_source = source;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Note && o != null) {
			Note other = (Note) o;
			return EqualsUtil.equal(other.d_source, d_source) && EqualsUtil.equal(other.d_text, d_text);
		}
		return false;
	}

	/**
	 * Deep equality and shallow equality are equivalent for this type.
	 */
	public boolean deepEquals(Entity other) {
		return equals(other);
	}
	
	public int hashCode() {
		return d_source.hashCode() * 31 + d_text.hashCode();
	}
}
