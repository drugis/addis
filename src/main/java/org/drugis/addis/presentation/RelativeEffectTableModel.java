package org.drugis.addis.presentation;

import javax.swing.table.TableModel;

public interface RelativeEffectTableModel extends TableModel {

	/**
	 * Return description for cell (row, col). Null if no tooltip should be displayed.
	 * @param row Row index.
	 * @param col Column index.
	 * @return description or null.
	 */
	public abstract String getDescriptionAt(int row, int col);

	public String getTitle();

	public String getDescription();

	public abstract ForestPlotPresentation getPlotPresentation(int row, int column);

}