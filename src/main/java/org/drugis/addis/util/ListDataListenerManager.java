/**
 * 
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