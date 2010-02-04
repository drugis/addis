package org.drugis.addis.gui.components;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.drugis.common.gui.GUIHelper;

@SuppressWarnings("serial")
public class StudyTable extends JTable {
	
	private EnhancedTableHeader d_tableHeader;

	public StudyTable(TableModel model) {
		super(model);
		setDefaultRenderer(Object.class, new MyRenderer());		
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
	
	private class MyRenderer extends DefaultTableCellRenderer {
		
		public void setValue(Object value) {
			if (value instanceof Date) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
				value = sdf.format((Date)value);
			}
			if (value == null) {
				setToolTipText(null);
			} else {
				setToolTipText(GUIHelper.createToolTip(value.toString()));
			}
			super.setValue(value);			
		}
	}	
}
