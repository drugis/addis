/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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


public class Endpoint extends AbstractOutcomeMeasure {
	private static final long serialVersionUID = -1182348850033782011L;
	
	private Direction d_direction;
	
	public Endpoint(String name, Type type, Direction direction) {
		super(name, type);
		d_direction = direction;
	}
	
	public Endpoint(String string, Type type) {
		this(string, type, Direction.HIGHER_IS_BETTER);
	}
	
	public BasicMeasurement buildMeasurement(Arm pg) {
		switch (getType()) {
		case CONTINUOUS:
			return new BasicContinuousMeasurement(0.0, 0.0, pg.getSize());
		case RATE:
			return new BasicRateMeasurement(0, pg.getSize());
		default:
			throw new IllegalStateException("Not all enum cases covered");
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Endpoint) {
			return super.equals(o);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (d_name != null) {
			return d_name.hashCode();
		}
		return 0;
	}
	
	public void setDirection(Direction dir) {
		Direction oldVal = d_direction;
		d_direction = dir;
		firePropertyChange(PROPERTY_DIRECTION, oldVal, d_direction);
	}
	
	public Direction getDirection() {
		return d_direction;
	}
}
