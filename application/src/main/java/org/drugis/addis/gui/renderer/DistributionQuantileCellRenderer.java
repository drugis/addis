/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

package org.drugis.addis.gui.renderer;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.mtc.gui.results.SummaryCellRenderer;

@SuppressWarnings("serial")
public class DistributionQuantileCellRenderer extends DefaultTableCellRenderer {
	
	private final boolean d_useTwoLines;
	public DistributionQuantileCellRenderer(boolean useTwoLines) {
		d_useTwoLines = useTwoLines;
		
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	public DistributionQuantileCellRenderer() {
		this(false);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Distribution) {
			Distribution d = (Distribution)value;
			String str = "<html><center>" + SummaryCellRenderer.format(d.getQuantile(0.5)) + 
				(d_useTwoLines ? "<br>" : " ") + "(" + 
				SummaryCellRenderer.format(d.getQuantile(0.025)) + ",&nbsp;" + 
				SummaryCellRenderer.format(d.getQuantile(0.975)) + ")" + "</center></html>";
			JComponent renderer = (JComponent) super.getTableCellRendererComponent(table, str, isSelected, hasFocus, row, column);
			renderer.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
			return renderer;
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
