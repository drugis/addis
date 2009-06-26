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

package nl.rug.escher.addis.entities;

import java.util.Collections;
import java.util.Set;

import com.jgoodies.binding.beans.Model;

public class Endpoint extends Model implements Comparable<Endpoint>, Entity {
	private static final long serialVersionUID = -1182348850033782011L;

	public enum Type {
		CONTINUOUS,
		RATE;
		
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	private String d_name;
	private String d_description;
	private Type d_type;
	
	public final static String PROPERTY_NAME = "name";
	public final static String PROPERTY_DESCRIPTION = "description";
	public final static String PROPERTY_TYPE = "type";
	
	public Endpoint(String string, Type type) {
		d_name = string;
		d_type = type;
	}
	
	public Endpoint(String string) {
		d_name = string;
	}
	
	public Endpoint() {
		
	}

	public BasicMeasurement buildMeasurement() {
		switch (getType()) {
		case CONTINUOUS:
			return new BasicContinuousMeasurement(this, 0);
		case RATE:
			return new BasicRateMeasurement(this, 0, 0);
		default:
			throw new IllegalStateException("Not all enum cases covered");
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof Endpoint) {
			Endpoint other = (Endpoint)o;
			if (other.getName() == null && getName() == null) {
				return true;
			}
			return other.getName().equals(getName());
		}
		return false;
	}
	
	public int hashCode() {
		if (d_name != null) {
			return d_name.hashCode();
		}
		return 0;
	}
	
	public void setDescription(String description) {
		String oldVal = d_description;
		d_description = description;
		firePropertyChange(PROPERTY_DESCRIPTION, oldVal, d_description);
	}
	
	public String getDescription() {
		return d_description;
	}
	
	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
	
	public String getName() {
		return d_name;
	}
	
	public String toString() {
		return getName();
	}

	public void setType(Type type) {
		Type oldVal = d_type;
		d_type = type;
		firePropertyChange(PROPERTY_TYPE, oldVal, d_type);
	}

	public Type getType() {
		return d_type;
	}

	public int compareTo(Endpoint other) {
		return getName().compareTo(other.getName());
	}

	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
}
