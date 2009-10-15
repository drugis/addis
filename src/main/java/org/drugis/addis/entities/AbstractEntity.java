package org.drugis.addis.entities;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import org.drugis.common.ObserverManager;

public abstract class AbstractEntity implements Entity {
	private static final long serialVersionUID = -3889001536692466540L;
	
	transient private ObserverManager d_om;
	
	protected AbstractEntity() {
		init();
	}
	
	private void init() {
		d_om = new ObserverManager(this);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		init();
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
	
	public abstract Set<Entity> getDependencies();
}
