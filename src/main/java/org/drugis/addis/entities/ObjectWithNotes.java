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

package org.drugis.addis.entities;

import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class ObjectWithNotes<T> extends ModifiableHolder<T> implements TypeWithNotes {
	private ObservableList<Note> d_notes = new ArrayListModel<Note>();

	public ObjectWithNotes(T obj) {
		super(obj);	
	}
	
	public ObservableList<Note> getNotes() { 
		return d_notes;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ObjectWithNotes<?>) {
			ObjectWithNotes<?> other = (ObjectWithNotes<?>)o;
			return EqualsUtil.equal(other.getValue(), getValue()) &&
				other.getNotes().equals(getNotes());
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
