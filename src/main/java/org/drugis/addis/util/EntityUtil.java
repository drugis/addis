package org.drugis.addis.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.drugis.addis.entities.Entity;
import org.drugis.common.EqualsUtil;

public class EntityUtil {
	public static boolean deepEqual(Entity o1, Entity o2) {
		return o1 == null ? o2 == null : o1.deepEquals(o2);
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
}
