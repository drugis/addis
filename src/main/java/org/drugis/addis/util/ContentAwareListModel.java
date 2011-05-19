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

package org.drugis.addis.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.jgoodies.binding.beans.Observable;
import com.jgoodies.binding.list.ObservableList;

public class ContentAwareListModel<T extends Observable> extends AbstractListModel implements ListModel {
	private static final long serialVersionUID = -2573774495322221916L;
	private ObservableList<T> d_nested;
	private PropertyChangeListener d_listMemberListener;
	private List<T> d_observed = new ArrayList<T>();

	public ContentAwareListModel(ObservableList<T> lm) {
		d_nested = lm;
		
		d_listMemberListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				update();
			}
		};
		
		d_nested.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateListeners();
				fireIntervalRemoved(ContentAwareListModel.this, e.getIndex0(), e.getIndex1());
			}
			
			public void intervalAdded(ListDataEvent e) {
				updateListeners();
				fireIntervalAdded(ContentAwareListModel.this, e.getIndex0(), e.getIndex1());
			}

			public void contentsChanged(ListDataEvent e) {
				updateListeners();
				update();
			}
		});
		
		updateListeners();
	}
	
	private void updateListeners() {
		for (T e : d_observed) {
			e.removePropertyChangeListener(d_listMemberListener);
		}
		d_observed.clear();
		for (T e : d_nested) {
			e.addPropertyChangeListener(d_listMemberListener);
			d_observed.add(e);
		}
	}

	private void update() {
		fireContentsChanged(this, 0, d_nested.getSize()-1);
	}	
		
	public Object getElementAt(int index) {
		return d_nested.getElementAt(index);
	}

	public int getSize() {
		return d_nested.getSize();
	}
	
	public ObservableList<T> getList() {
		return d_nested;
	}
}
