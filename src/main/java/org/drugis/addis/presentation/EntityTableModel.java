package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Entity;

import com.jgoodies.binding.PresentationModel;


@SuppressWarnings("serial")
public class EntityTableModel<T extends Entity> extends AbstractTableModel {
	SortedSet<T> d_entities;
	Domain d_domain;
	List<String> d_props;

	public EntityTableModel(SortedSet<T> entities, Domain domain, List<String> properties) {
		d_entities = entities;
		d_domain = domain;
		d_props = properties;
		d_domain.addListener(new ValueChangeListener());		
	}
	
	public int getColumnCount() {
		return d_props.size();
	}

	public int getRowCount() {
		return d_entities.size();
	}

	public Object getValueAt(int row, int column) {
		List<T> lists = new ArrayList<T>(d_entities); 
		PresentationModel<T> pm = new PresentationModelFactory(d_domain).getModel(lists.get(row));
		return pm.getModel(d_props.get(column)).getValue();
	}

	private class ValueChangeListener implements DomainListener {
		public void analysesChanged() {
			// TODO Auto-generated method stub
		}

		public void drugsChanged() {
			fireTableStructureChanged();
		}

		public void endpointsChanged() {
			fireTableStructureChanged();	
		}

		public void indicationsChanged() {
			fireTableStructureChanged();	
		}

		public void studiesChanged() {
			// TODO Auto-generated method stub		
		}		
	}
}
