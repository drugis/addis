package org.drugis.addis.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.SwingConstants;

public class GraphSimpleNode extends GraphComponent implements SwingConstants {
	private static final long serialVersionUID = 7712060678575254850L;
	
	public enum GraphSimpleNodeType {
		START, END, DECISION
	}

	private GraphSimpleNodeType d_type;
	
	public GraphSimpleNode(Dimension gridCellSize, GraphSimpleNodeType type) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR, type);
	}
	
	public GraphSimpleNode(Dimension gridCellSize, int lineWidth, Color color, GraphSimpleNodeType type) {
		super(gridCellSize, lineWidth, color);

//		revalidate();
		
		d_type = type;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;  
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Dimension s = getSize();
		g2.setColor(d_color);
		g2.setStroke(new BasicStroke((float)d_lineWidth));
		
		switch (d_type) {
		case START:
			g2.fillOval(0, 0, (int)s.getWidth(), (int)s.getHeight());
			break;
		case END:
//			double factor = 0.8;
//			double invFactor = 1 - factor;
//			Rectangle2D scaledDims = new Rectangle2D.Double(
//					s.getWidth() * invFactor / 2, s.getHeight() * invFactor / 2,
//					s.getWidth() / 2 * factor, s.getHeight() * factor); 
//			g2.fillOval((int)scaledDims.getMinX(), (int)scaledDims.getMinY(), (int)scaledDims.getWidth(), (int)scaledDims.getHeight());
//			g2.drawOval(0, 0, (int)s.getWidth(), (int)s.getHeight());
			break;
		case DECISION:
			break;
		}
	}
}

