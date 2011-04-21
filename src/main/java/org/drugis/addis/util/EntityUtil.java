package org.drugis.addis.util;

import java.util.List;

import org.drugis.addis.entities.Entity;

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
}
