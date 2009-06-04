package nl.rug.escher.common;

import java.util.Iterator;
import java.util.SortedSet;

public class CollectionUtil {

	public static <E> E getElementAtIndex(SortedSet<E> set, int idx) {
		if (idx >= set.size() || idx < 0) {
			throw new IndexOutOfBoundsException();
		}
		Iterator<E> it = set.iterator();
		for (int i = 0; i < idx; ++i) {
			it.next();
		}
		return it.next();
	}

	public static <E> int getIndexOfElement(SortedSet<E> set, Object child) {
		int i = 0;
		for (E e : set) {
			if (e.equals(child)) {
				return i;
			}
			++i;
		}
		return -1;
	}

}
