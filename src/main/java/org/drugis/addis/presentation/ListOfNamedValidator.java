/**
 * 
 */
package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.TypeWithName;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

/**
 * Validates an ObservableList of TypeWithName. The names should be unique and non-empty. It should contain at least minElms elements.
 */
@SuppressWarnings("serial")
public class ListOfNamedValidator<T extends TypeWithName> extends AbstractValueModel implements ValueHolder<Boolean> {
	private final ObservableList<T> d_listModel;
	PropertyChangeListener d_nameListener;
	private int d_minElms;

	/**
	 * @param listModel The list to validate.
	 * @param minElms The minimum number of elements the list should contain.
	 */
	public ListOfNamedValidator(ObservableList<T> listModel, int minElms) {
		d_minElms = minElms;
		d_listModel = listModel;
		d_nameListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				update();
			}
		};
		
		listModel.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateListeners();
			}
			
			public void intervalAdded(ListDataEvent e) {
				updateListeners();
			}

			public void contentsChanged(ListDataEvent e) {
				updateListeners();
			}
		});
		
		updateListeners();
	}
	
	private void updateListeners() {
		for (T e : d_listModel) {
			e.removePropertyChangeListener(d_nameListener);
			e.addPropertyChangeListener(d_nameListener);
		}
		update();
	}

	private void update() {
		fireValueChange(null, getValue());
	}

	/**
	 * @return true iff the list is valid.
	 */
	public Boolean getValue() {
		return areNamesUniqueAndValid() && d_listModel.size() >= d_minElms;
	}

	public void setValue(Object newValue) {}
	
	private boolean areNamesUniqueAndValid() {
		Set<String> s = new HashSet<String>();
		for (T e: d_listModel) {
			if (e.getName().length() == 0 || !s.add(e.getName())) return false;
		}
		return true;
	}

}