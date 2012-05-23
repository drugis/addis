package org.drugis.addis.gui.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;

public abstract class GraphComponent extends JComponent {
	private static final long serialVersionUID = -2929257889835014286L;
	
	protected static final int DEFAULT_LINE_WIDTH = 2;
	protected static final Color DEFAULT_COLOR = Color.BLACK;
	
	protected int d_lineWidth;
	protected Color d_color;

	public GraphComponent(Dimension gridCellSize) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR);
	}
	
	public GraphComponent(Dimension gridCellSize, int lineWidth, Color color) {
		super();
		
		d_color = color;
		d_lineWidth = lineWidth;
		
		setPreferredSize(gridCellSize);
		setMinimumSize(gridCellSize);
		setMaximumSize(gridCellSize);
		revalidate();
	}
}