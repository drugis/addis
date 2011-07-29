package org.drugis.addis.util;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ListDataEventProxy extends ListDataListenerManager {
	public ListDataEventProxy(Object source, ListModel model) {
		super(source);
		model.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				fireIntervalRemoved(e.getIndex0(), e.getIndex1());
			}
			public void intervalAdded(ListDataEvent e) {
				fireIntervalAdded(e.getIndex0(), e.getIndex1());
			}
			public void contentsChanged(ListDataEvent e) {
				fireContentsChanged(e.getIndex0(), e.getIndex1());
			}
		});
	}
}
