package org.drugis.addis.entities;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javolution.xml.XMLFormat;

import org.drugis.addis.util.EntityXMLFormat;
import org.drugis.addis.util.EntryXMLFormat;
import org.drugis.addis.util.HashMapXMLFormat;
import org.drugis.common.ObserverManager;

public abstract class AbstractEntity implements Entity, Serializable {
	private static final long serialVersionUID = -3889001536692466540L;
	
	transient private ObserverManager d_om;

	@SuppressWarnings("unchecked")
	protected static final XMLFormat<HashMap> mapXML = new HashMapXMLFormat();
	@SuppressWarnings("unchecked")
	protected static final XMLFormat<Entry> entryXML = new EntryXMLFormat();
	
	public AbstractEntity() {
		init();
	}
	
	protected void init() {
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
	
	public abstract Set<? extends Entity> getDependencies();
	
	public String[] getXmlExclusions() {
		return null;
	}
	
	protected static final XMLFormat<Entity> XML = new EntityXMLFormat();
}
