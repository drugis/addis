/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.gui.AddisWindow;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
abstract public class SelectFromFiniteListPresentationImpl<T> extends Model
implements SelectFromFiniteListPresentation<T> {

	protected List<ModifiableHolder<T>> d_slots;
	protected ListHolder<T> d_options;
	protected ValueModel d_addSlotsEnabled;
	protected InputCompleteModel d_inputCompleteModel;
	protected AddisWindow d_mainWindow;
	public static final String PROPERTY_NSLOTS = "nSlots";
	private String d_typeName;
	private String d_title;
	private String d_description;

	public SelectFromFiniteListPresentationImpl(ListHolder<T> options,
			String typeName, String title, String description, AddisWindow mainWindow) {
		d_typeName = typeName;
		d_title = title;
		d_description = description;
		d_slots = new ArrayList<ModifiableHolder<T>>();
		d_options = options;
		d_addSlotsEnabled = new AddSlotsAlwaysEnabledModel();
		d_inputCompleteModel = new InputCompleteModel();
		d_mainWindow = mainWindow;
	}

	public ListHolder<T> getOptions() {
		return d_options;
	}

	public void addSlot() {
		Slot<T> s = new Slot<T>(d_slots);
		d_slots.add(s);
		firePropertyChange(PROPERTY_NSLOTS, d_slots.size() - 1, d_slots.size());
		d_inputCompleteModel.addSlot(s);
	}

	public int countSlots() {
		return d_slots.size();
	}

	public void removeSlot(int idx) {
		ModifiableHolder<T> s = d_slots.get(idx);
		d_slots.remove(idx);
		firePropertyChange(PROPERTY_NSLOTS, d_slots.size() + 1, d_slots.size());
		d_inputCompleteModel.removeSlot(s);
	}

	public ModifiableHolder<T> getSlot(int idx) {
		return d_slots.get(idx);
	}

	public ValueModel getAddSlotsEnabledModel() {
		return d_addSlotsEnabled;
	}

	public ValueModel getInputCompleteModel() {
		return d_inputCompleteModel;
	}

	public boolean hasAddOptionDialog() {
		return true;
	}
	
	public void showAddOptionDialog(int idx) {
		throw new RuntimeException("AddOptionDialog not implemented");
	}

	public String getTypeName() {
		return d_typeName;
	}

	public String getTitle() {
		return d_title;
	}

	public String getDescription() {
		return d_description;
	}

	public List<ModifiableHolder<T>> getSlots() { 
		return Collections.unmodifiableList(d_slots);
	}
	
	public void clear() {
		d_slots.clear();
	}

	class Slot<E> extends ModifiableHolder<E> {
		private List<ModifiableHolder<E>> d_slots;
		public Slot(List<ModifiableHolder<E>> slots) {
			d_slots = slots;
		}
		@Override
		public void setValue(Object obj) {
			super.setValue(obj);			
			// Make sure each option is selected only once
			for (ModifiableHolder<E> s : d_slots) {
				if (s.getValue() != null && s != this && EqualsUtil.equal(s.getValue(), getValue())) {
					s.setValue(null);
				}
			}
		}
		
	}
	
	class InputCompleteModel extends AbstractValueModel implements PropertyChangeListener {
		private Boolean d_oldValue;
		
		public InputCompleteModel() {
			for (ModifiableHolder<T> s : d_slots) {
				s.addValueChangeListener(this);
			}
			d_oldValue = getValue();
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			evaluate();
		}

		private void evaluate() {
			if (getValue() != d_oldValue) {
				fireValueChange(d_oldValue, getValue());
				d_oldValue = getValue();
			}
		}

		public Boolean getValue() {
			boolean r = true;
			for (ModifiableHolder<T> s: d_slots) {
				if (s.getValue() == null) {
					r = false;
					break;
				}
			}
			return r;
		}
		
		public void addSlot(ModifiableHolder<T> s) {
			s.addValueChangeListener(this);
			evaluate();
		}
		
		public void removeSlot(ModifiableHolder<T> s) {
			s.removeValueChangeListener(this);
			evaluate();
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("InputCompleteModel is read-only");
		}
	}
	
	public class AddSlotsAlwaysEnabledModel extends AbstractValueModel {
		public Object getValue() {
			return true;
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("AddSlotsEnabledModel is read-only");
		}
	}
}
