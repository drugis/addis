/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.beans.IntrospectionException;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.util.EntityUtil;

import com.jgoodies.binding.beans.BeanUtils;
import com.jgoodies.binding.beans.PropertyNotFoundException;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;


@SuppressWarnings("serial")
public class EntityTableModel extends AbstractTableModel {
	private Class<? extends Entity> d_entityType;
	ObservableList<? extends Entity> d_entities;
	List<String> d_props;
	private final PresentationModelFactory d_pmf;

	public EntityTableModel(Class<? extends Entity> entityType, ObservableList<? extends Entity> entities, List<String> properties, PresentationModelFactory pmf) {
		d_entities = entities;
		d_entityType = entityType;
		d_props = properties;
		d_pmf = pmf;
		
		d_entities.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				fireTableDataChanged();
			}
			public void intervalAdded(ListDataEvent e) {
				fireTableDataChanged();
			}
			public void contentsChanged(ListDataEvent e) {
				fireTableDataChanged();
			}
		});
	}
	
	public int getColumnCount() {
		return d_props.size();
	}

	public int getRowCount() {
		return d_entities.size();
	}

	public Object getValueAt(int row, int column) {
		if (d_props.get(column).equals("name")) {
			return d_entities.get(row);
		}
		try {
			ValueModel model = d_pmf.getModel(d_entities.get(row)).getModel(d_props.get(column));
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
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (d_props.get(columnIndex).equals("name")) {
			return d_entityType;
		}
		try {
			Class<?> propertyType = BeanUtils.getPropertyDescriptor(d_entityType, d_props.get(columnIndex)).getPropertyType();
			return EntityUtil.getConcreteTypeOrEntity(propertyType);
		} catch (IntrospectionException e) {
			System.err.println(e);
			return Object.class;
		}
	}
}
