package org.drugis.addis.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;

import org.drugis.addis.presentation.ListHolder;

public class ListHolderWrapperPlsDel<T> extends AbstractListModel {
	private static final long serialVersionUID = 4520098614672215268L;

	private final ListHolder<T> d_holder;
	private int d_size;

	public ListHolderWrapperPlsDel(ListHolder<T> holder) {
		d_holder = holder;
		d_size = d_holder.getValue().size();
		d_holder.addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				fireChangeEvent();
			}
		});
	}
	
	protected void fireChangeEvent() {
		int newSize = d_holder.getValue().size();
		if (newSize > d_size) {
			fireIntervalAdded(this, d_size, newSize - 1);
		} else if (newSize < d_size) {
			fireIntervalRemoved(this, newSize, d_size - 1);
		}
		fireContentsChanged(this, 0, Math.min(d_size, newSize) - 1);
		d_size = newSize;
	}
	@Override
	public Object getElementAt(int index) {
		return d_holder.getValue().get(index);
	}

	@Override
	public int getSize() {
		return d_size;
	}
}
