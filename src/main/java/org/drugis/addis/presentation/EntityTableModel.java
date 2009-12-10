package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Entity;

import com.jgoodies.binding.PresentationModel;


@SuppressWarnings("serial")
public class EntityTableModel<T extends Entity> extends AbstractTableModel {
	List<PresentationModel<T>> d_entities;
	List<String> d_props;

	public EntityTableModel(List<PresentationModel<T>> entities, List<String> properties) {
		d_entities = entities;
		for (PresentationModel<T> pm : d_entities)
			pm.addPropertyChangeListener(new ValueChangeListener());
		d_props = properties;
	}
	
	public int getColumnCount() {
		return d_props.size();
	}

	public int getRowCount() {
		return d_entities.size();
	}

	public Object getValueAt(int row, int column) {
		if (column == 0)
			return d_entities.get(row).getBean();
		
		return d_entities.get(row).getModel(d_props.get(column)).getValue();
	}

	@Override
	public String getColumnName(int columnIndex) {
		String name = d_props.get(columnIndex);
		for (int i = 0; i < name.length(); ++i) {
			if (Character.isUpperCase(name.charAt(i))) {
				name = name.substring(0,i) + " " + name.substring(i);
				++i;
			}
		}
			
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private class ValueChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireTableStructureChanged();
		}		
	}
}
