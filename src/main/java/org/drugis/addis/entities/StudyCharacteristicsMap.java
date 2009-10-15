package org.drugis.addis.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StudyCharacteristicsMap extends AbstractEntity implements Map<StudyCharacteristic, Object>  {
	private static final long serialVersionUID = -8733867872254497765L;

	public static final String PROPERTY_CONTENTS = "contents";
	
	private Map<StudyCharacteristic, Object> d_map = new HashMap<StudyCharacteristic, Object>();
	
	public StudyCharacteristicsMap() {
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			d_map.put(c, null);
		}
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

	public Set<java.util.Map.Entry<StudyCharacteristic, Object>> entrySet() {
		return d_map.entrySet();
	}

	public Object get(Object key) {
		return d_map.get(key);
	}

	public boolean isEmpty() {
		return d_map.isEmpty();
	}

	public Set<StudyCharacteristic> keySet() {
		return d_map.keySet();
	}

	public Object put(StudyCharacteristic key, Object value) {
		if (!key.getValueType().validate(value)) {
			throw new IllegalArgumentException("Illegal value " + value.getClass() + "(" + value 
					+ ") for the type of characteristic: " + this);
		}
		Object old = d_map.put(key, value);
		fireContentsChanged();
		return old;
	}

	private void fireContentsChanged() {
		firePropertyChange(PROPERTY_CONTENTS, null, null);
	}

	public void putAll(
			Map<? extends StudyCharacteristic, ? extends Object> m) {
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
