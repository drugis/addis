package org.drugis.addis.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ListDataListener;

import com.jgoodies.binding.list.ObservableList;

public class SortedSetModel<E> extends AbstractList<E> implements ObservableList<E> {
	private SortedSet<E> d_set = new TreeSet<E>();
	private ListDataListenerManager d_listenerManager = new ListDataListenerManager(this);

	public SortedSetModel() {
	}

	public SortedSetModel(Collection<? extends E> c) {
		d_set.addAll(c);
	}

	//// List<E> methods

	@Override
	public int size() {
		return d_set.size();
	}
	
	@Override
	public E get(int index) {
		int i = 0;
		for (E e : d_set) {
			if (i == index) {
				return e;
			}
			++i;
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public void add(int index, E element) {
		if (!d_set.contains(element)) {
			d_set.add(element);
			int idx = indexOf(element);
			d_listenerManager.fireIntervalAdded(idx, idx);
		}
	}

	@Override
    public E remove(int index) {
		if (index >= 0 && index < size()) {
			E e = get(index);
			d_set.remove(e);
			d_listenerManager.fireIntervalRemoved(index, index);
			return e;
		}
		throw new IndexOutOfBoundsException();
    }
	
	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index >= 0) {
			remove(index);
			return true;
		}
		return false;
	}
	
	//// ListModel methods
	@Override
	public int getSize() {
		return size();
	}

	@Override
	public Object getElementAt(int index) {
		return get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		d_listenerManager.addListDataListener(l);
	}
	
	@Override
	public void removeListDataListener(ListDataListener l) {
		d_listenerManager.removeListDataListener(l);
	}
	
	public SortedSet<E> getSet() {
		return new TreeSet<E>(d_set);
	}
}
