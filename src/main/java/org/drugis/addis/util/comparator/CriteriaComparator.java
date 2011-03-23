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

package org.drugis.addis.util.comparator;

import java.util.Comparator;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;

/**
 * Sorts criteria so that HIGHER_IS_BETTER are put before LOWER_IS_BETTER.
 */
public class CriteriaComparator implements Comparator<OutcomeMeasure> {

	public int compare(OutcomeMeasure x0, OutcomeMeasure x1) {
		if (x0.getDirection() == x1.getDirection()) {
			return x0.getName().compareTo(x1.getName());
		}
		if (x0.getDirection() == Direction.HIGHER_IS_BETTER) {
			return -1;
		} else {
			return 1;
		}
	}

}
