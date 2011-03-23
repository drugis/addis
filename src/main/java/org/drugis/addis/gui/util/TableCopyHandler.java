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

package org.drugis.addis.gui.util;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import scala.actors.threadpool.Arrays;
import sun.awt.datatransfer.DataTransferer;

public class TableCopyHandler implements Transferable, ClipboardOwner {
	private static final int IDX_HTML = 0;
	private static final int IDX_TEXT = 1;
	private final JTable d_table;

	@SuppressWarnings("serial")
	public static void registerCopyAction(final JTable jtable) {
		// Platform-independent copy-shortcut:
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		Action action = new AbstractAction("copy") {
			public void actionPerformed(ActionEvent event) {
				TableCopyHandler hander = new TableCopyHandler(jtable);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(hander, hander);
			}
		};
		jtable.registerKeyboardAction(action, copy, JComponent.WHEN_FOCUSED);
	}

	public TableCopyHandler(JTable jtable) {
		d_table = jtable;	
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		switch (getSupportedFlavors().indexOf(flavor)) {
			case IDX_HTML:
				return getTableAsHtml();
			case IDX_TEXT:
				return getTableAsText();
			default:
				throw new UnsupportedFlavorException(flavor);
		}
	}

	public DataFlavor[] getTransferDataFlavors() {
		try {
			DataFlavor htmlFlavor = new DataFlavor("text/html;class=java.lang.String;charset=" + 
					DataTransferer.getInstance().getDefaultUnicodeEncoding());
			DataFlavor textFlavor = new DataFlavor("text/plain;class=java.lang.String;charset=" + 
					DataTransferer.getInstance().getDefaultUnicodeEncoding());
			return new DataFlavor[] {
					htmlFlavor, textFlavor
			};
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not create text/html DataFlavor", e);
		}
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return getSupportedFlavors().contains(flavor);
	}

	public void lostOwnership(Clipboard arg0, Transferable arg1) {
	}
	
	private interface TableStringBuilder {
		public void startTable();
		public void endTable();
		public void startRow();
		public void endRow();
		public void headerCell(String contents);
		public void dataCell(String contents);
	}
	
	private static class TextTableStringBuilder implements TableStringBuilder {
		private StringBuilder d_builder = new StringBuilder();
		public void startTable() {}
		public void endTable() {}
		public void startRow() {}
		public void endRow() {
			d_builder.append("\n");
		}
		public void headerCell(String contents) {
			dataCell(contents);
		}
		public void dataCell(String contents) {
			if (d_builder.length() > 0 && d_builder.charAt(d_builder.length() - 1) != '\n') {
				d_builder.append("\t");
			}
			d_builder.append(contents);
		}
		public String toString() {
			return d_builder.toString();
		}
	}
	
	public static class HtmlTableStringBuilder implements TableStringBuilder {
		private StringBuilder d_builder = new StringBuilder();
		public void startTable() {
			d_builder.append("<table>");
		}
		public void endTable() {
			d_builder.append("</table>");
		}
		public void startRow() {
			d_builder.append("<tr>");
		}
		public void endRow() {
			d_builder.append("</tr>");
		}
		public void headerCell(String contents) {
			d_builder.append("<th>" + contents + "</th>");
		}
		public void dataCell(String contents) {
			d_builder.append("<td>" + contents + "</td>");
		}
		public String toString() {
			return d_builder.toString();
		}
	}

	private Object getTableAsText() {
		TableStringBuilder builder = new TextTableStringBuilder();
		return buildTableString(builder);
	}

	private String getTableAsHtml() {
		TableStringBuilder builder = new HtmlTableStringBuilder();
		return buildTableString(builder);
	}

	private String buildTableString(TableStringBuilder builder) {
		builder.startTable();
		if (d_table.getTableHeader() != null) {
			builder.startRow();
			Enumeration<TableColumn> columns = d_table.getTableHeader().getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				builder.headerCell(columns.nextElement().getHeaderValue().toString()); 
			}
			builder.endRow();
		}
		for (int row : getRows()) {
			builder.startRow();
			for (int col = 0; col < d_table.getColumnCount(); ++col) {
				builder.dataCell(getTextAt(row, col));
			}
			builder.endRow();
		}
		builder.endTable();
		return builder.toString();
	}

	private int[] getRows() {
		if (d_table.getSelectedRowCount() == 0) {
			int count = d_table.getRowCount();
			int[] rows = new int[count];
			for (int i = 0; i < count; ++i) {
				rows[i] = i;
			}
			return rows;
		}
		return d_table.getSelectedRows();
	}

	private String getTextAt(int row, int col) {
		Component c = getRendererComponent(row, col);
		if (c instanceof JLabel) {
			return ((JLabel)c).getText();
		}
		return d_table.getValueAt(row, col).toString();
	}

	@SuppressWarnings("unchecked")
	private List<DataFlavor> getSupportedFlavors() {
		return Arrays.asList(getTransferDataFlavors());
	}

	private Component getRendererComponent(int row, int column) {
		TableCellRenderer renderer = d_table.getCellRenderer(row, column);
		return d_table.prepareRenderer(renderer, row, column);
	}
}
