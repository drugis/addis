/**
 * 
 */
package org.drugis.addis.gui.components;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.drugis.addis.presentation.wizard.MissingMeasurementPresentation;

public class MeasurementCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 5755993653824290803L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return super.getTableCellRendererComponent(table, ((MissingMeasurementPresentation)value).getDescription(), isSelected, hasFocus, row, column);
	}
}