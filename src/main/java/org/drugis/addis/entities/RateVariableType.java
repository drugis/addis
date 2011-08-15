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

import java.util.Collections;
import java.util.Set;

public class RateVariableType extends AbstractEntity implements VariableType {
	public BasicMeasurement buildMeasurement() {
		return new BasicRateMeasurement(null, null);
	}

	public BasicMeasurement buildMeasurement(int size) {
		return new BasicRateMeasurement(null, size);
	}

	public String getType() {
		return "Rate";
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof RateVariableType);
	}
	
	@Override
	public String toString() {
		return getType();
	}
}
