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
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;

@SuppressWarnings("serial")
public class StudyMeasurementTableModel extends AbstractTableModel {		

	protected Study d_study;
	private PresentationModelFactory d_pmf;
	private Class<? extends Variable> d_type;
	protected MyMeasurementListener d_measurementListener = new MyMeasurementListener();
	
	public StudyMeasurementTableModel(Study study, PresentationModelFactory pmf, Class<? extends Variable> type) {
		d_study = study;
		d_pmf = pmf;
		d_type = type;
		
		connectMeasurementListeners();
	}

	private void connectMeasurementListeners() {
		for (Arm a : d_study.getArms()) {
			for (Variable v : d_study.getVariables(d_type)) {
				d_study.getMeasurement(v, a).addPropertyChangeListener(d_measurementListener);
			}
		}
	}

	public int getColumnCount() {
		return d_study.getArms().size() + 1;
	}

	public int getRowCount() {
		return d_study.getVariables(d_type).size();
	}
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	@Override
	public String getColumnName(int index) {
		if (index == 0) {
			return d_type.getSimpleName();
		}
		return d_pmf.getLabeledModel(d_study.getArms().get(index-1)).getLabelModel().getString();	
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return getVariableAtIndex(rowIndex).getName();
		}
		Variable om = new ArrayList<Variable>(d_study.getVariables(d_type)).get(rowIndex);
		Arm arm = d_study.getArms().get(columnIndex - 1);
		return d_study.getMeasurement(om, arm);
	}

	private Variable getVariableAtIndex(int rowIndex) {
		int index = 0;
		for (Variable m : d_study.getVariables(d_type)) {
			if (index == rowIndex) {
				return m;
			} else {
				index++;
			}
		}
		throw new IllegalStateException("no endpoint of index " + rowIndex);
	}
	
	private class MyMeasurementListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent ev) {
			fireTableDataChanged();
		}		
	}
}