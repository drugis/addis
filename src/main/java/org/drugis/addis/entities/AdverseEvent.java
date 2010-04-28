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

public class AdverseEvent extends AbstractVariable implements OutcomeMeasure {
	private static final long serialVersionUID = -1026622949185265860L;

	public AdverseEvent() {
		super("", Type.RATE);
	}
	
	public AdverseEvent(String name, Variable.Type type) {
		super(name, type);
	}

	public Direction getDirection() {
		return Direction.LOWER_IS_BETTER;
	}
	
	public void setDirection(Direction dir) {
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AdverseEvent) {
			return super.equals(o);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (d_name != null) {
			return d_name.hashCode() + 7;
		}
		return 0;
	}
}
