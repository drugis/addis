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

package org.drugis.addis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.TypeWithName;
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
			if (actualKey == null) return false;
			if (!deepEqual(entry.getValue(), o2.get(actualKey)))
				return false;
		}
		return true;
	}

	/**
	 * Find a key in a set using Object.equals or by using Object.shallowEquals(Object o) using reflection
	 * Object.shallowEquals may be implemented to use a different equals criteria and is invoked after Object.equals
	 * @param key the key to find
	 * @param keySet the set to find key
	 * @return null or the element in the keySet
	 */
	private static Object findMatchingKey(Object key, Set<?> keySet) {
		for (Object otherKey : keySet) {
			if (EqualsUtil.equal(key, otherKey)) {
				return otherKey;
			}
			java.lang.reflect.Method shallowEquals;
			try {
				shallowEquals = key.getClass().getMethod("deepEquals", Object.class);
				boolean shallowEqual = ((Boolean)shallowEquals.invoke(key, otherKey));
				if (shallowEqual) return otherKey;
			} catch (NoSuchMethodException e) {
				continue;
			} catch (Exception e) {
				throw new RuntimeException("Reflection failed for shallowEquals on: " + e);
			}
		}
		return null;
	}

	/**
	 * Flattens a Collection of Entities by dynamically calling getContents() on its members
	 * @return a Collection of type T containing all the elements resulting from getContents()
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity> Collection<T> flatten(Collection<? extends Entity> set) {
		HashSet<T> flat = new HashSet<T>();
		for (Entity nested : set) {
			java.lang.reflect.Method getContents;
			try {
				getContents = nested.getClass().getMethod("getContents");
				flat.addAll((Collection<T>)getContents.invoke(nested));
			} catch (NoSuchMethodException e) {
				continue;
			} catch (Exception e) {
				throw new RuntimeException("Reflection failed for getContents on: " + e);
			}
		}
		return flat;
	}

	/**
	 * Add a collection of entities and their dependencies to the set of dependencies.
	 * @param dependencies Dependencies to add to.
	 * @param entities Entities to add recursively.
	 */
	public static void addRecursiveDependencies(Set<Entity> dependencies, Collection<? extends Entity> entities) {
		dependencies.addAll(entities);
		for (Entity e : entities) {
			dependencies.addAll(e.getDependencies());
		}
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

	public static <T extends TypeWithName> T findByName(Collection<T> haystack, String needle) {
		for (T o : haystack) {
			if (needle.equals(o.getName())) {
				return o;
			}
		}
		return null;
	}
}
