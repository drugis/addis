package org.drugis.addis.presentation;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

public abstract class AbstractListValidator<E> extends AbstractValueModel implements ValueHolder<Boolean> {
	private static final long serialVersionUID = 391045513777444696L;
	protected final ObservableList<E> d_list;
	private boolean d_value;
	
	public AbstractListValidator(ObservableList<E> list) {
		d_list = list;
		d_list.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				update();
			}
			public void intervalAdded(ListDataEvent e) {
				update();
			}
			public void contentsChanged(ListDataEvent e) {
				update();
			}
		});
		update();
	}
	
	private void update() {
		boolean oldValue = d_value;
		d_value = validate();
		fireValueChange(oldValue, d_value);
	}
	
	public abstract boolean validate();
	
	@Override
	public Boolean getValue() {
		return d_value;
	}
	
	@Override
	public void setValue(Object newValue) {
		throw new IllegalAccessError("ListValidator is read-only");
	}
}