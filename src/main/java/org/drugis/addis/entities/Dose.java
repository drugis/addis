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

import static org.drugis.common.EqualsUtil.equal;

import java.util.Set;

import org.drugis.common.Interval;

public class Dose extends AbstractEntity {
	private static final long serialVersionUID = -8789524312421940513L;
	private SIUnit d_unit;
	private Double d_quantity;
	private Interval<Double> d_flexDose;
	private final boolean d_isFlexibleDose;
	
	public static final String PROPERTY_UNIT = "unit";
	public static final String PROPERTY_QUANTITY = "quantity";
	public static final String PROPERTY_FLEXIBLEDOSE = "flexibleDose";
	
	protected Dose() {
		d_isFlexibleDose = false;
	}
	
	public Dose(double quantity, SIUnit unit) {
		d_quantity = quantity;
		d_unit = unit;
		d_isFlexibleDose = false;
	}
	
	public Dose(Interval<Double> flexDose, SIUnit unit) {
		d_flexDose = flexDose;
		d_unit = unit;
		d_isFlexibleDose = true;
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
		if (isFlexible())
			throw new IllegalArgumentException("Current dose is flexible");
		return d_quantity;
	}
	
	public void setQuantity(Double quantity) {
		if (isFlexible())
			throw new IllegalArgumentException("Current dose is flexible");
		Double oldVal = d_quantity;
		d_quantity = quantity;
		firePropertyChange(PROPERTY_QUANTITY, oldVal, d_quantity);
	}
	
	public Interval<Double> getFlexibleDose() {
		if (!isFlexible())
			throw new IllegalArgumentException("Current dose is not flexible");
		return d_flexDose;
	}
	
	public void setFlexibleDose(Interval<Double> flexdose) {
		if (!isFlexible())
			throw new IllegalArgumentException("Current dose is not flexible");
		Interval<Double> oldVal = d_flexDose;
		d_flexDose = flexdose;
		firePropertyChange(PROPERTY_FLEXIBLEDOSE, oldVal, d_flexDose);
	}

	public boolean isFlexible() {
		return d_isFlexibleDose;
	}
	
	public String toString() {
		if ((d_quantity == null && d_flexDose == null) || d_unit == null) {
			return "INCOMPLETE";
		}
		return isFlexible() ? d_flexDose.toString() + " " + d_unit.toString() : d_quantity.toString() + " " + d_unit.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Dose) {
			Dose other = (Dose)o;
			if (isFlexible())
				return equal(other.getFlexibleDose(), getFlexibleDose()) && equal(other.getUnit(), getUnit());
			else
				return equal(other.getQuantity(), getQuantity()) &&
					equal(other.getUnit(), getUnit());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 31; 
		hash += isFlexible() ? getFlexibleDose().hashCode() : getQuantity().hashCode();
		hash = hash * 31 + getUnit().hashCode();
		return hash;
	}

	public Set<Entity> getDependencies() {
		return null;
	}
}
