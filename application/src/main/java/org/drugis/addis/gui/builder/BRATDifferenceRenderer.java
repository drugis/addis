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
