/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui.components;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.drugis.common.gui.GUIHelper;

import com.sun.java.components.TableSorter;

@SuppressWarnings("serial")
public class EnhancedTable extends JTable {
	
	private EnhancedTableHeader d_tableHeader;

	public EnhancedTable(TableModel model) {
		super(model);
		setDefaultRenderer(Object.class, new MyRenderer());		
		d_tableHeader = new TooltipTableHeader(model, getColumnModel(), this);
		setTableHeader(d_tableHeader);
		setPreferredScrollableViewportSize(getPreferredSize());
		setBackground(Color.WHITE);
		d_tableHeader.autoSizeColumns();
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		TableSorter sort = new TableSorter(model);
		sort.setTableHeader(getTableHeader());
		setModel(sort);
	}
	
	public EnhancedTable(TableModel model, int maxColWidth) {
		this(model);
		d_tableHeader.setMaxColWidth(maxColWidth);
		d_tableHeader.autoSizeColumns();
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (d_tableHeader != null) {
			d_tableHeader.autoSizeColumns();
		}
	}
	
	private class MyRenderer extends DefaultTableCellRenderer {
		
		@Override
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
