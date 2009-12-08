package org.drugis.addis.presentation;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class DefaultListHolder<E> extends AbstractListHolder<E> {
	
	private List<E> d_list;

	public DefaultListHolder(List<E> list) {
		d_list = list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object newValue) {
		List<E> oldValue = d_list;
		d_list = (List<E>) newValue;
		fireValueChange(oldValue, newValue);
	}

	@Override
	public List<E> getValue() {
		return Collections.unmodifiableList(d_list);
	}
}
