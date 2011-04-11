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

import org.drugis.common.EqualsUtil;

public abstract class AbstractVariable extends AbstractEntity implements Variable {

	protected String d_name;
	String d_description = "";
	protected String d_unitOfMeasurement;
	protected Variable.Type d_type;
	
	protected AbstractVariable(String name, Variable.Type type) {
		d_name = name;
		d_type = type;
				
		if (d_type == Variable.Type.RATE)
			d_unitOfMeasurement = UOM_DEFAULT_RATE;
		else
			d_unitOfMeasurement = UOM_DEFAULT_CONTINUOUS;
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

	@Override
	public String toString() {
		return getName();
	}

	public void setType(Variable.Type type) {
		Variable.Type oldVal = d_type;
		d_type = type;
		firePropertyChange(PROPERTY_TYPE, oldVal, d_type);
	}

	public Variable.Type getType() {
		return d_type;
	}

	public int compareTo(Variable other) {
		return getName().compareTo(other.getName());
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			Variable other = (Variable) o;
			return EqualsUtil.equal(other.getName(), getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	public BasicMeasurement buildMeasurement(Arm a) {
		return buildMeasurement(a.getSize());
	}

	public BasicMeasurement buildMeasurement(int size) {
		switch (getType()) {
		case CONTINUOUS:
			return new BasicContinuousMeasurement(0.0, 0.0, size);
		case RATE:
			return new BasicRateMeasurement(0, size);
		default:
			throw new IllegalStateException("Not all enum cases covered");
		}
	}
	
	public BasicMeasurement buildMeasurement() {
		return buildMeasurement(0);
	}
	
	public boolean deepEquals(Entity obj) {
		if (!equals(obj)) return false;
		
		AbstractVariable other = (AbstractVariable)obj;
		return EqualsUtil.equal(other.getType(), getType()) &&
			EqualsUtil.equal(other.getDescription(), getDescription()) &&
			EqualsUtil.equal(other.getUnitOfMeasurement(), getUnitOfMeasurement());
	}
}
