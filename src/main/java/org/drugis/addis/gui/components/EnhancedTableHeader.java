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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class EnhancedTableHeader extends JTableHeader {
	private static final int MAX_COL_WIDTH = 150;
	private final JTable d_table;

	public EnhancedTableHeader(TableColumnModel cm, JTable table) {
		super(cm);
		this.d_table = table;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				doMouseClicked(e);
			}
		});
	}

	public static int getRequiredColumnWidth(JTable table, TableColumn column) {
		int modelIndex = column.getModelIndex();
		TableCellRenderer renderer;
		Component component;
		int requiredWidth = 0;
		int rows = table.getRowCount();
		if (column.getHeaderValue() != null) {
			requiredWidth = (int) column.getHeaderValue().toString().length()*7;
		}
		for (int i = 0; i < rows; i++) {
			renderer = table.getCellRenderer(i, modelIndex);
			Object valueAt = table.getValueAt(i, modelIndex);
			component = renderer.getTableCellRendererComponent(table, valueAt, false, false, i, modelIndex);
			
			requiredWidth = Math.max(requiredWidth, component.getPreferredSize().width + 25);
		}
		return requiredWidth;
	}

	/**
	 * Autosizes all columns to fit to width of their data or max. length.
	 */
	 public void autoSizeColumns() {
		autoSizeColumns(d_table);
	 }
	 
	public static void autoSizeColumns(JTable table) {
		autoSizeColumns(table, MAX_COL_WIDTH);
	}

	public static void autoSizeColumns(JTable table, int maxColWidth) {
		int col_count = table.getModel().getColumnCount();
		
		for (int i = 0; i < col_count; i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			int requiredColumnWidth = getRequiredColumnWidth(table, col);
			if (requiredColumnWidth > maxColWidth) {
				requiredColumnWidth = maxColWidth;
			}
			col.setMinWidth(requiredColumnWidth);
		}
	}

	 public void doMouseClicked(MouseEvent e) {
		 if (!getResizingAllowed()) {
			 return;
		 }
		 if (e.getClickCount() != 2) {
			 return;
		 }
		 TableColumn column = getResizingColumn(e.getPoint(), columnAtPoint(e.getPoint()));
		 if (column == null) {
			 return;
		 }
		 int oldMinWidth = column.getMinWidth();
		 column.setMinWidth(getRequiredColumnWidth(d_table, column));
		 setResizingColumn(column);
		 d_table.doLayout();
		 column.setMinWidth(oldMinWidth);
	 }

	 private TableColumn getResizingColumn(Point p, int column) {
		 if (column == -1) {
			 return null;
		 }
		 Rectangle r = getHeaderRect(column);
		 r.grow(-3, 0);
		 if (r.contains(p)) {
			 return null;
		 }
		 int midPoint = r.x + (r.width / 2);
		 int columnIndex;
		 if (getComponentOrientation().isLeftToRight()) {
			 columnIndex = (p.x < midPoint) ? (column - 1) : column;
		 } else {
			 columnIndex = (p.x < midPoint) ? column : (column - 1);
		 }
		 if (columnIndex == -1) {
			 return null;
		 }
		 return getColumnModel().getColumn(columnIndex);
	 }
}
