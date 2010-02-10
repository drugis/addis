/**
 * 
 */
package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

public class MeasurementTableModel extends AbstractTableModel {		
	private static final long serialVersionUID = 5331596469882184969L;

	private Study d_study;
	private PresentationModelFactory d_pmf;
	private Class<? extends OutcomeMeasure> d_type;
	private MyMeasurementListener d_measListener = new MyMeasurementListener();
	
	public MeasurementTableModel(Study study, PresentationModelFactory pmf, 
			Class<? extends OutcomeMeasure> type) {
		d_study = study;
		d_pmf = pmf;
		d_type = type;
		
		connectMeasurementListeners();
	}

	private void connectMeasurementListeners() {
		for (Arm a : d_study.getArms()) {
			for (OutcomeMeasure m : d_study.getOutcomeMeasures()) {
				if (m.getClass().equals(d_type)) {
					d_study.getMeasurement(m, a).addPropertyChangeListener(d_measListener);
				}
			}
		}
	}

	public int getColumnCount() {
		return d_study.getArms().size() + 1;
	}

	public int getRowCount() {
		return d_study.getOutcomeMeasures(d_type).size();
	}
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	@Override
	public String getColumnName(int index) {
		if (index == 0) {
			return "Endpoint";
		}
		return d_pmf.getLabeledModel(d_study.getArms().get(index-1)).getLabelModel().getString();	
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return getEndpointAtIndex(rowIndex).getName();
		}
		OutcomeMeasure om = new ArrayList<OutcomeMeasure>(d_study.getOutcomeMeasures(d_type)).get(rowIndex);
		Arm arm = d_study.getArms().get(columnIndex - 1);
		return d_study.getMeasurement(om, arm);
	}

	private OutcomeMeasure getEndpointAtIndex(int rowIndex) {
		int index = 0;
		for (OutcomeMeasure m : d_study.getOutcomeMeasures(d_type)) {
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