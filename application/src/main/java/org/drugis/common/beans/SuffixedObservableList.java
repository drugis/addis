package org.drugis.common.beans;

import com.jgoodies.binding.list.ObservableList;

public class SuffixedObservableList<E> extends AbstractObservableList<E> {
	private final ObservableList<E> d_nested;
	private final E[] d_suffix;

	public SuffixedObservableList(final ObservableList<E> list, final E ... suffix) {
		d_nested = list;
		d_suffix = suffix;
	}

	@Override
	public E get(final int index) {
		if (index < d_nested.size()) {
			return d_nested.get(index);
		}
		return d_suffix[index - d_nested.size()];
	}

	@Override
	public int size() {
		return d_nested.size() + d_suffix.length;
	}

}
