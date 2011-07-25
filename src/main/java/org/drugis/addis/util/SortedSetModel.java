package org.drugis.addis.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.jgoodies.binding.list.ObservableList;

public class SortedSetModel<E> extends AbstractList<E> implements ObservableList<E> {
	private SortedSet<E> d_set = new TreeSet<E>();
	private List<ListDataListener> d_listeners = new ArrayList<ListDataListener>();

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
			fireIntervalAdded(idx, idx);
		}
	}

	@Override
    public E remove(int index) {
		if (index >= 0 && index < size()) {
			E e = get(index);
			d_set.remove(e);
			fireIntervalRemoved(index, index);
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
		d_listeners.add(l);
	}
	
	@Override
	public void removeListDataListener(ListDataListener l) {
		d_listeners.remove(l);
	}
	
	private void fireIntervalAdded(int index0, int index1) {
		ListDataEvent evt = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
		for (ListDataListener l : d_listeners) {
			l.intervalAdded(evt);
		}
	}
	
	private void fireIntervalRemoved(int index0, int index1) {
		ListDataEvent evt = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
		for (ListDataListener l : d_listeners) {
			l.intervalRemoved(evt);
		}
	}

	public SortedSet<E> getSet() {
		return new TreeSet<E>(d_set);
	}
}
