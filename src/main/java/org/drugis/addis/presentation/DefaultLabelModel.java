package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jgoodies.binding.beans.Observable;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class DefaultLabelModel extends AbstractValueModel implements PropertyChangeListener {

	private final Observable d_bean;

	public DefaultLabelModel(Observable bean) {
		d_bean = bean;
		d_bean.addPropertyChangeListener(this);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange("value", null, getValue());
	}

	public Object getValue() {
		if (d_bean == null)
			return "INCOMPLETE";
		
		return d_bean.toString();
	}

	public void setValue(Object arg0) {
		throw new RuntimeException("Label is Read-Only");
	}
}
