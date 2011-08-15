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

public class ContinuousVariableType extends AbstractEntity implements VariableType {	
	public static final String PROPERTY_UNIT_OF_MEASUREMENT = "unitOfMeasurement";
	private String d_uom;
	
	public ContinuousVariableType() {
		this(Variable.UOM_DEFAULT_CONTINUOUS);
	}
	
	public ContinuousVariableType(String unitOfMeasurement) {
		d_uom = unitOfMeasurement;
	}

	public String getType() {
		return "Continuous";
	}
	
	public String getUnitOfMeasurement() {
		return d_uom;
	}
	
	public void setUnitOfMeasurement(String unit) {
		String oldValue = d_uom;
		d_uom = unit;
		firePropertyChange(PROPERTY_UNIT_OF_MEASUREMENT, oldValue, d_uom);
	}

	public BasicMeasurement buildMeasurement() {
		return new BasicContinuousMeasurement(null, null, null);
	}

	public BasicMeasurement buildMeasurement(int size) {
		return new BasicContinuousMeasurement(null, null, size);
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof ContinuousVariableType) {
			ContinuousVariableType other = (ContinuousVariableType) o;
			return EqualsUtil.equal(d_uom, other.d_uom);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_uom != null ? d_uom.hashCode() : 0;
	}
	
	@Override
	public String toString() {
		return getType();
	}
}