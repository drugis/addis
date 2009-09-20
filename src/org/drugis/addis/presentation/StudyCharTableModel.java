package org.drugis.addis.presentation;

import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.StudyCharacteristic;

@SuppressWarnings("serial")
public class StudyCharTableModel extends AbstractTableModel {
	MetaStudyPresentationModel d_pm;
	
	public StudyCharTableModel(MetaStudyPresentationModel pm) {
		d_pm = pm;
	}

	public int getColumnCount() {
		return StudyCharacteristic.values().length + 1;
	}

	public int getRowCount() {
		return d_pm.getIncludedStudies().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return d_pm.getIncludedStudies().get(rowIndex).getId();
		}
		StudyCharacteristic c = StudyCharacteristic.values()[columnIndex - 1];
		return d_pm.getIncludedStudies().get(rowIndex).getCharacteristics().get(c);
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "Study ID";
		}
		return StudyCharacteristic.values()[columnIndex - 1].getDescription();
	}
	
	public int getCharacteristicColumnIndex(StudyCharacteristic c) {
		return Arrays.asList(StudyCharacteristic.values()).indexOf(c) + 1;
	}
}
