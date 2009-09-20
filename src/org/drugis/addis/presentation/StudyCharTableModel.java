package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class StudyCharTableModel extends AbstractTableModel {
	private MetaStudyPresentationModel d_pm;
	
	public StudyCharTableModel(MetaStudyPresentationModel pm) {
		d_pm = pm;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			ValueModel vm = d_pm.getCharacteristicVisibleModel(c);
			vm.addValueChangeListener(new ValueChangeListener(this));
		}
	}
		
	public int getColumnCount() {
		return getNoVisibleColumns() + 1;
	}

	private int getNoVisibleColumns() {
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
	
	private class ValueChangeListener implements PropertyChangeListener {
		private TableModel d_parent;
		private ValueChangeListener(TableModel parent) {
			d_parent = parent;
		}
		public void propertyChange(PropertyChangeEvent evt) {
			fireTableChanged(new TableModelEvent(d_parent));
		}		
	}
}
