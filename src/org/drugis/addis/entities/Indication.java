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

import com.jgoodies.binding.beans.Model;

public class Indication extends Model implements Comparable<Indication>, Entity {
	private static final long serialVersionUID = -4383475531365696177L;
	
	private String d_name;
	/**
	 * SNOMED CT code is defined as a 64-bit int.
	 */
	private Long d_code;
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_CODE = "code";

	public static final String PROPERTY_LABEL = "label";
	
	public Indication(Long code, String name) {
		d_code = code;
		d_name = name;
	}

	public int compareTo(Indication other) {
		if (other == null) {
			return 1;
		}
		return d_code.compareTo(other.d_code);
	}

	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
		firePropertyChange(PROPERTY_LABEL, d_code + " " + oldVal, getLabel());
	}

	public String getName() {
		return d_name;
	}

	public void setCode(Long code) {
		Long oldVal = d_code;
		d_code = code;
		firePropertyChange(PROPERTY_CODE, oldVal, d_code);
		firePropertyChange(PROPERTY_LABEL, oldVal + " " + d_name, getLabel());
	}

	public Long getCode() {
		return d_code;
	}
	
	public String getLabel() {
		return toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Indication) {
			Indication other = (Indication)o;
			return other.d_code.equals(d_code);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_code.hashCode();
	}
	
	@Override
	public String toString() {
		return d_code.toString() + " " + d_name;
	}
}