package org.drugis.addis.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharacteristicsMap extends AbstractEntity implements Map<Characteristic, Object>  {
	private static final long serialVersionUID = -8733867872254497765L;

	public static final String PROPERTY_CONTENTS = "contents";
	
	private Map<Characteristic, Object> d_map = new HashMap<Characteristic, Object>();
	
	public CharacteristicsMap() {
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
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

	public Set<java.util.Map.Entry<Characteristic, Object>> entrySet() {
		return d_map.entrySet();
	}

	public Object get(Object key) {
		return d_map.get(key);
	}

	public boolean isEmpty() {
		return d_map.isEmpty();
	}

	public Set<Characteristic> keySet() {
		return d_map.keySet();
	}

	public Object put(Characteristic key, Object value) {
		Object old = d_map.put(key, value);
		fireContentsChanged();
		return old;
	}

	private void fireContentsChanged() {
		firePropertyChange(PROPERTY_CONTENTS, null, null);
	}

	public void putAll(
			Map<? extends Characteristic, ? extends Object> m) {
		throw new RuntimeException("Not implemented");
	}

	public Object remove(Object key) {
		throw new RuntimeException("Keys are not mutable in this map");
	}

	public int size() {
		return d_map.size();
	}

	public Collection<Object> values() {
		return d_map.values();
	}
}
