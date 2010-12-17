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

package org.drugis.addis.forestplot;

/**
 * A BinnedScale maps a real value x to a bin n in some integer range [nMin, nMax].
 * If x would map outside [nMin, nMax], out-of-bounds is returned.
 */
public class BinnedScale {
	public static class Bin {
		public boolean outOfBoundsMin = false;
		public boolean outOfBoundsMax = false;
		public Integer bin = 0;
	}
	
	private int d_min;
	private int d_max;
	private Scale d_scale;

	public BinnedScale(Scale scale, int nMin, int nMax) {
		d_min = nMin;
		d_max = nMax;
		d_scale = scale;
	}
	
	public Bin getBin(double x) {
		Bin b = new Bin();
		b.bin = (int) Math.round(d_scale.getNormalized(x) * (d_max - d_min) + d_min);
		
		if (b.bin > d_max) {
			b.bin = d_max;
			b.outOfBoundsMax = true;
		}
		if (b.bin < d_min) {
			b.bin = d_min;
			b.outOfBoundsMin = true;
		}
		
		return b;
	}
	
	public int getMin() {
		return d_min;
	}
	
	public int getMax() {
		return d_max;
	}
}
