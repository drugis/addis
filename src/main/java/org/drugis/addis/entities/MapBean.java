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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract public class MapBean<K, V> extends AbstractEntity implements Map<K, V>  {

	public static final String PROPERTY_CONTENTS = "contents";
	
	private Map<K, V> d_map = new HashMap<K, V>();
	
	public MapBean() {
	}

	public void clear() {
		throw new RuntimeException("Keys are not mutable in this map");
	}

	public boolean containsKey(Object key) {
		return d_map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return d_map.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return d_map.entrySet();
	}

	public V get(Object key) {
		return d_map.get(key);
	}

	public boolean isEmpty() {
		return d_map.isEmpty();
	}

	public Set<K> keySet() {
		return d_map.keySet();
	}

	public V put(K key, V value) {
		V old = d_map.put(key, value);
		fireContentsChanged();
		return old;
	}

	private void fireContentsChanged() {
		firePropertyChange(PROPERTY_CONTENTS, null, null);
	}

	public void putAll(
			Map<? extends K, ? extends V> m) {
		d_map.putAll(m);
		fireContentsChanged();
	}

	public V remove(Object key) {
		throw new RuntimeException("Keys are not mutable in this map");
	}

	public int size() {
		return d_map.size();
	}

	public Collection<V> values() {
		return d_map.values();
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof MapBean) {
			MapBean other = (MapBean) obj;
			return d_map.equals(other.d_map);
		}
		return false;
	}
	
}
