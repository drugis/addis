package org.drugis.addis.gui.components.progressgraph;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.SwingConstants;

public class GraphConnector extends GraphComponent implements SwingConstants {
	private static final long serialVersionUID = 3271331606688687191L;
	private final int d_numberOfChains;
	private final int d_cellHeight;
	private final int d_totalHeight;

	public GraphConnector(Dimension gridCellSize, int cellHeight, int totalHeight, int numberOfChains) {
		super(gridCellSize);
		d_cellHeight = cellHeight;
		d_totalHeight = totalHeight;
		d_numberOfChains = numberOfChains;
	}


	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(d_color);
		g2.setStroke(new BasicStroke((int)d_lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		double w = getSize().getWidth();
//		double h = getSize().getHeight();
		double step = w / (d_numberOfChains + 1);
		
		for (int i = 0; i < d_numberOfChains; ++i) {
			int hch = d_cellHeight / 2;
			double x = step + step * i;
			double y = hch + d_cellHeight * i;
			
			g2.drawLine((int)x, (int)y, (int)x, d_totalHeight);
		}
	}

}

