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

public class Indication extends AbstractNamedEntity<Indication> implements Comparable<Indication>, TypeWithName {
	/**
	 * SNOMED CT code is defined as a 64-bit int.
	 */
	private Long d_code;
	
	public static final String PROPERTY_CODE = "code";
	
	public Indication(Long code, String name) {
		super(name);
		d_code = code;
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void setCode(Long code) {
		Long oldVal = d_code;
		d_code = code;
		firePropertyChange(PROPERTY_CODE, oldVal, d_code);
	}

	public Long getCode() {
		return d_code;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Indication) {
			return super.equals(o);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public String getLabel() {
		return d_code.toString() + " " + getName();
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if(!equals(other)) {
			return false;
		}
		Indication o = (Indication) other;
		return getCode().equals(o.getCode()) && getName().equals(o.getName());
	}
}