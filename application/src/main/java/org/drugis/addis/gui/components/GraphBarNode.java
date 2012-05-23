package org.drugis.addis.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.SwingConstants;

public class GraphBarNode extends GraphComponent implements SwingConstants {

	private static final long serialVersionUID = -8687904576467821237L;

	public GraphBarNode(Dimension gridCellSize) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR);
	}
	
	public GraphBarNode(Dimension gridCellSize, int lineWidth, Color color) {
		super(gridCellSize, lineWidth, color);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;  
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Dimension s = getSize();
		g2.setColor(d_color);
		g2.fillRect(0, 0, (int)s.getWidth(), (int)s.getHeight());
	}
}

