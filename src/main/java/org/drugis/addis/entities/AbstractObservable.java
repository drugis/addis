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
