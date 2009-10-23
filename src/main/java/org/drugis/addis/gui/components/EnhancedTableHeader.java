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
public final class EnhancedTableHeader extends JTableHeader {
	private final JTable d_table;

	public EnhancedTableHeader(TableColumnModel cm, JTable table) {
		super(cm);
		this.d_table = table;
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				doMouseClicked(e);
			}
		});
	}

	public int getRequiredColumnWidth(TableColumn column) {
		int modelIndex = column.getModelIndex();
		TableCellRenderer renderer;
		Component component;
		int requiredWidth = 0;
		int rows = d_table.getRowCount();
		for (int i = 0; i < rows; i++) {
			renderer = d_table.getCellRenderer(i, modelIndex);
			Object valueAt = d_table.getValueAt(i, modelIndex);
			component = renderer.getTableCellRendererComponent(d_table, valueAt, false, false, i, modelIndex);
			requiredWidth = Math.max(requiredWidth, component.getPreferredSize().width + 2);
		}
		return requiredWidth;
	}

	/**
	 * Autosizes all columns to fit to width of their data.
	 */
	 public void autoSizeColumns() {
		TableColumnModel tableColumnModel = d_table.getColumnModel();
		int col_count = tableColumnModel.getColumnCount();

		for (int i = 0; i < col_count; i++) {
			TableColumn col = tableColumnModel.getColumn(i);
			col.setMinWidth(this.getRequiredColumnWidth(col));
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
		 column.setMinWidth(getRequiredColumnWidth(column));
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
