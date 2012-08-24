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

package org.drugis.addis.presentation;

import java.util.List;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

public interface SelectFromFiniteListPresentation<T> {
	/**
	 * Remove one of the slots
	 * @param idx
	 */
	public void removeSlot(int idx);

	/**
	 * Add an additional slot.
	 */
	public void addSlot();

	/**
	 * The number of slots.
	 */
	public int countSlots();

	/**
	 * The slot at index idx
	 */
	public ModifiableHolder<T> getSlot(int idx);

	/**
	 * Whether or not it is possible to add more options to the list.
	 */
	public boolean hasAddOptionDialog();

	/**
	 * Show a dialog for adding more options to the list, and use the new option in slot idx.
	 */
	public void showAddOptionDialog(int idx);

	/**
	 * Whether more slots can be added.
	 */
	public ValueModel getAddSlotsEnabledModel();

	/**
	 * Whether input is complete.
	 */
	public ValueModel getInputCompleteModel();

	/**
	 * The title for this step.
	 */
	public String getTitle();

	/**
	 * The title for this step.
	 */
	public String getDescription();

	/**
	 * A name for the type of options we are dealing with.
	 */
	public String getTypeName();

	/**
	 * A list of options to select from.
	 */
	ObservableList<T> getOptions();

	public List<ModifiableHolder<T>> getSlots();
}
