package org.drugis.addis.entities;

import java.beans.PropertyChangeListener;
import org.drugis.common.ObserverManager;
import com.jgoodies.binding.beans.Observable;

public abstract class AbstractObservable implements Observable {

	private transient ObserverManager d_om;

	public AbstractObservable() {
		init();
	}
	
	protected void init() {
		d_om = new ObserverManager(this);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		d_om.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		d_om.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		d_om.removePropertyChangeListener(listener);
	}
}
