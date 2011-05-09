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
	
	// setNestedList(...)
}
