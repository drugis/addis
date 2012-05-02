package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

/**
 * Wrap a ValueModel to conform to the typed ValueHolder<T> interface.
 * Does NOT make the ValueModel type safe. 
 */
public class ValueModelWrapper<T> extends AbstractValueModel implements ValueHolder<T> {
	private static final long serialVersionUID = 1485871079580004731L;
	private final ValueModel d_model;

	public ValueModelWrapper(ValueModel model) {
		d_model = model;
		model.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				firePropertyChange("value", event.getOldValue(), event.getNewValue());
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getValue() {
		return (T) d_model.getValue();
	}

	@Override
	public void setValue(Object newValue) {
		d_model.setValue(newValue);
	}
}
