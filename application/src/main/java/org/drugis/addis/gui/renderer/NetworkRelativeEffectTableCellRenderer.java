package org.drugis.addis.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.presentation.NetworkRelativeEffectTableModel;

public class NetworkRelativeEffectTableCellRenderer extends SummaryCellRenderer implements TableCellRenderer {
	public NetworkRelativeEffectTableCellRenderer(boolean applyExpTransform) {
		super(applyExpTransform);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table,
			Object cellContents, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component component =  super.getTableCellRendererComponent(
				table, cellContents, isSelected, hasFocus, row, column);

		if (cellContents instanceof DrugSet) {
			String text = ((DrugSet) cellContents).getLabel();
			component = (new DefaultTableCellRenderer()).getTableCellRendererComponent(
					table, text, isSelected, hasFocus, row, column);
			component.setBackground(Color.LIGHT_GRAY);
		}
		
		NetworkRelativeEffectTableModel networkTableModel = (NetworkRelativeEffectTableModel)table.getModel();
		((JComponent) component).setToolTipText(networkTableModel.getDescriptionAt(row, column));
		return component;
	}
}