package org.drugis.addis.gui.components;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.drugis.addis.presentation.StudyCharTableModel;

@SuppressWarnings("serial")
public class StudyTableHeader extends EnhancedTableHeader {

	private StudyCharTableModel d_model;

	public StudyTableHeader(StudyCharTableModel model, TableColumnModel cm, JTable table) {
		super(cm, table);
		
		d_model = model;
	}
	
	@Override
    public String getToolTipText(MouseEvent e) {
        java.awt.Point p = e.getPoint();
        int index = columnModel.getColumnIndexAtX(p.x);
        int realIndex = columnModel.getColumn(index).getModelIndex();
        return d_model.getColumnName(realIndex); 
    }
}
