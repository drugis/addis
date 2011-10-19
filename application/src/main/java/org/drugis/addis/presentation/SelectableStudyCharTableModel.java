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

@SuppressWarnings("serial")
public class SelectableStudyCharTableModel extends StudyCharTableModel {

	private final SelectableStudyListPresentation d_spm;

	public SelectableStudyCharTableModel(SelectableStudyListPresentation pm, PresentationModelFactory pmf) {
		super(pm.getSource(), pmf);
		d_spm = pm;
	}
	
	@Override
	public int getColumnCount() {
		return super.getColumnCount() + 1;
	}
	
	@Override
	public Class<?> getColumnClass(int c) {
		if (getRowCount() < 1) {
			return Object.class;
		}
		Object value = getValueAt(0, c);
		return (value == null ? Object.class : value.getClass());
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < 0 || columnIndex >= getColumnCount()) {
			throw new IndexOutOfBoundsException("column index (" + columnIndex + ") out of bounds");
		}
		if (rowIndex < 0 || rowIndex >= getRowCount()) {
			throw new IndexOutOfBoundsException("row index (" + rowIndex + ") out of bounds");
		}
		
		if (columnIndex == 0) {
			return getVisibleModelByRow(rowIndex).getValue();
		}	
		
		return super.getValueAt(rowIndex, columnIndex - 1);
	}

	private ModifiableHolder<Boolean> getVisibleModelByRow(int rowIndex) {
		return d_spm.getSelectedStudyBooleanModel(d_spm.getAvailableStudies().get(rowIndex));
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "";
		}
		
		return super.getColumnName(columnIndex - 1);
	}		
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}
	
	@Override
	public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			getVisibleModelByRow(rowIndex).setValue((Boolean) newValue);
		}
	}
}
