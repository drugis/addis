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

package org.drugis.addis.plot;

import java.awt.geom.Line2D;

public class Line extends Line2D.Double {
	private static final long serialVersionUID = -6107006633910258400L;

	public Line(double d, double e, double f, double g) {
		super(d, e, f, g);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Line2D) {
			return ((Line2D) o).getP1().equals(getP1()) && ((Line2D) o).getP2().equals(getP2());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Line(x1 = " + getX1() + ", y1 = " + getY1() +
			", x2 = " + getX2() + ", y2 = " + getY2() + ")";
	}
}
