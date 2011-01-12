package org.drugis.addis.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class HTMLWrappingModel extends AbstractValueModel {
	private final ValueModel d_nested;

	public HTMLWrappingModel(ValueModel nested) {
		d_nested = nested;
		d_nested.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireValueChange(null, getValue());				
			}
		});
	}
	public Object getValue() {
		return "<html><p style='margin-top: 3px;'>" + d_nested.getValue() + "</p></html>";
	}
	public void setValue(Object newValue) {
		throw new IllegalAccessError(this.getClass().getName()+" is read-only!");
	}
}
