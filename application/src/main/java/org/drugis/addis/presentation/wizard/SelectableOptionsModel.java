/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.presentation.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.ReadOnlyObservableList;
import org.drugis.common.beans.SortedSetModel;
import org.drugis.common.beans.TransformedObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;
import org.drugis.common.beans.TransformedObservableList.Transform;

import com.jgoodies.binding.list.ObservableList;

public class SelectableOptionsModel<E extends Comparable<? super E>> {
	private SortedSetModel<Option<E>> d_options = new SortedSetModel<Option<E>>();
	private ObservableList<E> d_selected;

	public SelectableOptionsModel() {
		ObservableList<Option<E>> contentAware = new ContentAwareListModel<Option<E>>(d_options);
		FilteredObservableList<Option<E>> selectedOptions = new FilteredObservableList<Option<E>>(contentAware, new Filter<Option<E>>() {
			public boolean accept(Option<E> obj) {
				return obj.toggle.getValue();
			}
		});
		d_selected = new TransformedObservableList<Option<E>, E>(selectedOptions, new Transform<Option<E>, E>() {
			public E transform(Option<E> a) {
				return a.item;
			}
		});		
	}
	
	/**
	 * Remove all options.
	 */
	public void clear() {
		d_options.clear();
	}
	
	/**
	 * Create a new option.
	 * @param option The entity that should be selectable through a ValueModel.
	 * @return The modifiable holder.
	 */
	public ModifiableHolder<Boolean> addOption(E option, boolean initialValue) {
		Option<E> o = new Option<E>(option, initialValue);
		d_options.add(o);
		return o.toggle;
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
	
	public ModifiableHolder<Boolean> getSelectedModel(E option) {
		int idx = Collections.binarySearch(d_options, new Option<E>(option, false));
		if (idx >= 0) {
			return d_options.get(idx).toggle;
		}
		return null;
	}
	
	public ObservableList<Option<E>> getOptions() {
		return new ReadOnlyObservableList<Option<E>>(d_options);
	}
	
	/**
	 * Observable list of the options that are selected (set to true).
	 */
	public ObservableList<E> getSelectedOptions() {
		return d_selected;
	}
}
