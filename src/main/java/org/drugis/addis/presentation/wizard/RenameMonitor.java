/**
 * 
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
		d_list = listPresentation.getList();
		listPresentation.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(AddListItemsPresentation.PROPERTY_LIST)) {
					resetList(listPresentation.getList());
				}
			}
		});
	}

	private void resetList(ObservableList<T> list) {
		d_list.removeListDataListener(d_listListener);
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