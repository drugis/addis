/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class StudyCharTableModel extends AbstractTableModel {
	private StudyListPresentationModel d_pm;
	
	public StudyCharTableModel(StudyListPresentationModel pm) {
		d_pm = pm;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			ValueModel vm = d_pm.getCharacteristicVisibleModel(c);
			vm.addValueChangeListener(new ValueChangeListener());
		}
	}
		
	public int getColumnCount() {
		return getNoVisibleCharacteristics() + 1;
	}

	private int getNoVisibleCharacteristics() {
		int visible = 0;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			if (d_pm.getCharacteristicVisibleModel(c).booleanValue()) {
				visible++;
			}
		}
		return visible;
	}

	public int getRowCount() {
		return d_pm.getIncludedStudies().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return d_pm.getIncludedStudies().get(rowIndex).getId();
		}
		StudyCharacteristic c = getCharacteristic(columnIndex);
		return d_pm.getIncludedStudies().get(rowIndex).getCharacteristics().get(c);
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "Study ID";
		}
		return getCharacteristic(columnIndex).getDescription();
	}
	
	private StudyCharacteristic getCharacteristic(int columnIndex) {
		int idx = 0;
		for (StudyCharacteristic c: StudyCharacteristic.values()) {
			if (d_pm.getCharacteristicVisibleModel(c).getValue().equals(Boolean.TRUE)) {
				++idx;
			}
			if (idx == columnIndex) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException();
	}
	
	private class ValueChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireTableStructureChanged();
		}		
	}
}
