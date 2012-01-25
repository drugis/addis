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

import java.util.Collections;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class Epoch extends AbstractNamedEntity<Epoch> implements TypeWithNotes, TypeWithDuration {
	public static final String PROPERTY_DURATION = "duration";

	private Duration d_duration;
	private ObservableList<Note> d_notes = new ArrayListModel<Note>();
	
	public Epoch(String name, Duration duration) {
		super(name);
		d_duration = duration;
	}

	public ObservableList<Note> getNotes() {
		return d_notes;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void setDuration(Duration duration) {
		Duration oldValue = d_duration;
		d_duration = duration;
		firePropertyChange(PROPERTY_DURATION, oldValue, d_duration);
	}

	public Duration getDuration() {
		return d_duration;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Epoch) {
			return super.equals(obj);
		}
		return false;
	}
	
	public boolean deepEquals(Entity obj) {
		if(!equals(obj)) return false;
		Epoch other = (Epoch) obj;
		return EqualsUtil.equal(other.getDuration(), getDuration()) && EntityUtil.deepEqual(other.getNotes(), getNotes());
	}
	
	@Override
	protected Epoch clone() {
		return new Epoch(getName(), d_duration);
	}
	
	@Override
	public String toString() {
		return getName() + " " + getDuration() + " " + getNotes();
	}

	public Epoch rename(String newName) {
		Epoch clone = clone();
		clone.d_name = newName;
		return clone;
	}
}
