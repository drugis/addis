/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import org.drugis.addis.gui.util.TableCopyHandler;
import org.drugis.common.gui.GUIHelper;

import com.sun.java.components.TableSorter;

@SuppressWarnings("serial")
public class EnhancedTable extends JTable {
	
	/**
	 * Create an Enhanced table with a default sorter and cell renderer, and then auto size the columns.
	 * Note: this is pretty dangerous as the "default" renderer may not be appropriate and result in weird behavior from the auto-sizer.
	 * @param model The table model.
	 * @return A fully initialized EnhancedTable.
	 */
	@Deprecated
	public static EnhancedTable createWithSorterAndAutoSize(TableModel model) {
		EnhancedTable enhancedTable = createWithSorter(model);
		
		enhancedTable.setDefaultRenderer(Object.class, new MyRenderer());
		enhancedTable.autoSizeColumns();
		
		return enhancedTable;
	}

	public static EnhancedTable createWithSorter(TableModel model) {
		EnhancedTable enhancedTable = createBare(model);
		
		TableSorter sort = new TableSorter(model);
		sort.setTableHeader(enhancedTable.getTableHeader());
		enhancedTable.setModel(sort);
		
		return enhancedTable;
	}
	
	/**
	 * Create a "bare" enhancedTable. You need to call autoSizeColumns() yourself.
	 * @param model The table model.
	 * @return A fully initialized EnhancedTable.
	 */
	public static EnhancedTable createBare(TableModel model) {
		return new EnhancedTable(model);
	}

	private EnhancedTableHeader d_tableHeader;

	private EnhancedTable(TableModel model) {
		super(model);
		d_tableHeader = new TooltipTableHeader(model, getColumnModel(), this);
		setTableHeader(d_tableHeader);
		setBackground(Color.WHITE);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableCopyHandler.registerCopyAction(this);
	}
	
	public void setSortingStatus(int column, int order) {
		((TableSorter)getModel()).setSortingStatus(column, order);
	}

	public void autoSizeColumns() {
		if (d_tableHeader != null) {
			d_tableHeader.autoSizeColumns();
		}
		setPreferredScrollableViewportSize(getPreferredSize());
	}
	
	public EnhancedTable(TableModel model, int maxColWidth) {
		this(model);
		d_tableHeader.setMaxColWidth(maxColWidth);
		autoSizeColumns();
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		autoSizeColumns();
	}
	
	private static class MyRenderer extends DefaultTableCellRenderer {
		
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
