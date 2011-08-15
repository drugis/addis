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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ListDataListenerManager {
	public List<ListDataListener> d_listeners;
	private final Object d_source;

	public ListDataListenerManager(Object source) {
		d_source = source;
		d_listeners = new ArrayList<ListDataListener>();
	}

	public void fireIntervalAdded(int index0, int index1) {
		ListDataEvent evt = new ListDataEvent(d_source, ListDataEvent.INTERVAL_ADDED, index0, index1);
		for (ListDataListener l : d_listeners) {
			l.intervalAdded(evt);
		}
	}

	public void fireIntervalRemoved(int index0, int index1) {
		ListDataEvent evt = new ListDataEvent(d_source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
		for (ListDataListener l : d_listeners) {
			l.intervalRemoved(evt);
		}
	}
	
	public void fireContentsChanged(int index0, int index1) {
		ListDataEvent evt = new ListDataEvent(d_source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
		for (ListDataListener l : d_listeners) {
			l.contentsChanged(evt);
		}
	}

	public void addListDataListener(ListDataListener l) {
		d_listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		d_listeners.remove(l);
	}
}