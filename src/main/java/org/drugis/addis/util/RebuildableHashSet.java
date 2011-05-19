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

package org.drugis.addis.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RebuildableHashSet<T> implements Set<T> {
	Set<T> d_nested = new HashSet<T>();
	
	public RebuildableHashSet() {
	}
	
	public RebuildableHashSet(Collection<? extends T> c) {
		d_nested = new HashSet<T>(c);
	}

	public void rebuild() {
		d_nested = new HashSet<T>(d_nested);
	}

	public boolean add(T e) {
		return d_nested.add(e);
	}

	public boolean addAll(Collection<? extends T> c) {
		return d_nested.addAll(c);
	}

	public void clear() {
		d_nested.clear();
	}

	public boolean contains(Object o) {
		return d_nested.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return d_nested.containsAll(c);
	}

	public boolean isEmpty() {
		return d_nested.isEmpty();
	}

	public Iterator<T> iterator() {
		return d_nested.iterator();
	}

	public boolean remove(Object o) {
		return d_nested.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return d_nested.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return d_nested.retainAll(c);
	}

	public int size() {
		return d_nested.size();
	}

	public Object[] toArray() {
		return d_nested.toArray();
	}

	public <U> U[] toArray(U[] a) {
		return d_nested.toArray(a);
	}
	
	@Override
	public boolean equals(Object obj) {
		return d_nested.equals(obj);
	}

	@Override
	public int hashCode() {
		return d_nested.hashCode();
	}
}