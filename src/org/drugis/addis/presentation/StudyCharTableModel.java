package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class StudyCharTableModel extends AbstractTableModel {
	private MetaStudyPresentationModel d_pm;
	
	public StudyCharTableModel(MetaStudyPresentationModel pm) {
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
