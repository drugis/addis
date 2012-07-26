package org.drugis.common.beans;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.common.beans.TransformedObservableList.Transform;
import org.drugis.common.event.ListDataEventProxy;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class TransformOnceObservableList<A, B> extends AbstractObservableList<B> {
	private final ObservableList<? extends A> d_nested;
	private final ObservableList<B> d_transformed;
	private final Transform<A, B> d_transform;

	public TransformOnceObservableList(final ObservableList<? extends A> list, final Transform<A, B> transform) {
		d_nested = list;
		d_transform = transform;
		d_transformed = new ArrayListModel<B>();
		for (final A a : d_nested) {
			d_transformed.add(d_transform.transform(a));
		}
		d_nested.addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(final ListDataEvent evt) {
				for (int i = evt.getIndex1(); i >= evt.getIndex0(); --i) {
					d_transformed.remove(i);
				}
			}
			@Override
			public void intervalAdded(final ListDataEvent evt) {
				for (int i = evt.getIndex0(); i <= evt.getIndex1(); ++i) {
					d_transformed.add(i, d_transform.transform(d_nested.get(i)));
				}
			}
			@Override
			public void contentsChanged(final ListDataEvent evt) {
				for (int i = evt.getIndex0(); i <= evt.getIndex1(); ++i) {
					d_transformed.set(i, d_transform.transform(d_nested.get(i)));
				}
			}
		});
		d_transformed.addListDataListener(new ListDataEventProxy(d_manager));
	}

	@Override
	public B get(final int index) {
		return d_transformed.get(index);
	}

	@Override
	public int size() {
		return d_transformed.size();
	}
}
