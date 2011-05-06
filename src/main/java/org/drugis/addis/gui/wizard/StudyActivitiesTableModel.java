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
