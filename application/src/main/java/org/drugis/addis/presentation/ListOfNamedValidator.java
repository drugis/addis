/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.TypeWithName;
import org.drugis.common.beans.ContentAwareListModel;

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
	private ContentAwareListModel<T> d_contentAware;

	/**
	 * @param listModel The list to validate.
	 * @param minElms The minimum number of elements the list should contain.
	 */
	public ListOfNamedValidator(ObservableList<T> listModel, int minElms) {
		d_minElms = minElms;
		d_listModel = listModel;
		d_contentAware = new ContentAwareListModel<T>(d_listModel);
		
		d_contentAware.addListDataListener(new ListDataListener() {
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