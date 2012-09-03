package org.drugis.addis.gui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class NonEmptyValueModel extends AbstractValueModel {
	private static final long serialVersionUID = 3077403842880451360L;
	private ValueModel d_wrapped;
	
	public NonEmptyValueModel(ValueModel wrapped) {
		d_wrapped = wrapped;
		d_wrapped.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				update();
			}
		});
		update();
	}
	
	public void update() {
		fireValueChange(null, getValue());
	}

	@Override
	public Boolean getValue() {
		return (d_wrapped.getValue() != null && !d_wrapped.getValue().equals(""));
	}

	@Override
	public void setValue(Object newValue) {
		throw new RuntimeException("Modification not allowed");
	}
}
