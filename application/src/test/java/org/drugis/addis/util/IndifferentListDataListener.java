package org.drugis.addis.util;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class IndifferentListDataListener implements ListDataListener {

	@Override
	public void intervalAdded(ListDataEvent e) {
		update();
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		update();		
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		update();		
	}

	protected abstract void update();
}
