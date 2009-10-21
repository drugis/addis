/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

public class Drug extends AbstractEntity implements Comparable<Drug> {
	private static final long serialVersionUID = 5156008576438893074L;

	private String d_name = "";
	
	public static final String PROPERTY_NAME = "name";
	
	public Drug(String name) {
		d_name = name;
	}

	public String getName() {
		return d_name;
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Drug) {
			Drug other = (Drug) o;
			if (other.getName() == null) {
				return getName() == null;
			}
			return other.getName().equals(getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName() == null ? 0 : getName().hashCode();
	}

	public int compareTo(Drug other) {
		if (other == null) {
			return 1;
		}
		return getName().compareTo(other.getName());
	}

	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
}
