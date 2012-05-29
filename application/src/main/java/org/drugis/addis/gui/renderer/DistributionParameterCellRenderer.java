package org.drugis.addis.gui.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.drugis.addis.entities.relativeeffect.GaussianBase;

public class DistributionParameterCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1988180153398796561L;

	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof GaussianBase) {
			GaussianBase d = (GaussianBase)value;
			String str = SummaryCellRenderer.format(d.getMu()) + " \u00B1 " + SummaryCellRenderer.format(d.getSigma());
			return super.getTableCellRendererComponent(table, str, isSelected, hasFocus, row, column);
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}