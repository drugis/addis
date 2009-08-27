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

import com.jgoodies.binding.beans.Model;

import static org.drugis.common.EqualsUtil.equal;

public class Dose extends Model {
	private static final long serialVersionUID = -8789524312421940513L;
	private SIUnit d_unit;
	private Double d_quantity;
	
	public static final String PROPERTY_UNIT = "unit";
	public static final String PROPERTY_QUANTITY = "quantity";
	
	protected Dose() {
		
	}
	
	public Dose(double quantity, SIUnit unit) {
		d_quantity = quantity;
		d_unit = unit;
	}
	
	public SIUnit getUnit() {
		return d_unit;
	}
	
	public void setUnit(SIUnit unit) {
		SIUnit oldVal = d_unit;
		d_unit = unit;
		firePropertyChange(PROPERTY_UNIT, oldVal, d_unit);
	}
	
	public Double getQuantity() {
		return d_quantity;
	}
	
	public void setQuantity(Double quantity) {
		Double oldVal = d_quantity;
		d_quantity = quantity;
		firePropertyChange(PROPERTY_QUANTITY, oldVal, d_quantity);
	}
	
	public String toString() {
		if (d_quantity == null || d_unit == null) {
			return "INCOMPLETE";
		}
		return d_quantity.toString() + " " + d_unit.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Dose) {
			Dose other = (Dose)o;
			return equal(other.getQuantity(), getQuantity()) &&
				equal(other.getUnit(), getUnit());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getQuantity().hashCode();
		hash = hash * 31 + getUnit().hashCode();
		return hash;
	}
}
