package org.drugis.addis.gui.wizard;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;

@SuppressWarnings("serial")
public class StudyActivitiesTableModel extends AbstractTableModel {
	
	private final Study d_study;

	public StudyActivitiesTableModel(Study s) {
		d_study = s;
	}
	
	public int getColumnCount() {
		return d_study.getEpochs().size() + 1;
	}

	public int getRowCount() {
		return d_study.getArms().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) return d_study.getArms().get(rowIndex);
		return d_study.getStudyActivityAt(d_study.getArms().get(rowIndex), d_study.getEpochs().get(columnIndex - 1));
	}

	@Override
	public String getColumnName(int column) {
		return column == 0 ? "Arms" : d_study.getEpochs().get(column-1).getName();
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		d_study.setStudyActivityAt(d_study.getArms().get(rowIndex), d_study.getEpochs().get(columnIndex - 1), 
				new StudyActivity(aValue.toString(), null));
	}
}
