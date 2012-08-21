/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.entities.treatment;

import org.drugis.common.gui.GUIHelper;

public class TypeEdge implements DecisionTreeEdge {
	private final Class<?> d_type;

	public TypeEdge(final Class<?> type) {
		d_type = type;
	}

	@Override
	public boolean decide(final Object object) {
		return object == getType();
	}

	@Override
	public String toString() {
		return GUIHelper.humanize(getType().getSimpleName().replace("Dose", ""));
	}

	public Class<?> getType() {
		return d_type;
	}

	@Override
	public boolean equivalent(DecisionTreeEdge o) {
		if(!(o instanceof TypeEdge)) { 
			return false;
		} else {
			return getType().equals((TypeEdge)o);
		}
	}
}
