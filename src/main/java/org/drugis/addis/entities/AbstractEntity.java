/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.entities;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javolution.xml.XMLFormat;

import org.drugis.addis.util.EntityXMLFormat;
import org.drugis.addis.util.EntryXMLFormat;
import org.drugis.addis.util.HashMapXMLFormat;
import org.drugis.common.ObserverManager;

public abstract class AbstractEntity implements Entity {
	
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
