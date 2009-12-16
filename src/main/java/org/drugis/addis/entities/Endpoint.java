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

public class Endpoint extends AbstractEntity implements Comparable<Endpoint> {
	private static final long serialVersionUID = -1182348850033782011L;
	
	public static final String UOM_DEFAULT_RATE = "Ratio of Patients";
	public static final String UOM_DEFAULT_CONTINUOUS = "";

	public enum Type {
		CONTINUOUS("Continuous"),
		RATE("Rate");
		
		private String d_name;
		
		Type(String name) {
			d_name = name;
		}
		
		public String toString() {
			return d_name;
		}
	}
	
	public enum Direction {
		HIGHER_IS_BETTER("Higher is better"),
		LOWER_IS_BETTER("Lower is better");
		
		
		String d_string;
		Direction(String s) {
			d_string = s;
		}
		
		public String toString() {
			return d_string;
		}
	}
	
	private String d_name;
	String d_description = "";
	private String d_unitOfMeasurement;
	private Type d_type;
	private Direction d_direction;
	
	public final static String PROPERTY_NAME = "name";
	public final static String PROPERTY_DESCRIPTION = "description";
	public final static String PROPERTY_TYPE = "type";
	public final static String PROPERTY_DIRECTION = "direction";
	public final static String PROPERTY_UNIT_OF_MEASUREMENT = "unitOfMeasurement";
	
	
	public Endpoint(String name, Type type, Direction direction) {
		d_name = name;
		d_type = type;
		d_direction = direction;
		
		if (d_type == Type.RATE)
			d_unitOfMeasurement = UOM_DEFAULT_RATE;
		else
			d_unitOfMeasurement = UOM_DEFAULT_CONTINUOUS;
	}
	
	public Endpoint(String string, Type type) {
		this(string, type, Direction.HIGHER_IS_BETTER);
	}
	
	public BasicMeasurement buildMeasurement(Arm pg) {
		switch (getType()) {
		case CONTINUOUS:
			return new BasicContinuousMeasurement(0.0, 0.0, pg.getSize());
		case RATE:
			return new BasicRateMeasurement(0, pg.getSize());
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
	
	public void setUnitOfMeasurement(String um) {
		String oldVal = d_unitOfMeasurement;
		d_unitOfMeasurement = um;
		firePropertyChange(PROPERTY_UNIT_OF_MEASUREMENT, oldVal, d_unitOfMeasurement);
	}

	public String getUnitOfMeasurement() {
		return d_unitOfMeasurement;
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
	
	public void setDirection(Direction dir) {
		Direction oldVal = d_direction;
		d_direction = dir;
		firePropertyChange(PROPERTY_DIRECTION, oldVal, d_direction);
	}
	
	public Direction getDirection() {
		return d_direction;
	}

	public int compareTo(Endpoint other) {
		return getName().compareTo(other.getName());
	}

	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
}
