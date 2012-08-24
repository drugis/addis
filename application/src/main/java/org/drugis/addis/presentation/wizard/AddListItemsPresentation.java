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

import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.common.beans.AbstractObservable;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

public abstract class AddListItemsPresentation<T extends TypeWithName> extends AbstractObservable {

	public static final String PROPERTY_LIST = "list";

	protected ObservableList<T> d_list;
	protected final String d_itemName;
	protected final int d_minElements;
	protected int d_itemsCreated;

	public AddListItemsPresentation(ObservableList<T> list, String itemName, int minElements) {
		d_list = list;
		d_itemName = itemName;
		d_minElements = minElements;
		d_itemsCreated = 0;
	}

	public abstract T createItem();
	public abstract ObservableList<Note> getNotes(T t);
	public abstract ValueModel getRemovable(T t);

	public ObservableList<T> getList() {
		return d_list;
	}

	public void setList(ObservableList<T> list) {
		ObservableList<T> oldVal = d_list;
		d_list = list;
		d_itemsCreated = d_list.size();
		firePropertyChange(PROPERTY_LIST, oldVal, d_list);
	}

	public String getItemName() {
		return d_itemName;
	}

	public int getMinElements() {
		return d_minElements;
	}

	public String nextItemName() {
		return getItemName() + " " + (++d_itemsCreated);
	}

	/**
	 * Rename elements of the list.
	 * Since the "name" could be an index field, this has to be handled by the subclass.
	 * @param idx Index of the item to be renamed.
	 * @param newName Desired new name.
	 */
	public abstract void rename(int idx, String newName);
}