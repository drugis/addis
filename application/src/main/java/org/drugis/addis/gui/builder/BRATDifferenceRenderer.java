/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.presentation.BRATTableModel.BRATDifference;

public class BRATDifferenceRenderer extends DistributionQuantileCellRenderer {
	private static final long serialVersionUID = 3342307695543623211L;

	TableCellRenderer d_altRenderer = new DefaultTableCellRenderer();
	
	Color d_selectGreen = new Color(0, 131, 0);
	Color d_unselectGreen = new Color(0, 195, 0);
	Color d_selectRed = new Color(134, 32, 41);
	Color d_unselectRed = new Color(255, 48, 48);
	
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
			
			Color green = isSelected ? d_selectGreen : d_unselectGreen;
			Color red = isSelected ? d_selectRed : d_unselectRed;
			
			if (diff.getOutcomeMeasure().getDirection().equals(Direction.HIGHER_IS_BETTER)) {
				renderer.setBackground(d.getQuantile(0.5) > 1 ? green : red);
			} else {
				renderer.setBackground(d.getQuantile(0.5) < 1 ? green : red);
			}
			return renderer;
		}
		return d_altRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
