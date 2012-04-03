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

final class BRATForestCellRenderer<PresentationType> extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -6339099621262904161L;
	public static final int PADDING = 20;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component superRenderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		final Color bg = superRenderer.getBackground();
		final Color fg = superRenderer.getForeground();
		
		final BRATForest forest = (BRATForest) value;
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				g.setColor(bg);
				Rectangle bounds = g.getClipBounds();
				g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
				
				if (forest == null || forest.scale == null) {
					return;
				}
				g.translate(PADDING, 0);
				g.setColor(fg);
				if (forest.ci != null) {
					final RelativeEffectBar bar = new RelativeEffectBar(forest.scale, ForestPlot.ROWVCENTER, forest.ci, ForestPlot.ROWHEIGHT / 3, forest.vt instanceof ContinuousVariableType);
					bar.paint((Graphics2D) g);
					int originX = forest.scale.getBin(forest.scale.getScale() instanceof LogScale ? 1D : 0D).bin;
					g.drawLine(originX, 1 - ForestPlot.ROWPAD, originX, ForestPlot.FULLROW);
				} else if (forest.axis != null) {
					g.drawLine(forest.scale.getBin(forest.axis.getMin()).bin, 1, forest.scale.getBin(forest.axis.getMax()).bin, 1);
					ForestPlot.drawAxisTicks(g, 1, ForestPlotPresentation.getTicks(forest.scale, forest.axis), ForestPlotPresentation.getTickVals(forest.scale, forest.axis));
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
		};
		
		return panel;
	}
}