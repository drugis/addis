/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

/**
 * Implements a named entity that has equality and natural order defined based on the name.
 * @param <T> What the subclass should be comparable to.
 */
public abstract class AbstractNamedEntity<T extends TypeWithName> extends AbstractEntity implements Comparable<T>, TypeWithName {
	protected String d_name = "";

	public AbstractNamedEntity(String name) {
		d_name = name;
	}
	
	public String getName() {
		return d_name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getName() + ")";
	}
	
	/**
	 * Extenders should override equals to check the type of the other.
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof AbstractNamedEntity<?>) {
			AbstractNamedEntity<?> other = (AbstractNamedEntity<?>) o;
			return EqualsUtil.equal(other.getName(), getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName() == null ? 0 : getName().hashCode();
	}

	public int compareTo(T other) {
		if (other == null) {
			return 1;
		}
		return getName().compareTo(other.getName());
	}
	
	@Override
	public String getLabel() {
		return getName();
	}
}
