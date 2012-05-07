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

package org.drugis.addis.forestplot;

import org.drugis.common.Interval;

public abstract class ScaleBase implements Scale {

	protected final Interval<Double> d_in;

	public ScaleBase(Interval<Double> interval) {
		d_in = interval;
	}

	public double getMax() {
		return d_in.getUpperBound();
	}

	public double getMin() {
		return d_in.getLowerBound();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getMin() + ", " + getMax() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScaleBase) {
			ScaleBase other = (ScaleBase)obj;
			return canEqual(other) && d_in.equals(other.d_in);
		}
		return false;
	}

	protected abstract boolean canEqual(ScaleBase other);
	
	@Override
	public int hashCode() {
		return d_in.hashCode();
	}

}
