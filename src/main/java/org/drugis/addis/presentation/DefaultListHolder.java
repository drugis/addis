package org.drugis.addis.presentation;

import java.util.List;

@SuppressWarnings("serial")
public class DefaultListHolder<E> extends AbstractListHolder<E> {
	
	private List<E> d_list;

	public DefaultListHolder(List<E> list) {
		d_list = list;
	}
	
	public void setValue(List<E> newValue) {
		List<E> oldValue = d_list;
		d_list = newValue;
		fireValueChange(oldValue, newValue);
	}

	@Override
	public List<E> getValue() {
		return d_list;
	}

}
