package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.value.AbstractValueModel;

public class StudyCharColumnManager {
	public static void connect(
			final StudyCharTableModel tableModel,
			final TableColumnModel columnModel,
			final MetaStudyPresentationModel pm) {
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			final AbstractValueModel model = pm.getCharacteristicVisibleModel(c);
			final TableColumn column = columnModel.getColumn(tableModel.getCharacteristicColumnIndex(c));
			model.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent arg0) {
					if (model.getValue().equals(Boolean.TRUE)) {
						columnModel.addColumn(column);
					} else {
						columnModel.removeColumn(column);
					}
				}
			});
		}
	}
}
