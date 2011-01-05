package org.drugis.addis.gui.builder;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.presentation.SummaryCellRenderer;

@SuppressWarnings("serial")
public class DistributionQuantileCellRenderer extends DefaultTableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Distribution) {
			Distribution d = (Distribution)value;
			String str = SummaryCellRenderer.format(d.getQuantile(0.5)) + " (" + 
				SummaryCellRenderer.format(d.getQuantile(0.025)) + ", " + 
				SummaryCellRenderer.format(d.getQuantile(0.975)) + ")";
			return super.getTableCellRendererComponent(table, str, isSelected, hasFocus, row, column);
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
