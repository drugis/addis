package org.drugis.addis.gui.components;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class StudyTable extends JTable {
	
	private EnhancedTableHeader d_tableHeader;

	public StudyTable(TableModel model) {
		super(model);
		d_tableHeader = new StudyTableHeader(model, getColumnModel(), this);
		setTableHeader(d_tableHeader);
		setPreferredScrollableViewportSize(getPreferredSize());
		setBackground(Color.WHITE);
		d_tableHeader.autoSizeColumns();
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		TableSorter sort = new TableSorter(model);
		sort.setTableHeader(getTableHeader());
		setModel(sort);
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (d_tableHeader != null) {
			d_tableHeader.autoSizeColumns();	
		}
	}
}
