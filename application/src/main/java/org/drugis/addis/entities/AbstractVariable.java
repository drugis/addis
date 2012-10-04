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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public abstract class AbstractVariable extends AbstractNamedEntity<Variable> implements Variable {
	String d_description = "";

	protected VariableType d_varType;

	protected AbstractVariable(String name, VariableType type) {
		super(name);
		d_varType = type;
	}

	public static VariableType convertVarType(Variable.Type type) {
		VariableType varType = null;
		
		switch (type) {
		case CATEGORICAL:
			varType = new CategoricalVariableType();
			break;
		case CONTINUOUS:
			varType = new ContinuousVariableType(UOM_DEFAULT_CONTINUOUS);
			break;
		case RATE:
			varType = new RateVariableType();
			break;
		}
		return varType;
	}
	
	public void setDescription(String description) {
		String oldVal = d_description;
		d_description = description;
		firePropertyChange(PROPERTY_DESCRIPTION, oldVal, d_description);
	}

	public String getDescription() {
		return d_description;
	}
	
	@Override
	public String getLabel() {
		return getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			return super.equals(o);
		}
		return false;
	}


	public BasicMeasurement buildMeasurement(Arm a) {
		return buildMeasurement(a.getSize());
	}

	public BasicMeasurement buildMeasurement(int size) {
		return d_varType.buildMeasurement(size);
	}
	
	public BasicMeasurement buildMeasurement() {
		return d_varType.buildMeasurement();
	}
	
	public boolean deepEquals(Entity obj) {
		if (!equals(obj)) return false;
		
		AbstractVariable other = (AbstractVariable)obj;
		return EqualsUtil.equal(other.getDescription(), getDescription()) &&
			EqualsUtil.equal(other.getVariableType(), getVariableType());
	}
	
	public VariableType getVariableType() {
		return d_varType;
	}
	
	public void setVariableType(VariableType type) {
		VariableType oldValue = d_varType;
		d_varType = type;
		firePropertyChange(PROPERTY_VARIABLE_TYPE, oldValue, d_varType);
	}
	
	@SuppressWarnings("unchecked")
	private static List<Class<? extends Variable>> s_types = Arrays.<Class<? extends Variable>>asList(Endpoint.class, AdverseEvent.class, PopulationCharacteristic.class);
	
	@Override
	public int compareTo(Variable other) {
		if (other.getClass().equals(getClass())) {
			return super.compareTo(other);
		}
		return s_types.indexOf(getClass()) - s_types.indexOf(other.getClass());
	}
	

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
}
