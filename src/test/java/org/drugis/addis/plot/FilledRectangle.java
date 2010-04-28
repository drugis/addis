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

package org.drugis.addis.plot;

import java.awt.Color;
import java.awt.Rectangle;

public class FilledRectangle extends Rectangle {
	private static final long serialVersionUID = 2895176337497405740L;
	
	private Color d_color;
	
	FilledRectangle(int x, int y, int width, int height, Color color) {
		super(x,y,width,height);
		d_color = color;
	}
	
	FilledRectangle(int x, int y, int width, int height) {
		super(x,y,width,height);
		d_color = Color.BLACK;
	}
	
	FilledRectangle(Rectangle r) {
		this(r.x, r.y, r.width, r.height);
	}
	
	FilledRectangle(Rectangle r, Color c) {
		this(r.x, r.y, r.width, r.height, c);
	}

	public boolean equals(Object o) {
		if (o instanceof FilledRectangle) {
			FilledRectangle other = (FilledRectangle) o;
			return super.equals(other) && other.d_color.equals(d_color);
		}			
		return false;
	}
	
	public String toString() {
		return "FilledRectangle(x = " + x + ", y = " + y +
			", width = " + width + ", height = " + height +
			", color = " + d_color + ")";
	}
}
