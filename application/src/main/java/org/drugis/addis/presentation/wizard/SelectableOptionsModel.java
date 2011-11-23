package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.SortedSetModel;
import org.drugis.common.beans.TransformedObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;
import org.drugis.common.beans.TransformedObservableList.Transform;

import com.jgoodies.binding.list.ObservableList;

public class SelectableOptionsModel<E extends Comparable<? super E>> {
	public static class Option<E extends Comparable<? super E>> extends AbstractObservable implements Comparable<Option<E>> {
		public static final String PROPERTY_SELECTED = null;
		public final E item;
		public final ModifiableHolder<Boolean> selected;
		
		public Option(E it, boolean value) {
			item = it;
			selected = new ModifiableHolder<Boolean>(value);
			selected.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					firePropertyChange(PROPERTY_SELECTED, evt.getOldValue(), evt.getNewValue());
				}
			});
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Option<?>) {
				Option<?> other = (Option<?>) obj;
				return EqualsUtil.equal(item, other.item);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return item.hashCode();
		}

		@Override
		public int compareTo(Option<E> o) {
			return item.compareTo(o.item);
		}
	}

	
	private SortedSetModel<Option<E>> d_options = new SortedSetModel<Option<E>>();
	private ObservableList<E> d_selected;

	public SelectableOptionsModel() {
		ObservableList<Option<E>> d_contentAware = new ContentAwareListModel<Option<E>>(d_options);
		FilteredObservableList<Option<E>> d_selectedOptions = new FilteredObservableList<Option<E>>(d_contentAware, new Filter<Option<E>>() {
			public boolean accept(Option<E> obj) {
				return obj.selected.getValue();
			}
		});
		d_selected = new TransformedObservableList<Option<E>, E>(d_selectedOptions, new Transform<Option<E>, E>() {
			public E transform(Option<E> a) {
				return a.item;
			}
		});		
	}
	
	/**
	 * Remove all options.
	 */
	public void clear() {
	}
	
	/**
	 * Create a new option.
	 * @param option The entity that should be selectable through a ValueModel.
	 * @return The modifiable holder.
	 */
	public ModifiableHolder<Boolean> addOption(E option, boolean initialValue) {
		Option<E> o = new Option<E>(option, initialValue);
		d_options.add(o);
		return o.selected;
	}
	
	/**
	 * Create new options.
	 * @param option The entity that should be selectable through a ValueModel.
	 * @return The modifiable holder.
	 */
	public List<ModifiableHolder<Boolean>> addOptions(Collection<? extends E> options, boolean initialValue) {
		List<ModifiableHolder<Boolean>> retVal = new ArrayList<ModifiableHolder<Boolean>>();
		for (E it : options) {
			retVal.add(addOption(it, initialValue));
		}
		return retVal;
	}
	
	/**
	 * Observable list of the options that are selected (set to true).
	 */
	public ObservableList<E> getSelectedOptions() {
		return d_selected;
	}
}
