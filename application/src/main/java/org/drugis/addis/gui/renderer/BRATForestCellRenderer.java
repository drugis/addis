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

package org.drugis.addis.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.forestplot.ForestPlot;
import org.drugis.addis.forestplot.LogScale;
import org.drugis.addis.forestplot.RelativeEffectBar;
import org.drugis.addis.presentation.ForestPlotPresentation;
import org.drugis.addis.presentation.BRATTableModel.BRATForest;

public class BRATForestCellRenderer<PresentationType> extends DefaultTableCellRenderer {
	public static final class ForestPlotTableCell extends JPanel {
		private final Color d_fg;
		private final BRATForest d_forest;
		private final Color d_bg;
		private static final long serialVersionUID = 1L;

		public ForestPlotTableCell(BRATForest forest, Color bg, Color fg) {
			d_forest = forest;
			d_bg = bg;
			d_fg = fg;
		}

		@Override
		public void paintComponent(Graphics g) {
			g.setColor(d_bg);
			Rectangle bounds = g.getClipBounds();
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			
			if (d_forest == null || d_forest.scale == null) {
				return;
			}
			g.translate(PADDING, 0);
			g.setColor(d_fg);
			if (d_forest.ci != null) {
				final RelativeEffectBar bar = new RelativeEffectBar(d_forest.scale, ForestPlot.ROWVCENTER, d_forest.ci, ForestPlot.ROWHEIGHT / 3, d_forest.vt instanceof ContinuousVariableType);
				bar.paint((Graphics2D) g);
				int originX = d_forest.scale.getBin(d_forest.scale.getScale() instanceof LogScale ? 1D : 0D).bin;
				g.drawLine(originX, 1 - ForestPlot.ROWPAD, originX, ForestPlot.FULLROW);
			} else if (d_forest.axis != null) {
				g.drawLine(d_forest.scale.getBin(d_forest.axis.getMin()).bin, 1, d_forest.scale.getBin(d_forest.axis.getMax()).bin, 1);
				ForestPlot.drawAxisTicks(g, 1, ForestPlotPresentation.getTicks(d_forest.scale, d_forest.axis), ForestPlotPresentation.getTickVals(d_forest.scale, d_forest.axis));
			}
			g.translate(-PADDING, 0);
		}

		@Override
		public Dimension getSize() {
			return new Dimension(ForestPlot.BARWIDTH, ForestPlot.ROWHEIGHT);
		}

		@Override
		public Dimension getPreferredSize() {
			return getSize();
		}

		@Override
		public Dimension getMinimumSize() {
			return getSize();
		}
	}

	private static final long serialVersionUID = -6339099621262904161L;
	public static final int PADDING = 20;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component superRenderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		final Color bg = superRenderer.getBackground();
		final Color fg = superRenderer.getForeground();
		
		final BRATForest forest = (BRATForest) value;
		JPanel panel = new ForestPlotTableCell(forest, bg, fg);
		
		return panel;
	}
}