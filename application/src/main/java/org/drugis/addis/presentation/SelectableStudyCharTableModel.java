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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.Study;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class SelectableStudyCharTableModel extends StudyCharTableModel {
	private HashMap<Study, ModifiableHolder<Boolean>> d_selectedStudiesMap = new HashMap<Study, ModifiableHolder<Boolean>>();
	
	private ObservableList<Study> d_selectedStudiesList = new ArrayListModel<Study>();
	
	public SelectableStudyCharTableModel(StudyListPresentation source, PresentationModelFactory pmf) {
		super(source, pmf);
		updateSelectedStudies();
	}

	private void updateSelectedStudies() {
		for (Study s : getAvailableStudies()) {
			if (!d_selectedStudiesMap.containsKey(s)) {
				d_selectedStudiesMap.put(s, getBooleanHolder());
			}
		}
		
		Set<Study> leftStudies = new HashSet<Study>(d_selectedStudiesMap.keySet());
		leftStudies.removeAll(getAvailableStudies());
		for (Study s : leftStudies) {
			d_selectedStudiesMap.remove(s);
		}
		d_selectedStudiesList.clear();
		d_selectedStudiesList.addAll(createSelectedStudies());
	}

	public ObservableList<Study> getAvailableStudies() {
		return d_pm.getIncludedStudies();
	}
	
	public ObservableList<Study> getSelectedStudiesModel() {
		return d_selectedStudiesList;
	}

	private ModifiableHolder<Boolean> getBooleanHolder() {
		ModifiableHolder<Boolean> holder = new ModifiableHolder<Boolean>();
		holder.setValue(true);
		holder.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_selectedStudiesList.clear();
				d_selectedStudiesList.addAll(createSelectedStudies());
			}
		});
		return holder;
	}

	private List<Study> createSelectedStudies() {
		List<Study> selectedStudyList = new ArrayList<Study>();
		
		for (Study s : getAvailableStudies()) {
			if (d_selectedStudiesMap.get(s).getValue() ) {
				selectedStudyList.add(s);
			}
		}
		
		return selectedStudyList;
	}
	
	/**
	 * 
	 * @see org.drugis.addis.presentation.SelectableStudyListPresentation#getSelectedStudyBooleanModel(org.drugis.addis.entities.Study)
	 * @throws IllegalArgumentException if !getIncludedStudies().getValue().contains(s)
	 */
	public ModifiableHolder<Boolean> getSelectedStudyBooleanModel(Study s) throws IllegalArgumentException{
		if (!getAvailableStudies().contains(s)) {
			throw new IllegalArgumentException();
		}
		return d_selectedStudiesMap.get(s);
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
			return getSelectedModelByRow(rowIndex).getValue();
		}	
		
		return super.getValueAt(rowIndex, columnIndex - 1);
	}

	private ModifiableHolder<Boolean> getSelectedModelByRow(int rowIndex) {
		return getSelectedStudyBooleanModel(getAvailableStudies().get(rowIndex));
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
			getSelectedModelByRow(rowIndex).setValue((Boolean) newValue);
		}
	}
	
	@Override
	protected void includedStudiesListChanged() {
		updateSelectedStudies();
	}
}
