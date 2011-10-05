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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.entities.Study.WhenTaken;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.presentation.wizard.MissingMeasurementPresentation;

@SuppressWarnings("serial")
public class StudyMeasurementTableModel extends AbstractTableModel {		

	protected Study d_study;
	private PresentationModelFactory d_pmf;
	private Class<? extends Variable> d_type;
	protected MyMeasurementListener d_measurementListener = new MyMeasurementListener();
	private Map<MeasurementKey, MissingMeasurementPresentation> d_mmpMap = new HashMap<MeasurementKey, MissingMeasurementPresentation>();
	private final boolean d_hasOverallColumn;
	
	public StudyMeasurementTableModel(Study study, PresentationModelFactory pmf, Class<? extends Variable> type, boolean hasOverallColumn) {
		d_study = study;
		d_pmf = pmf;
		d_type = type;
		d_hasOverallColumn = hasOverallColumn;
		
		initTable();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(isArmColumn(columnIndex)) {
			return String.class;
		} else if (isMeasurementMomentColumn(columnIndex)) { 
			return String.class;
		} else {
			return MissingMeasurementPresentation.class;
		}
	}
	
	private void initTable() {
		for (Arm a : d_study.getArms()) {
			initVariables(a);
		}
		if(d_hasOverallColumn) {
			initVariables(null);
		}
	}

	private void initVariables(Arm a) {
		for (StudyOutcomeMeasure<? extends Variable> v : d_study.getStudyOutcomeMeasures(d_type)) {
			MissingMeasurementPresentation mmp = new MissingMeasurementPresentation(d_study, v, a);
			for (WhenTaken wt : v.getWhenTaken()) {
				d_mmpMap.put(new MeasurementKey(v.getValue(), a, wt), mmp);
			}
			mmp.getMeasurement().addPropertyChangeListener(d_measurementListener);
			mmp.getMissingModel().addValueChangeListener(d_measurementListener);
		}
	}

	public int getColumnCount() {
		return d_study.getArms().size() + 2 + (d_hasOverallColumn ? 1 : 0);
	}

	public int getRowCount() {
		return d_study.getVariables(d_type).size();
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	@Override
	public String getColumnName(int col) {
		if (isArmColumn(col)) {
			return CategoryKnowledgeFactory.getCategoryKnowledge(d_type).getSingularCapitalized();
		} else if (isMeasurementMomentColumn(col)) {
			return "Measurement moment";
		} else if (isOverallColumn(col)) {
			return "Overall";
		} 
		return d_pmf.getLabeledModel(d_study.getArms().get(col-2)).getLabelModel().getString();	
	}

	private boolean isOverallColumn(int col) {
		return d_hasOverallColumn ? col == getColumnCount() - 1 : false;
	}

	private boolean isArmColumn(int col) {
		return col == 0;
	}

	private boolean isMeasurementMomentColumn(int col) {
		return col == 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (isArmColumn(columnIndex)) {
			return getVariableAtIndex(rowIndex).getName();
		} 
		StudyOutcomeMeasure<? extends Variable> om = d_study.getStudyOutcomeMeasures(d_type).get(rowIndex);
		Arm arm = (isOverallColumn(columnIndex) || isMeasurementMomentColumn(columnIndex)) ? null : d_study.getArms().get(columnIndex - 2);
		if (isMeasurementMomentColumn(columnIndex)) {
			return om.getWhenTaken().get(0);
		}
		return d_mmpMap.get(new Study.MeasurementKey(om.getValue(), arm, om.getWhenTaken().get(0)));
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