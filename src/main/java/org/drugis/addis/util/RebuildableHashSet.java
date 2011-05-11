/**
 * 
 */
package org.drugis.addis.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class RebuildableHashSet<T> implements Set<T> {
	Set<T> d_nested = new HashSet<T>();
	
	public RebuildableHashSet() {
	}
	
	public void rebuild() {
		d_nested = new HashSet<T>(d_nested);
	}

	public boolean add(T e) {
		return d_nested.add(e);
	}

	public boolean addAll(Collection<? extends T> c) {
		return d_nested.addAll(c);
	}

	public void clear() {
		d_nested.clear();
	}

	public boolean contains(Object o) {
		return d_nested.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return d_nested.containsAll(c);
	}

	public boolean isEmpty() {
		return d_nested.isEmpty();
	}

	public Iterator<T> iterator() {
		return d_nested.iterator();
	}

	public boolean remove(Object o) {
		return d_nested.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return d_nested.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return d_nested.retainAll(c);
	}

	public int size() {
		return d_nested.size();
	}

	public Object[] toArray() {
		return d_nested.toArray();
	}

	public <U> U[] toArray(U[] a) {
		return d_nested.toArray(a);
	}
}