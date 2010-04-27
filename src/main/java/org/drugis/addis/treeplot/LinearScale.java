/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.treeplot;

import org.drugis.common.Interval;

public class LinearScale implements Scale {
	
	Interval<Double> d_in;

	public LinearScale(Interval<Double> interval) {
		d_in = interval;
	}
	
	public double getMax() {
		return d_in.getUpperBound();
	}

	public double getMin() {
		return d_in.getLowerBound();
	}

	public double getNormalized(double x) {
		return (x - getMin()) / (getMax() - getMin());
	}

}
