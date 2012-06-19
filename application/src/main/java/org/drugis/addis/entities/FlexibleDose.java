/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.drugis.common.EqualsUtil;
import org.drugis.common.Interval;

public class FlexibleDose extends AbstractDose {
	private Interval<Double> d_flexDose;
	
	public static final String PROPERTY_FLEXIBLEDOSE = "flexibleDose";
	public static final String PROPERTY_MIN_DOSE = "minDose";
	public static final String PROPERTY_MAX_DOSE = "maxDose";
	
	
	public FlexibleDose(){
		d_flexDose = new Interval<Double>(0.,0.);
	}
	
	public FlexibleDose(Interval<Double> flexDose, DoseUnit doseUnit) {
		if (flexDose.getLowerBound() > flexDose.getUpperBound()) {
			throw new IllegalArgumentException("Dose bounds illegal");
		}
		d_flexDose = flexDose;
		d_unit = doseUnit;
	}
	
	public Interval<Double> getFlexibleDose() {
		return d_flexDose;
	}
	
	public Double getMinDose() {
		return d_flexDose.getLowerBound();
	}
	
	public Double getMaxDose() {
		return d_flexDose.getUpperBound();
	}
	
	public void setFlexibleDose(Interval<Double> flexdose) {
		Interval<Double> oldVal = d_flexDose;
		d_flexDose = flexdose;
		firePropertyChange(PROPERTY_FLEXIBLEDOSE, oldVal, d_flexDose);
	}
	
	public void setMinDose(Double d) {
		Interval<Double> oldVal = d_flexDose;
		d_flexDose = new Interval<Double>(d, d > oldVal.getUpperBound() ? d : oldVal.getUpperBound());
		firePropertyChange(PROPERTY_FLEXIBLEDOSE, oldVal, d_flexDose);
		firePropertyChange(PROPERTY_MIN_DOSE, oldVal.getLowerBound(), d_flexDose.getLowerBound());
		firePropertyChange(PROPERTY_MAX_DOSE, oldVal.getUpperBound(), d_flexDose.getUpperBound());
	}
	
	public void setMaxDose(Double d) {
		Interval<Double> oldVal = d_flexDose;
		d_flexDose = new Interval<Double>(d < oldVal.getLowerBound() ? d : oldVal.getLowerBound(), d);
		firePropertyChange(PROPERTY_FLEXIBLEDOSE, oldVal, d_flexDose);
		firePropertyChange(PROPERTY_MIN_DOSE, oldVal.getLowerBound(), d_flexDose.getLowerBound());
		firePropertyChange(PROPERTY_MAX_DOSE, oldVal.getUpperBound(), d_flexDose.getUpperBound());
	}

	@Override
	public String toString() {
		if (d_flexDose == null || d_unit == null) {
			return "INCOMPLETE";
		}
		return d_flexDose.toString() + " " + d_unit.getLabel();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FlexibleDose) {
			FlexibleDose other = (FlexibleDose)o;
			return EqualsUtil.equal(other.getFlexibleDose(), getFlexibleDose()) && EqualsUtil.equal(other.getDoseUnit(), getDoseUnit());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 31; 
		hash += getFlexibleDose().hashCode();
		hash = hash * 31 + getDoseUnit().hashCode();
		return hash;
	}

	@Override
	public AbstractDose clone() {
		return new FlexibleDose(getFlexibleDose(), getDoseUnit().clone());
	}
}
