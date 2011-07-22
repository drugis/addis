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

package org.drugis.addis.entities;

import org.drugis.common.EqualsUtil;



public abstract class OutcomeMeasure extends AbstractVariable {
	public enum Direction {
		HIGHER_IS_BETTER("Higher is better"),
		LOWER_IS_BETTER("Lower is better");
		
		String d_string;
		
		Direction() {
		}
		
		Direction(String s) {
			d_string = s;
		}
		
		@Override
		public String toString() {
			return d_string;
		}
	}
	
	public final static String PROPERTY_DIRECTION = "direction";

	protected Direction d_direction;

	protected OutcomeMeasure(String name, VariableType type) {
		super(name, type);
	}

	public void setDirection(Direction dir) {
		Direction oldVal = d_direction;
		d_direction = dir;
		firePropertyChange(PROPERTY_DIRECTION, oldVal, d_direction);
	}

	public Direction getDirection() {
		return d_direction;
	}

	@Override
	public boolean deepEquals(Entity obj) {
		if (!super.deepEquals(obj)) return false;
		
		OutcomeMeasure other = (OutcomeMeasure) obj;
		return EqualsUtil.equal(other.getDirection(), getDirection());
	}
}