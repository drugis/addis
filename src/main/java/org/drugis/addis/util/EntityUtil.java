package org.drugis.addis.util;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.drugis.addis.entities.Activity;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Note;
import org.drugis.common.EqualsUtil;

public class EntityUtil {
	public static boolean deepEqual(Activity o1, Activity o2) {
		return o1 == null ? o2 == null : o1.deepEquals(o2);
	}

	public static boolean deepEqual(List<Note> o1, List<Note> o2) {
		return EqualsUtil.equal(o1, o2);
	}
	
	
	public static boolean deepEqual(Collection<Entity> o1, Collection<Entity> o2) {
		throw new NotImplementedException();
	}
}
