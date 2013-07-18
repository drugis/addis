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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.entities.treatment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DecisionTreeEdgeComparator implements Comparator<DecisionTreeEdge> {
	private static final List<Class<?>> s_types = Arrays.<Class<?>>asList(
		TypeEdge.class,
		RangeEdge.class
	);

	@Override
	public int compare(final DecisionTreeEdge o1, final DecisionTreeEdge o2) {
		final int i1 = s_types.indexOf(o1.getClass());
		final int i2 = s_types.indexOf(o2.getClass());
		if (i1 == i2 && o1 instanceof RangeEdge) {
			return ((RangeEdge)o1).compareTo((RangeEdge)o2);
		} else if (i1 == i2 && o1 instanceof TypeEdge) { 
			return ((TypeEdge)o1).getType().toString().compareTo(((TypeEdge)o2).getType().toString());
		}
		return i2 - i1;
	}
}