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

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Set;

public enum PredefinedActivity implements Activity {
	RANDOMIZATION("Randomization"),
	SCREENING("Screening"),
	WASH_OUT("Wash out"),
	FOLLOW_UP("Follow up");

	private final String d_description;

	PredefinedActivity(String description) {
		d_description = description;
	}
	
	public String getLabel() {
		return d_description;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	/**
	 * Deep equality and shallow equality are equivalent for this type.
	 */
	public boolean deepEquals(Entity other) {
		return equals(other);
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {}
	public void removePropertyChangeListener(PropertyChangeListener listener) {}
	
}
