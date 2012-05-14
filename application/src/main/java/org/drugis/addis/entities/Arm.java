/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class Arm extends AbstractNamedEntity<Arm> implements TypeWithNotes {
	private Integer d_size;
	private ObservableList<Note> d_notes = new ArrayListModel<Note>();
	
	public static final String PROPERTY_SIZE = "size";

	public Arm(String name, int size) {
		super(name);
		d_size = size;
	}

	@Override
	public String toString() {
		return getName();
	}

	public Integer getSize() {
		return d_size;
	}

	public void setSize(Integer size) {
		Integer oldVal = d_size;
		d_size = size;
		firePropertyChange(PROPERTY_SIZE, oldVal, d_size);
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public Arm clone() {
		Arm arm = new Arm(getName(), getSize());
		arm.getNotes().addAll(getNotes());
		return arm;
	}

	public ObservableList<Note> getNotes() {
		return d_notes;
	}

	@Override
	public boolean deepEquals(Entity obj) {
		if (!equals(obj)) return false;
		Arm other = (Arm) obj;
		return EqualsUtil.equal(other.getSize(), getSize()) && EntityUtil.deepEqual(other.getNotes(), getNotes());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Arm) {
			return super.equals(obj);
		}
		return false;
	}

	public Arm rename(String newName) {
		Arm clone = clone();
		clone.d_name = newName;
		return clone;
	}
}
