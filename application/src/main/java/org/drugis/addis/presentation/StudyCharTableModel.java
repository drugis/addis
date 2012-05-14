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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristics;
import org.drugis.addis.util.EntityUtil;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class StudyCharTableModel extends AbstractTableModel {
	protected StudyListPresentation d_pm;
	private PresentationModelFactory d_pmf;
	
	public StudyCharTableModel(StudyListPresentation pm, PresentationModelFactory pmf) {
		d_pm = pm;
		d_pmf = pmf;
		for (Characteristic c : StudyCharacteristics.values()) {
			ValueModel vm = d_pm.getCharacteristicVisibleModel(c);
			vm.addValueChangeListener(new CharacteristicVisibleListener());
		}
		d_pm.getIncludedStudies().addListDataListener(new StudyListChangeListener());
	}
		
	public int getColumnCount() {
		return getNoVisibleCharacteristics() + 1;
	}

	private int getNoVisibleCharacteristics() {
		int visible = 0;
		for (Characteristic c : StudyCharacteristics.values()) {
			if (d_pm.getCharacteristicVisibleModel(c).booleanValue()) {
				visible++;
			}
		}
		return visible;
	}

	public int getRowCount() {
		return d_pm.getIncludedStudies().size();
	}

	/**
	 * @throws IndexOutOfBoundsException if row- or columnindex doesn't exist in the model
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < 0 || columnIndex >= getColumnCount()) {
			throw new IndexOutOfBoundsException("column index (" + columnIndex + ") out of bounds");
		}
		if (rowIndex < 0 || rowIndex >= getRowCount()) {
			throw new IndexOutOfBoundsException("row index (" + rowIndex + ") out of bounds");
		}
		
		if (columnIndex == 0) {
			return d_pm.getIncludedStudies().get(rowIndex);
		}
		Characteristic c = getCharacteristic(columnIndex);
		StudyPresentation spm = (StudyPresentation) d_pmf.getModel(d_pm.getIncludedStudies().get(rowIndex));
		return spm.getCharacteristicModel(c).getValue();
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "Study ID";
		}
		return getCharacteristic(columnIndex).getDescription();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return Study.class;
		}
		return EntityUtil.getConcreteTypeOrEntity(getCharacteristic(columnIndex).getValueType());
	}
	
	private Characteristic getCharacteristic(int columnIndex) {
		int idx = 0;
		for (Characteristic c: StudyCharacteristics.values()) {
			if (d_pm.getCharacteristicVisibleModel(c).getValue().equals(Boolean.TRUE)) {
				++idx;
			}
			if (idx == columnIndex) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException();
	}
	
	private class StudyListChangeListener implements ListDataListener {
		public void contentsChanged(ListDataEvent e) {
			fireTableStructureChanged(); // FIXME
		}

		public void intervalAdded(ListDataEvent e) {
			fireTableStructureChanged(); // FIXME
		}

		public void intervalRemoved(ListDataEvent e) {
			fireTableStructureChanged(); // FIXME
		}		
	}
	
	public class CharacteristicVisibleListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireTableStructureChanged();
		}
	}	
}
