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

package org.drugis.addis.util;

import org.apache.commons.lang.math.DoubleRange;
import org.drugis.common.EqualsUtil;

public class BoundedInterval {
	private final DoubleRange d_range;
	private final boolean d_lowerBoundIsOpen;
	private final boolean d_upperBoundIsOpen;
	public static final double EPSILON = 1.0E-14;
	
	public BoundedInterval(DoubleRange range, boolean lowerBoundIsOpen, boolean upperBoundIsOpen) {
		d_range = range;
		d_lowerBoundIsOpen = lowerBoundIsOpen;
		d_upperBoundIsOpen = upperBoundIsOpen;
	}

	public BoundedInterval(double lowerBound, boolean lowerBoundIsOpen, double upperBound, boolean upperBoundIsOpen) {
		this(new DoubleRange(
				 lowerBound + (lowerBoundIsOpen ? BoundedInterval.EPSILON : 0), 
				 upperBound	- (upperBoundIsOpen ? BoundedInterval.EPSILON : 0)),
				 lowerBoundIsOpen,
				 upperBoundIsOpen);
	}

	public DoubleRange getRange() {
		return d_range;
	}

	public boolean isLowerBoundOpen() {
		return d_lowerBoundIsOpen;
	}

	public boolean isUpperBoundOpen() {
		return d_upperBoundIsOpen;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoundedInterval) {
			BoundedInterval other = (BoundedInterval) obj;
			return EqualsUtil.equal(d_range, other.d_range) &&
					d_lowerBoundIsOpen == other.d_lowerBoundIsOpen &&
					d_upperBoundIsOpen == other.d_upperBoundIsOpen;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_range.hashCode() + 31 * (d_lowerBoundIsOpen ? 1 : 0) + 31 * 31 * (d_upperBoundIsOpen ? 1 : 0);
	}
	
}