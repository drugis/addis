/**
 * 
 */
package org.drugis.addis.gui.builder;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.forestplot.ForestPlot;
import org.drugis.addis.forestplot.LogScale;
import org.drugis.addis.forestplot.RelativeEffectBar;
import org.drugis.addis.presentation.ForestPlotPresentation;
import org.drugis.addis.presentation.BRATTableModel.BRATForest;

final class BRATForestCellRenderer<PresentationType> implements TableCellRenderer {
	public static final int PADDING = 20;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final BRATForest forest = (BRATForest) value;
		Canvas canvas = new Canvas() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				if (forest == null || forest.scale == null) {
					return;
				}
				g.translate(PADDING, 0);
				g.setColor(Color.BLACK);
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
		return canvas;
	}
}