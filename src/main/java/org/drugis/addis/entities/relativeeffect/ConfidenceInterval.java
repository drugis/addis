/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.entities.relativeeffect;

import java.text.DecimalFormat;

import org.drugis.common.Interval;

public class ConfidenceInterval extends Interval<Double> {

	private final Double d_pointEstimate;

	public ConfidenceInterval(Double pointEstimate, Double lowerBound, Double upperBound) {
		super(lowerBound, upperBound);
		d_pointEstimate = pointEstimate;
	}

	public Double getPointEstimate() {
		return d_pointEstimate;
	}
	
	@Override
	protected boolean canEqual(Interval<?> other) {
		if (other.getClass().equals(ConfidenceInterval.class)) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ConfidenceInterval) {
			ConfidenceInterval other = (ConfidenceInterval) o;
			if (other.canEqual(this)) {
				return other.d_pointEstimate.equals(d_pointEstimate) && other.getLowerBound().equals(getLowerBound()) &&
					other.getUpperBound().equals(getUpperBound());
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() * 31 + d_pointEstimate.hashCode();
	}

	@Override
	public String toString() {
		DecimalFormat format = new DecimalFormat("###0.00");
		return format.format(getPointEstimate()) + " (" + format.format(getLowerBound()) + ", " + 
			format.format(getUpperBound()) + ")";
	}
}
