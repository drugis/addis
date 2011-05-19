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

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.TypeWithName;

import com.jgoodies.binding.list.ObservableList;

abstract public class RenameMonitor<T extends TypeWithName> {
	private ObservableList<T> d_list;
	private List<T> d_listened = new ArrayList<T>();
	private ListDataListener d_listListener = new ListDataListener() {
		public void intervalRemoved(ListDataEvent e) {
			updateListeners();
		}
		public void intervalAdded(ListDataEvent e) {
			updateListeners();
		}
		public void contentsChanged(ListDataEvent e) {
			updateListeners();
		}
	};
	private PropertyChangeListener d_itemListener = new PropertyChangeListener() {
		
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(TypeWithName.PROPERTY_NAME)) {
				renameDetected();
			}
		}
	};

	public RenameMonitor(final AddListItemsPresentation<T> listPresentation) {
		listPresentation.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(AddListItemsPresentation.PROPERTY_LIST)) {
					resetList(listPresentation.getList());
				}
			}
		});
		resetList(listPresentation.getList());
	}

	private void resetList(ObservableList<T> list) {
		if (d_list != null) {
			d_list.removeListDataListener(d_listListener);
		}
		d_list = list;
		list.addListDataListener(d_listListener);
		updateListeners();
	}

	private void updateListeners() {
		for (T item : d_listened) {
			item.removePropertyChangeListener(d_itemListener);
		}
		d_listened.clear();
		for (T item : d_list) {
			item.addPropertyChangeListener(d_itemListener);
			d_listened.add(item);
		}
	}
	
	abstract protected void renameDetected();
}