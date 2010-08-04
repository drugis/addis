/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Entity;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.PropertyNotFoundException;
import com.jgoodies.binding.value.ValueModel;


@SuppressWarnings("serial")
public class EntityTableModel extends AbstractTableModel {
	List<PresentationModel<? extends Entity>> d_entities;
	List<String> d_props;

	public EntityTableModel(List<PresentationModel<? extends Entity>> entities, List<String> properties) {
		d_entities = entities;
		for (PresentationModel<? extends Entity> pm : d_entities)
			pm.addPropertyChangeListener(new ValueChangeListener());
		d_props = properties;
	}
	
	public int getColumnCount() {
		return d_props.size();
	}

	public int getRowCount() {
		return d_entities.size();
	}

	public Object getValueAt(int row, int column) {
		if (column == 0)
			return d_entities.get(row).getBean();
		
		
		try {
			ValueModel model = d_entities.get(row).getModel(d_props.get(column));
			return model.getValue();
		} catch (PropertyNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		String name = d_props.get(columnIndex);
		for (int i = 0; i < name.length(); ++i) {
			if (Character.isUpperCase(name.charAt(i))) {
				name = name.substring(0,i) + " " + name.substring(i);
				++i;
			}
		}
			
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private class ValueChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireTableStructureChanged();
		}		
	}
}
