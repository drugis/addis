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

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;

@SuppressWarnings("serial")
public class StudyActivitiesTableModel extends AbstractTableModel {
	
	private final Study d_study;

	public StudyActivitiesTableModel(Study s) {
		d_study = s;
		d_study.getEpochs().addListDataListener(new ListDataListener() {
			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				fireTableStructureChanged();
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				fireTableStructureChanged();
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				fireTableStructureChanged();				
			}
		});
		d_study.getArms().addListDataListener(new ListDataListener() {
			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				fireTableDataChanged();
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				fireTableDataChanged();				
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				fireTableDataChanged();				
			}
		});
	}
	
	public int getColumnCount() {
		return d_study.getEpochs().size() + 1;
	}

	public int getRowCount() {
		return d_study.getArms().size();
	}

	public Object getValueAt(int row, int column) {
		if(column == 0) return d_study.getArms().get(row);
		return d_study.getStudyActivityAt(d_study.getArms().get(row), d_study.getEpochs().get(column - 1));
	}

	@Override
	public String getColumnName(int column) {
		return column == 0 ? "Arms" : d_study.getEpochs().get(column - 1).getName();
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		return column == 0 ? String.class : StudyActivity.class;
	}
	
	public void setValueAt(StudyActivity activity, int row, int column) {
		d_study.setStudyActivityAt(d_study.getArms().get(row), d_study.getEpochs().get(column - 1),  activity);
	}
}
