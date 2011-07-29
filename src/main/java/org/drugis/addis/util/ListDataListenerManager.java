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

	public ListDataListenerManager() {
		d_listeners = new ArrayList<ListDataListener>();
	}

	public void fireIntervalAdded(Object src, int index0, int index1) {
		ListDataEvent evt = new ListDataEvent(src, ListDataEvent.INTERVAL_ADDED, index0, index1);
		for (ListDataListener l : d_listeners) {
			l.intervalAdded(evt);
		}
	}

	public void fireIntervalRemoved(Object src, int index0, int index1) {
		ListDataEvent evt = new ListDataEvent(src, ListDataEvent.INTERVAL_REMOVED, index0, index1);
		for (ListDataListener l : d_listeners) {
			l.intervalRemoved(evt);
		}
	}
	
	public void fireContentsChanged(Object src, int index0, int index1) {
		ListDataEvent evt = new ListDataEvent(src, ListDataEvent.CONTENTS_CHANGED, index0, index1);
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