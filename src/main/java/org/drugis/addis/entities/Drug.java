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

public class Drug extends AbstractEntity implements Comparable<Drug> {

	private String d_name = "";
	private String d_atcCode = "";
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_ATCCODE = "atcCode";	
	
	
	public Drug(){
	}
	
	public Drug(String name, String atcCode) {
		d_name = name;
		d_atcCode = atcCode;
	}

	public String getName() {
		return d_name;
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
	
	public String getAtcCode() {
		return d_atcCode;
	}

	public void setAtcCode(String atcCode) {
		String oldVal = d_atcCode;
		d_atcCode = atcCode;
		firePropertyChange(PROPERTY_ATCCODE, oldVal, d_atcCode);
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
