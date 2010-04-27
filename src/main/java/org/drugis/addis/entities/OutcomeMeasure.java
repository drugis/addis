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

package org.drugis.addis.entities;

import org.drugis.addis.util.EnumXMLFormat;


public interface OutcomeMeasure extends Variable {

	public final static String PROPERTY_DIRECTION = "direction";
	
	public enum Direction {
		HIGHER_IS_BETTER("Higher is better"),
		LOWER_IS_BETTER("Lower is better");
		
		String d_string;
		
		Direction() {
		}
		
		Direction(String s) {
			d_string = s;
		}
		
		public String toString() {
			return d_string;
		}
		
		static EnumXMLFormat<Direction> XML = new EnumXMLFormat<Direction>(Direction.class);
	}


	public void setType(Variable.Type type);

	public Variable.Type getType();

	public Direction getDirection();
	
	public BasicMeasurement buildMeasurement();
	
	public BasicMeasurement buildMeasurement(int size);
}