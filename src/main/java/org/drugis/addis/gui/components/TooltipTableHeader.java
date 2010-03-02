package org.drugis.addis.gui.components;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class TooltipTableHeader extends EnhancedTableHeader {

	private TableModel d_model;

	public TooltipTableHeader(TableModel model, TableColumnModel cm, JTable table) {
		super(cm, table);
		
		d_model = model;
	}
	
	@Override
    public String getToolTipText(MouseEvent e) {
        java.awt.Point p = e.getPoint();
        int index = columnModel.getColumnIndexAtX(p.x);
        if (index < 0) {
        	return "";
        }
        int realIndex = columnModel.getColumn(index).getModelIndex();
        String columnName = d_model.getColumnName(realIndex);
        if (columnName != null && columnName.length() == 0) {
        	return null;
        }
		return columnName; 
    }
}
