package org.drugis.addis.presentation.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.list.ObservableList;

public abstract class OptionsEnabledModel<E extends Comparable<? super E>> {
	private final SelectableOptionsModel<E> d_selectModel;
	private final ObservableList<Option<E>> d_options;
	private final List<Option<E>> d_enabled;

	public OptionsEnabledModel(SelectableOptionsModel<E> selectModel, boolean listenToSelection) {
		d_selectModel = selectModel;
		d_options = d_selectModel.getOptions();
		d_enabled = new ArrayList<Option<E>>();
		intervalAdded(0, d_selectModel.getOptions().size() - 1);
		
		d_options.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				OptionsEnabledModel.this.intervalRemoved(e.getIndex0(), e.getIndex1());
			}
			public void intervalAdded(ListDataEvent e) {
				OptionsEnabledModel.this.intervalAdded(e.getIndex0(), e.getIndex1());
			}
			public void contentsChanged(ListDataEvent e) {
			}
		});
		
		if (listenToSelection) {
			d_selectModel.getSelectedOptions().addListDataListener(new ListDataListener() {
				public void intervalRemoved(ListDataEvent e) {
					update();
				}
				public void intervalAdded(ListDataEvent e) {
					update();
				}
				public void contentsChanged(ListDataEvent e) {
					update();
				}
			});
		}
	}
	
	private void intervalAdded(int index0, int index1) {
		for (int i = index0; i <= index1; ++i) {
			E item = d_options.get(i).item;
			d_enabled.add(i, new Option<E>(item, optionShouldBeEnabled(item)));
		}
	}
	
	private void intervalRemoved(int index0, int index1) {
		for (int i = index1; i >= index0; --i) {
			d_enabled.remove(i);
		}
	}
	
	public abstract boolean optionShouldBeEnabled(E option);
	
	/**
	 * Update the enabled-ness of all options.
	 */
	public void update() {
		for (Option<E> selected : d_selectModel.getOptions()) {
			boolean enabled = optionShouldBeEnabled(selected.item);
			if (getEnabledModel(selected.item) != null) {
				getEnabledModel(selected.item).setValue(enabled);
			}
			if (!enabled && selected.toggle.getValue()) {
				selected.toggle.setValue(false);
			}
		}
	}
	
	public ValueHolder<Boolean> getEnabledModel(E option) {
		int idx = Collections.binarySearch(d_enabled, new Option<E>(option, false));
		if (idx >= 0) {
			return d_enabled.get(idx).toggle;
		}
		return null;
	}
}
