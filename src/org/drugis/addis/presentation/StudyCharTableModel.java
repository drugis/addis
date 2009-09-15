package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

@SuppressWarnings("serial")
public class StudyCharTableModel extends AbstractTableModel {
	List<Study> d_studies;
	
	public StudyCharTableModel(List<Study> studies) {
		d_studies = new ArrayList<Study>(studies);
	}

	public int getColumnCount() {
		return StudyCharacteristic.values().length + 1;
	}

	public int getRowCount() {
		return d_studies.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return d_studies.get(rowIndex).getId();
		}
		StudyCharacteristic c = StudyCharacteristic.values()[columnIndex - 1];
		return d_studies.get(rowIndex).getCharacteristics().get(c);
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
