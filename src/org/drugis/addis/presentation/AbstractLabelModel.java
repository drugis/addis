package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jgoodies.binding.beans.Observable;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public abstract class AbstractLabelModel<B extends Observable> extends AbstractValueModel implements PropertyChangeListener {
	protected B d_bean;
	
	protected AbstractLabelModel(B bean) {
		d_bean = bean;
		getBean().addPropertyChangeListener(this);
	}

	public String getValue() {
		return getBean().toString();
	}

	public void setValue(Object newValue) {
		throw new RuntimeException("Label is Read-Only");
	}

	public abstract void propertyChange(PropertyChangeEvent evt);
	
	protected void firePropertyChange(String oldVal, String newVal) {
		firePropertyChange("value", oldVal, newVal);
	}

	public B getBean() {
		return d_bean;
	}
}
