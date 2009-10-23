package org.drugis.addis.gui.components;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import org.drugis.addis.presentation.StudyCharTableModel;

@SuppressWarnings("serial")
public class StudyTable extends JTable {
	
	private EnhancedTableHeader d_tableHeader;

	public StudyTable(StudyCharTableModel model) {
		super(model);
		
		d_tableHeader = new EnhancedTableHeader(getColumnModel(), this);
		setTableHeader(d_tableHeader);
		setPreferredScrollableViewportSize(getPreferredSize());
		setBackground(Color.WHITE);
		d_tableHeader.autoSizeColumns();		
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (d_tableHeader != null) {
			d_tableHeader.autoSizeColumns();	
		}
	}

}
