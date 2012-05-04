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

package org.drugis.addis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Entity;
import org.drugis.common.EqualsUtil;

public class EntityUtil {
	public static boolean deepEqual(Entity o1, Entity o2) {
		return o1 == null ? o2 == null : o1.deepEquals(o2);
	}

	public static boolean deepEqual(Collection<? extends Entity> o1, Collection<? extends Entity> o2) {
		return deepEqual(new ArrayList<Entity>(o1), new ArrayList<Entity>(o2));
	}
	
	public static boolean deepEqual(List<? extends Entity> o1, List<? extends Entity> o2) {
		if (o1.size() == o2.size()) {
			for (int i = 0; i < o1.size(); ++i) {
				if (!EntityUtil.deepEqual(o1.get(i), o2.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Assumes that the key in <key, value> pairs is an Object to which a regular equals() is applicable.
	 */
	public static boolean deepEqual(Map<?, ? extends Entity> o1, Map<?, ? extends Entity> o2) {
		if (o1.keySet().size() != o2.keySet().size()) return false;
		for (Entry<?,? extends Entity> entry : o1.entrySet()) {
			Object actualKey = findMatchingKey(entry.getKey(), o2.keySet());
			if (!deepEqual(entry.getValue(), o2.get(actualKey)))
				return false;
		}
		return true;
	}
	
	private static Object findMatchingKey(Object key, Set<?> keySet) {
		for (Object otherKey : keySet) {
			if (EqualsUtil.equal(key, otherKey)) {
				return otherKey;
			}
		}
		return null;
	}

	public static HashSet<Entity> flatten(Collection<DrugSet> set) {
		HashSet<Entity> flat = new HashSet<Entity>();
		for (DrugSet nested : set) {
			flat.addAll(nested.getContents());
		}
		return flat;
	}

	public static Duration createDuration(String durationStr) {
		try {
			return DatatypeFactory.newInstance().newDuration(durationStr);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Return a concrete super-type of the given type, or Entity.class.
	 * If the given class is not an interface, return the given class itself.
	 * If the given type is an interface, return Entity.class if the interface is a sub-type of Entity, and Object.class otherwise.
	 * @param type The class to convert.
	 * @return The concrete type.
	 */
	public static Class<?> getConcreteTypeOrEntity(Class<?> type) {
		if (type.isPrimitive()) {
			if (type == boolean.class) {
				return Boolean.class;
			}
			if (type == byte.class) {
				return Byte.class;
			}
			if (type == char.class) {
				return Character.class;
			}
			if (type == short.class) {
				return Short.class;
			}
			if (type == int.class) {
				return Integer.class;
			}
			if (type == long.class) {
				return Long.class;
			}
			if (type == float.class) {
				return Float.class;
			}
			if (type == double.class) {
				return Double.class;
			}
		}
		if (type.isInterface()) {
			return Entity.class.isAssignableFrom(type) ? Entity.class : Object.class;
		}
		return type;
	}
}
