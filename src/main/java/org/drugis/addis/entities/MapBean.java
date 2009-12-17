package org.drugis.addis.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract public class MapBean<K, V> extends AbstractEntity implements Map<K, V>  {
	private static final long serialVersionUID = -8733867872254497765L;

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
		throw new RuntimeException("Not implemented");
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
}
