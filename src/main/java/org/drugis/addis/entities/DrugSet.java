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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.util.EntityUtil;

public class DrugSet extends AbstractEntity implements Comparable<DrugSet> {
	private SortedSet<Drug> d_contents;

	public DrugSet(Collection<Drug> contents) {
		d_contents = new TreeSet<Drug>(contents);
	}
	
	public DrugSet(Drug drug) {
		this(Collections.singleton(drug));
	}
	
	public DrugSet() {
		this(Collections.<Drug>emptySet());
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return getContents();
	}

	public SortedSet<Drug> getContents() {
		return d_contents;
	}
	
	public String getDescription() {
		return StringUtils.join(d_contents, " + ");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof DrugSet)) {
			return false;
		}
		DrugSet other = (DrugSet) o;
		return other.getContents().equals(getContents());
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if(!equals(other)) {
			return false;
		}
		DrugSet ds = (DrugSet) other;
		return EntityUtil.deepEqual(getContents(), ds.getContents());
	}
	
	@Override
	public int hashCode() {
		return d_contents.hashCode();
	}

	@Override
	public int compareTo(DrugSet o) {
		Iterator<Drug> i1 = getContents().iterator();
		Iterator<Drug> i2 = o.getContents().iterator();
		while (i1.hasNext() && i2.hasNext()) {
			int compVal = i1.next().compareTo(i2.next());
			if (compVal != 0) {
				return compVal;
			}
		}
		if (i1.hasNext()) {
			return 1;
		}
		if (i2.hasNext()) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return "DrugSet" + d_contents;
	}
}