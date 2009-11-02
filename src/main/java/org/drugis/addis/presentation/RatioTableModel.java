package org.drugis.addis.presentation;

import javax.swing.table.TableModel;

public interface RatioTableModel extends TableModel {

	/**
	 * Return description for cell (row, col). Null if no tooltip should be displayed.
	 * @param row Row index.
	 * @param col Column index.
	 * @return description or null.
	 */
	public abstract String getDescriptionAt(int row, int col);

}