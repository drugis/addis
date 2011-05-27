/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.util.comparator.AlphabeticalComparator;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

/**
 * Wraps a beans-property that returns a list in a ListHolder
 */
@SuppressWarnings("serial")
public class PropertyListHolder<E> extends AbstractListHolder<E> implements PropertyChangeListener {
	private final AbstractValueModel d_vm;

	@SuppressWarnings("unchecked")
	public PropertyListHolder(Object bean, String propertyName, Class<E> objType) {
		PresentationModel pm = new PresentationModel(bean);
		d_vm = pm.getModel(propertyName);
		
		if (d_vm.getValue() instanceof ObservableList<?>) {
			((ObservableList<?>) d_vm.getValue()).addListDataListener(new ListDataListener() {
				@Override
				public void intervalRemoved(ListDataEvent e) {
					fireValueChange(null, getValue());
				}
				@Override
				public void intervalAdded(ListDataEvent e) {
					fireValueChange(null, getValue());
				}
				@Override
				public void contentsChanged(ListDataEvent e) {
					fireValueChange(null, getValue());
				}
			});
		} else {
			d_vm.addValueChangeListener(this);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<E> getValue() {
		if (d_vm.getValue() instanceof Set) {
			List<E> lst = new ArrayList((Set<E>)d_vm.getValue());
			Collections.sort(lst, new AlphabeticalComparator());
			return lst;
		}
		return Collections.unmodifiableList((List<E>) d_vm.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object list) {
		if (d_vm.getValue() instanceof ObservableList) {
			((ObservableList<E>) d_vm.getValue()).clear();
			((ObservableList<E>) d_vm.getValue()).addAll((List<E>) list);
		} else if (d_vm.getValue() instanceof List) {
			d_vm.setValue((List<E>)list); 
		}
		else if (d_vm.getValue() instanceof Set){
			HashSet<E> hashSet = new HashSet<E>();
			for(int i=0; i < ((List<E>) list).size(); i++) {
				hashSet.add(((List<E>) list).get(i));
			}
			d_vm.setValue(hashSet);
		}
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		fireValueChange(null, getValue());
	}
}
