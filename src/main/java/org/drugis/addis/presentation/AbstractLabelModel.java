/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

	protected B getBean() {
		return d_bean;
	}
}
