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

package org.drugis.addis.util.comparator;

import java.util.Comparator;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;

public class AlphabeticalComparator implements Comparator<Object>  {
	public int compare(Object o1, Object o2) {
		if (o1 instanceof OutcomeMeasure)
			return compareOm((OutcomeMeasure)o1, (OutcomeMeasure)o2);
		return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
	}

	public int compareOm(OutcomeMeasure o1, OutcomeMeasure o2) {
		if (o1.getClass().equals(o2.getClass()))
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		else 
			return o1 instanceof Endpoint ? -1 : 1;
	}
}