package org.drugis.addis.gui.builder;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.presentation.BRATTableModel.BRATDifference;

public class BRATDifferenceRenderer extends DistributionQuantileCellRenderer {
	private static final long serialVersionUID = 3342307695543623211L;

	public BRATDifferenceRenderer() {
		super(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof BRATDifference) {
			BRATDifference diff = (BRATDifference) value;
			Distribution d = diff.getDifference();
			Component renderer = super.getTableCellRendererComponent(table, d, isSelected, hasFocus, row, column);
			
			if (diff.getOutcomeMeasure().getDirection().equals(Direction.HIGHER_IS_BETTER)) {
				renderer.setBackground(d.getQuantile(0.5) > 1 ? Color.GREEN : Color.RED);
			} else {
				renderer.setBackground(d.getQuantile(0.5) < 1 ? Color.GREEN : Color.RED);
			}
			return renderer;
		}
		Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value == null) {
			renderer.setBackground(Color.WHITE);
		}
		return renderer ;
	}

}
