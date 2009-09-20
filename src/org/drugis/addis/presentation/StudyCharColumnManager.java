package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.AbstractValueModel;

public class StudyCharColumnManager {
	private TableColumnModel d_columnModel;
	
	
	

	protected StudyCharColumnManager(StudyCharTableModel tableModel,
			TableColumnModel columnModel, MetaStudyPresentationModel pm) {
		this.d_columnModel = columnModel;
		
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			AbstractValueModel model = pm.getCharacteristicVisibleModel(c);
			TableColumn column = columnModel.getColumn(tableModel.getCharacteristicColumnIndex(c));			
			if (model.booleanValue() == false) {
				d_columnModel.removeColumn(column);
			}
			model.addPropertyChangeListener(new ColumnValueListener(column));
		}
	}
	
	private class ColumnValueListener implements PropertyChangeListener {
		private TableColumn d_column;
		
		public ColumnValueListener(TableColumn column) {
			d_column = column;
		}
		public void propertyChange(PropertyChangeEvent ev) {
			if (ev.getNewValue().equals(Boolean.TRUE)) {
				d_columnModel.addColumn(d_column);
			} else {
				d_columnModel.removeColumn(d_column);
			}
		}
	}

	public static void connect(StudyCharTableModel model,
			TableColumnModel columnModel, MetaStudyPresentationModel pm) {
		
		@SuppressWarnings("unused")
		final StudyCharColumnManager mgr = new StudyCharColumnManager(model, columnModel, pm);
	}
}
